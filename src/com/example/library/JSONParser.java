package com.example.library;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class JSONParser {
	static InputStream is = null;
	static JSONObject jObj = null;
	static String json = "";
	
	public JSONParser(){
		
	}
	
	public JSONObject getJSONFromUrl(String url, JSONObject params){
		//Log.d("ASSHOLE",url);
		Log.d("FUCKFUCK",params.toString());
		try {
			String params_string = "";
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url);
			params_string = params.toString();
			StringEntity se = new StringEntity(params_string);
			httpPost.setEntity(se);
			httpPost.setHeader("Accept","application/json");
			httpPost.setHeader("Content-type","application/json");
			HttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity httpEntity = httpResponse.getEntity();
			is = httpEntity.getContent();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e){
			e.printStackTrace();
		} catch (IOException e){
			e.printStackTrace();
		}
		try {
			BufferedReader reader = new BufferedReader (new InputStreamReader(is, "iso-8859-1"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine())!=null){
				Log.d("Reading",line);
				sb.append(line);
			}
			is.close();
			json = sb.toString();
			Log.e("JSON", json);
		} catch (Exception e) {
			Log.e("Buffer Error","Error converting result " + e.toString());
		}
		
		try {
			jObj = new JSONObject(json);
		} catch (JSONException e) {
			Log.e("JSON Parser", "Error parsing data" + e.toString());
		}
		Log.d("jObj",jObj.toString());
		return jObj;
		
	}
	
	
	
	

}
