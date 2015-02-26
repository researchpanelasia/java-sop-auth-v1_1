package com.surveyon.partners.v1_1;

import java.util.TreeMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class Example {

	public static void main(String[] args) {		        
		HttpClient httpclient = new DefaultHttpClient();
		HttpResponse response;

		try {
			TreeMap param = new TreeMap();
			param.put("time", "1424925486");
			param.put("app_id",  args[0]);
			param.put("from_date", "2015-1-20");
			param.put("to_date", "2015-1-21");

			HttpGet httpget = new HttpGet(args[2] + "?" + SurveyonPartnersAuth.generateQuery(param, args[1]));

			System.out.println("executing request " + httpget.getURI());
			response = httpclient.execute(httpget);
			HttpEntity entity = response.getEntity();

			System.out.println("----------------------------------------");
			System.out.println(response.getStatusLine());
			if (entity != null) {
				System.out.println("Response content length: " + entity.getContentLength());
				System.out.println("Response content length: " + EntityUtils.toString(entity));
			}
			System.out.println("----------------------------------------");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
