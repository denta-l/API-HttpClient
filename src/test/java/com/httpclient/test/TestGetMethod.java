package com.httpclient.test;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.api.helper.HttpClientHelper;
import com.api.util.HttpResponseBody;
import com.api.util.HttpResponseMessage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class TestGetMethod {
	
	@Test
	public void getPingAlive() {
		String url = "http://localhost:8080/laptop-bag/webapi/api/ping/Hello";
		
		HttpResponseMessage response = HttpClientHelper.performGetRequest(url, null);
		Assert.assertEquals(response.getStatusCode(), HttpStatus.SC_OK);
		Assert.assertEquals(response.getResponseBody(), "Hi! Hello");
		System.out.println(response.getResponseBody());
	}
	
	
	@Test
	public void getAll() {
		String url = "http://localhost:8080/laptop-bag/webapi/api/all";
		
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Accept", "application/json");
		
		HttpResponseMessage response = HttpClientHelper.performGetRequest(url, headers);
		Assert.assertTrue(HttpStatus.SC_OK == response.getStatusCode() || HttpStatus.SC_NO_CONTENT == response.getStatusCode());
		System.out.println(response.getResponseBody());
		
	}
	
	@Test
	public void getWithID() {
		String url = "http://localhost:8080/laptop-bag/webapi/api/find/844";
		
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Accept", "application/json");
		
		HttpResponseMessage response = HttpClientHelper.performGetRequest(url, headers);
		Assert.assertTrue((HttpStatus.SC_OK == response.getStatusCode() || HttpStatus.SC_NOT_FOUND == response.getStatusCode()),"Expected status code not found");
		System.out.println(response.getResponseBody());
		
		GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.serializeNulls().setPrettyPrinting().create();
		HttpResponseBody body = gson.fromJson(response.getResponseBody(), HttpResponseBody.class);
		
		Assert.assertEquals("Dell", body.BrandName);
		Assert.assertEquals("126", body.Id);
		Assert.assertEquals("Latitude", body.LaptopName);
		
		
		
		
	}

}
