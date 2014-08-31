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


public class LoginActivity extends Activity {
	
	Button btnLogin;
	EditText inputUsername;
	EditText inputPassword;
	private TextView loginErrorMsg;
	
	private static String KEY_SUCCESS = "success";
	private static String KEY_UID = "uid";
	private static String KEY_USERNAME = "uname";
	private static String KEY_FIRSTNAME = "fname";
	private static String KEY_LASTNAME = "lname";
	private static String KEY_EMAIL = "email";
	private static String KEY_CREATED_AT = "created_at";
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        inputUsername = (EditText) findViewById(R.id.uname);
        inputPassword = (EditText) findViewById(R.id.pword);
        btnLogin = (Button) findViewById(R.id.login);
        loginErrorMsg = (TextView) findViewById(R.id.loginErrorMsg);
        TextView registerScreen = (TextView) findViewById(R.id.link_to_register);
        
        registerScreen.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(), RegisterActivity.class);
				startActivity(i);
				// TODO Auto-generated method stub
			}
		});
        
        btnLogin.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if ((!inputUsername.getText().toString().equals(""))&&(!inputPassword.getText().equals("")))
				{
					NetAsync(v);
				}
				else if ((!inputUsername.getText().toString().equals("")))
				{
					Toast.makeText(getApplicationContext(), "Password field empty", Toast.LENGTH_SHORT).show();
				}
				else if ((!inputPassword.getText().toString().equals("")))
				{
					Toast.makeText(getApplicationContext(), "Email field empty", Toast.LENGTH_SHORT).show();
				}
				else
				{
					Toast.makeText(getApplicationContext(), "Email and Password field are empty", Toast.LENGTH_SHORT).show();
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
    		nDialog = new ProgressDialog(LoginActivity.this);
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
						return true;
					}
				} catch (MalformedURLException e1){
					e1.printStackTrace();
				} catch (IOException e){
					e.printStackTrace();
				}
			}
			return false;
		}
    	
		protected void onPostExecute(Boolean th){
			if(th == true){
				nDialog.dismiss();
				new ProcessLogin().execute();
			}
			else{
				nDialog.dismiss();
				loginErrorMsg.setText("Error in Network Connection");
			}
			
			
		}
    	
    }
    
    private class ProcessLogin extends AsyncTask <Object, String, JSONObject> {
    	private ProgressDialog pDialog;
    	String uname,password;
    	
    	protected void onPreExecute(){
    		super.onPreExecute();
    		inputUsername = (EditText) findViewById(R.id.uname);
    		inputPassword = (EditText) findViewById(R.id.pword);
    		uname = inputUsername.getText().toString();
    		password = inputPassword.getText().toString();
    		pDialog = new ProgressDialog(LoginActivity.this);
    		pDialog.setTitle("Contacting Servers");
    		pDialog.setMessage("Logging in ...");
    		pDialog.setIndeterminate(false);
    		pDialog.setCancelable(true);
    		pDialog.show();
    	}

		@Override
		protected JSONObject doInBackground(Object... params) {
			UserFunctions userFunction = new UserFunctions();
			JSONObject json = new JSONObject();
			try {
				json = userFunction.loginUser(uname, password);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return json;
		}
		
		protected void onPostExecute(JSONObject json){
			try {
				if (json.getString(KEY_SUCCESS) != null) {
					String res = json.getString(KEY_SUCCESS);
					if(res == "true"){
						pDialog.setMessage("Loading User Space");
						pDialog.setTitle("Getting Data");
						DatabaseHandler db = new DatabaseHandler(getApplicationContext());
						//JSONObject json_user = json.getJSONObject("user");
						
						UserFunctions logout = new UserFunctions();
						logout.logoutUser(getApplicationContext());
						db.addUser(uname);
						Log.d("Username",db.getUsername());
						Intent upanel = new Intent(getApplicationContext(), Main_Activity.class);
						upanel.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						pDialog.dismiss();
						startActivity(upanel);
						
						finish();
					}else{
						pDialog.dismiss();
						loginErrorMsg.setText("Incorrect username/password");
					}
				}
			} catch (JSONException e){
				e.printStackTrace();
			}
		}	
    }
    public void NetAsync(View view){
    	new NetCheck().execute();
    }
}
