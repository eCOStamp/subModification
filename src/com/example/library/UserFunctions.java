package com.example.library;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

public class UserFunctions {
	private JSONParser jsonParser;
	
	private static String commonURL = "http://ecostamp.aosekai.net/api/";
	private static String login_tag = "login";
	private static String register_tag = "register";
	
	public UserFunctions() {
		jsonParser = new JSONParser();
	}
	
	public JSONObject loginUser(String username, String password) throws JSONException {
		
		/*List params = new ArrayList();
		params.add(new BasicNameValuePair("tag", login_tag));
		params.add(new BasicNameValuePair("\"email\"", "\""+email+"\""));
        params.add(new BasicNameValuePair("password", password));*/
        JSONObject para_json = new JSONObject();
        para_json.put("username",username);
        para_json.put("password", password);
        JSONObject json = jsonParser.getJSONFromUrl(commonURL+"authenticate/", para_json);
		return json;
	}
	
	public JSONObject registerUser(String fname, String lname, String email, String uname, String password) throws JSONException{
		//List params = new ArrayList();
		//params.add(new BasicNameValuePair("tag", register_tag));
		//params.add(new BasicNameValuePair("fname", fname));
        //params.add(new BasicNameValuePair("lname", lname));
        //params.add(new BasicNameValuePair("username", uname));
        //params.add(new BasicNameValuePair("email", email));
        //params.add(new BasicNameValuePair("password", password));
        JSONObject para_json = new JSONObject();
        para_json.put("username", uname);
        para_json.put("email", email);
        para_json.put("password", password);
        JSONObject json = jsonParser.getJSONFromUrl(commonURL+"register/",para_json);
        return json;
	}
	
	public boolean logoutUser(Context context){
		DatabaseHandler db = new DatabaseHandler(context);
		db.resetTables();
		return true;
	}
	
}
