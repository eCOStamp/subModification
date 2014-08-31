package com.example.cicp_application;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.library.DatabaseHandler;
import com.example.library.UserFunctions;

public class RegisterActivity extends Activity {
	
	private static String KEY_SUCCESS = "success";
	private static String KEY_UID = "uid";
	private static String KEY_USERNAME = "uname";
	private static String KEY_FIRSTNAME = "fname";
	private static String KEY_LASTNAME = "lname";
	private static String KEY_EMAIL = "email";
	private static String KEY_CREATED_AT = "created_at";
	private static String KEY_ERROR = "error";
	
	EditText inputFirstName;
	EditText inputLastName;
	EditText inputUsername;
	EditText inputEmail;
	EditText inputPassword;
	Button btnRegister;
	TextView registerErrorMsg;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);
		
		inputFirstName = (EditText) findViewById(R.id.fname);
		inputLastName = (EditText) findViewById(R.id.lname);
		inputUsername = (EditText) findViewById(R.id.uname);
		inputEmail = (EditText) findViewById(R.id.email);
		inputPassword = (EditText) findViewById(R.id.pword);
		btnRegister = (Button) findViewById(R.id.btnRegister);
		registerErrorMsg = (TextView) findViewById(R.id.register_error);
		
		TextView loginScreen = (TextView) findViewById(R.id.link_to_login);
		loginScreen.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent myIntent = new Intent(getApplicationContext(), LoginActivity.class);
				startActivity(myIntent);
				// TODO Auto-generated method stub
				
			}
		});
		
		btnRegister.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if ((!inputUsername.getText().toString().equals("")) && (!inputPassword.getText().toString().equals("")) && (!inputFirstName.getText().toString().equals("")) && (!inputLastName.getText().toString().equals("")) && (!inputEmail.getText().toString().equals("")))
				{
					if (inputUsername.getText().toString().length() > 4){
						NetAsync(v);
					}
					else{
						Toast.makeText(getApplicationContext(), "Username should be minimum 5 characters", Toast.LENGTH_SHORT).show();
					}
				}
				else{
					Toast.makeText(getApplicationContext(), "One or more fields are empty", Toast.LENGTH_SHORT).show();
				}
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	private class NetCheck extends AsyncTask <String, String, Boolean>
    {
    	private ProgressDialog nDialog;
    	@Override
    	protected void onPreExecute(){
    		super.onPreExecute();
    		nDialog = new ProgressDialog(RegisterActivity.this);
    		nDialog.setTitle("Checking Network");
    		nDialog.setMessage("Loading..");
    		nDialog.setIndeterminate(false);
    		nDialog.setCancelable(true);
    		nDialog.show();
    	}
    	
    	@Override
		protected Boolean doInBackground(String... string) {
			ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo netInfo = cm.getActiveNetworkInfo();
			if (netInfo != null && netInfo.isConnected())
			{
				try{
					URL url = new URL("http://www.google.com");
					HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
					urlc.setConnectTimeout(3000);
					urlc.connect();
					if (urlc.getResponseCode() == 200){
						//Log.d("TAG", "Message");
						return true;
					}
				} catch (MalformedURLException e1){
					e1.printStackTrace();
				} catch (IOException e){
					e.printStackTrace();
				}
			}
			//Log.d("TAG", "Message");
			return false;
		}
    	
    	protected void onPostExecute(Boolean th){
    		//Log.d("FUCK", "SHIT");
    		if(th == true){
    			nDialog.dismiss();
    			new ProcessRegister().execute();
    		}
    		else{
    			nDialog.dismiss();
    			registerErrorMsg.setText("Error in Network Connection");
    		}
    	}
    }
	private class ProcessRegister extends AsyncTask <Object, String, JSONObject> {
		private ProgressDialog pDialog;
		String email,password,fname,lname,uname;
		@Override
		protected void onPreExecute(){
			super.onPreExecute();
			inputUsername = (EditText) findViewById(R.id.uname);
			inputPassword = (EditText) findViewById(R.id.pword);
			fname = inputFirstName.getText().toString();
			lname = inputLastName.getText().toString();
			email = inputEmail.getText().toString();
			uname = inputUsername.getText().toString();
			password = inputPassword.getText().toString();
			pDialog = new ProgressDialog(RegisterActivity.this);
			pDialog.setTitle("Contacting Servers");
			pDialog.setMessage("Registering ...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}
		@Override
		protected JSONObject doInBackground(Object... params) {
			UserFunctions userFunction = new UserFunctions();
			JSONObject json = new JSONObject();
			try {
				json = userFunction.registerUser(fname, lname, email, uname, password);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// TODO Auto-generated method stub
			return json;
		}
		
		protected void onPostExecute(JSONObject json){
			try {
				Log.d("RETURN", json.getString(KEY_SUCCESS));
				if (json.getString(KEY_SUCCESS) != null){
					registerErrorMsg.setText("");
					String res = json.getString(KEY_SUCCESS);
					//String red = json.getString(KEY_ERROR);
					if( res == "true"){
						pDialog.setTitle("Getting Data");
						pDialog.setMessage("Loading Info");
						registerErrorMsg.setText("Successfully Registered");
						DatabaseHandler db = new DatabaseHandler(getApplicationContext());
						//JSONObject json_user = json.getJSONObject("user");
						
						UserFunctions logout = new UserFunctions();
						logout.logoutUser(getApplicationContext());
						db.addUser(uname);
						
						Intent registered = new Intent(getApplicationContext(), Main_Activity.class);
						
						registered.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						pDialog.dismiss();
						startActivity(registered);
						finish();
					}
					/*else if (Integer.parseInt(red) == 2){
						pDialog.dismiss();
						registerErrorMsg.setText("User already exists");
					}
					else if (Integer.parseInt(red) == 3){
						pDialog.dismiss();
						registerErrorMsg.setText("Invalid Email id");
					}*/
				}
				else{
					pDialog.dismiss();
					registerErrorMsg.setText("Error occured in registration");
				}
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	public void NetAsync(View view){
		new NetCheck().execute();
	}
}

