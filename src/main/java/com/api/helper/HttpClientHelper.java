package com.api.helper;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;



import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;

import com.api.util.HttpResponseMessage;

public class HttpClientHelper {
	
	
	public static HttpResponseMessage performGetRequest(String url, Map<String, String> headers) {
		try {
			return performGetRequest(new URI(url), headers);
		} catch (URISyntaxException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	
	public static HttpResponseMessage performGetRequest(URI uri, Map<String, String> headers) {
		HttpGet httpGet = new HttpGet(uri);
		if(headers != null) {
			httpGet.setHeaders(getCustomHeaders(headers));
		}
		return performRequest(httpGet);
	}
	
	public static HttpResponseMessage performPostRequest(String url, Object content, ContentType type, Map<String, String> headers) {
		try {
			return performPostRequest(new URI(url), content, type, headers);
		} catch (URISyntaxException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	
	public static HttpResponseMessage performPostRequest(URI uri, Object content, ContentType type, Map<String, String> headers) {
		HttpPost httpPost = new HttpPost(uri);
		if(headers != null) {
			httpPost.setHeaders(getCustomHeaders(headers));
		}
		httpPost.setEntity(getHttpEntity(content, type));
		return performRequest(httpPost);
	}
	
	public static HttpResponseMessage performDeleteRequest(String url, Map<String, String> headers) {
		try {
			return performGetRequest(new URI(url), headers);
		} catch (URISyntaxException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	
	public static HttpResponseMessage performDeleteRequest(URI uri, Map<String,String> headers) {
		HttpDelete httpDelete = new HttpDelete();
		if(headers != null) {
			httpDelete.setHeaders(getCustomHeaders(headers));
		}
		return performRequest(httpDelete);
	}
	
	public static HttpResponseMessage performPutRequest(String url, Object content, ContentType type, Map<String, String> headers) {
		try {
			return performPutRequest(new URI(url), content, type, headers);
		} catch (URISyntaxException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	
	public static HttpResponseMessage performPutRequest(URI uri, Object content, ContentType type, Map<String, String> headers) {
		HttpPut httpPut = new HttpPut();
		if(headers != null) {
			httpPut.setHeaders(getCustomHeaders(headers));
		}
		return performRequest(httpPut);
	}
	
	
	/*
	 * HttpUriRequest: interface that provides methods to access request properties such as request URI and method type.
	 * Interface CloseableHttpResponse -> inherits method getStatusLine() from Interface HttpResponse
	 * class CloseableHttpClient: implements HttpClient
	 * Class BasicResponseHandler: A ResponseHandler that returns the response body as a String for successful (2xx) responses. 
	 * If the response code was >= 300,  an HttpResponseException is thrown.
	 * 
	 */
	
	private static HttpResponseMessage performRequest(HttpUriRequest httpMethod) {
		
		CloseableHttpResponse response = null;
		try (CloseableHttpClient client = HttpClientBuilder.create().build()){
			response = client.execute(httpMethod);
			ResponseHandler<String> body = new BasicResponseHandler();
			return new HttpResponseMessage(response.getStatusLine().getStatusCode(), body.handleResponse(response));
		} catch (Exception e) {
			//to handle HttpResponseException when the response code is >=300
			if(e instanceof HttpResponseException)
				return new HttpResponseMessage(response.getStatusLine().getStatusCode(), e.getMessage());
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	
	/*
	 * Interface Header -> Represents an HTTP header field.
	 * Class BasicHeader -> Implements a Header.
	 * BasicHeader Constructor Detail -> BasicHeader(String name, String value)
	 */
	
	private static Header[] getCustomHeaders(Map<String, String> headers) {
		Header[] customHeaders = new Header[headers.size()];
		int i=0;
		for(String key : headers.keySet()) {
			customHeaders[i++] = new BasicHeader(key, headers.get(key));
		}
		return customHeaders;
	}
	
	/*
	 * Interface HttpEntity -> An entity that can be sent or received with an HTTP message.
	 * Class FileEntity ->  entity that obtains its content from a file.
	 * Class StringEntity -> entity that obtains its content from a String.
	 */
	private static HttpEntity getHttpEntity(Object content, ContentType type) {
		if(content instanceof String)
			return new StringEntity ((String)content, type);
		else if(content instanceof File)
			return new FileEntity((File)content, type);
		else
			throw new RuntimeException("Entity type not found");
	}

}
