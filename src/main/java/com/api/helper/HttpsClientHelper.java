package com.api.helper;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;

import javax.net.ssl.SSLContext;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.ssl.SSLContextBuilder;

import com.api.util.HttpResponseMessage;

public class HttpsClientHelper {
	
	/*
	 * Interface TrustStrategy: used to override the standard JSSE certificate verification process.
	 * class SSLEngine: a class that enables secure communication using SSL protocol
	 * class SSLContext: Instance of this class represents a secure socket protocol implementation which acts as a factory for SSLEngine
	 * SSLContextBuilder - gives the object of SSLContext
	 */
	
	public static SSLContext getSSLContext() throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
		TrustStrategy trust = new TrustStrategy() {
			
			@Override
			public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
				// true: to override the certification verification process
				return true;
			}
		};
		return SSLContextBuilder.create().loadTrustMaterial(trust).build();
	}
	
	/*
	 * Assigns SSLContext instance.
	 */
	public static CloseableHttpClient getHttpClient(SSLContext sslContext) {
		return HttpClientBuilder.create().setSSLContext(sslContext).build();
	}
	
	
	public static HttpResponseMessage performRequest(HttpUriRequest httpRequest) {
		
		CloseableHttpResponse response = null;
		try (CloseableHttpClient client = getHttpClient(getSSLContext())){
			response = client.execute(httpRequest);
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			return new HttpResponseMessage(response.getStatusLine().getStatusCode(), responseHandler.handleResponse(response));
		} catch (Exception e) {
			if( e instanceof HttpResponseException)
				return new HttpResponseMessage(response.getStatusLine().getStatusCode(), e.getMessage());
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	
	private static HttpEntity getHttpEntity(Object content, ContentType type) {
		if(content instanceof String)
			return new StringEntity((String)content, type);
		else if(content instanceof File)
			return new FileEntity((File)content, type);
		else 
			throw new RuntimeException("Entity type not found");
	}
	
	private static Header[] getCustomHeaders(Map<String, String> headers) {
		Header[] customHeaders = new Header[headers.size()];
		int i=0;
		for(String key : headers.keySet()) {
			customHeaders[i++] = new BasicHeader(key, headers.get(key));
		}
		return customHeaders;
	}
	
	public static HttpResponseMessage performGetRequestWithSSL(String url, Map<String, String> headers) {
		try {
			return performGetRequestWithSSL(new URI(url), headers);
		} catch (URISyntaxException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	
	public static HttpResponseMessage performGetRequestWithSSL(URI uri, Map<String, String> headers) {
		HttpGet httpGet = new HttpGet();
		if(headers != null) {
			httpGet.setHeaders(getCustomHeaders(headers));
		}
		return performRequest(httpGet);
	}
	
	public static HttpResponseMessage performPostWithSSL(String url, Object content, ContentType type, Map<String, String> headers) {
		try {
			return performPostWithSSL(new URI(url), content, type, headers);
		} catch (URISyntaxException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	
	public static HttpResponseMessage performPostWithSSL(URI uri, Object content, ContentType type, Map<String, String> headers) {
		HttpUriRequest httpPost = RequestBuilder.post(uri).setEntity(getHttpEntity(content, type)).build();
		if(headers != null) {
			httpPost.setHeaders(getCustomHeaders(headers));
		}
		return performRequest(httpPost);
	}
	
	public static HttpResponseMessage performDeleteWithSSL(String url, Map<String, String> headers) {
		try {
			return performDeleteWithSSL(new URI(url), headers);
		} catch (URISyntaxException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	
	public static HttpResponseMessage performDeleteWithSSL(URI uri, Map<String, String> headers) {
		HttpUriRequest httpDelete = RequestBuilder.delete(uri).build();
		if( headers != null)
			httpDelete.setHeaders(getCustomHeaders(headers));
		return performRequest(httpDelete);
	}
	
	public static HttpResponseMessage performPutWithSSL(String url, Object content, ContentType type, Map<String, String> headers) {
		try {
			return performPutWithSSL(new URI(url), content, type, headers);
		} catch (URISyntaxException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	
	public static HttpResponseMessage performPutWithSSL(URI uri, Object content, ContentType type, Map<String, String> headers) {
		HttpUriRequest httpPut = RequestBuilder.put(uri).setEntity(getHttpEntity(content, type)).build();
		if(headers != null)
			httpPut.setHeaders(getCustomHeaders(headers));
		return performRequest(httpPut);
	}

}
