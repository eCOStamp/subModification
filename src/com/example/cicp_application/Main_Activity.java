package com.example.cicp_application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import NFC.NFC_Class;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.library.TabPagerAdapter;

public class Main_Activity extends FragmentActivity {

	String DefaultPic_Url = "http://1.bp.blogspot.com/-FmENw0I89F4/UgrC7ljp4ZI/AAAAAAAAAEo/qOt3mvdIz_U/s320/color.jpg";
	// Default Pic for image loading test

	private static final int PENDING_INTENT_TECH_DISCOVERED = 1;
	private static final int DIALOG_WRITE_URL = 1;
	private static final int DIALOG_WRITE_ERROR = 2;
	private static final int DIALOG_NEW_TAG = 3;
	private static final String ARG_MESSAGE = "message";
	private NfcAdapter mNfcAdapter;
	private EditText mMyUrl;
	private Button mMyWriteUrlButton;
	Boolean firstOpen = true;
	// private boolean mWriteUrl = false;
	ImageView img;
	TextView text;
	Bitmap bitmap;
	ProgressDialog pDialog;

	ViewPager Tab;
	TabPagerAdapter TabAdapter;
	ActionBar actionBar;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		TabAdapter = new TabPagerAdapter(getSupportFragmentManager());
		Tab = (ViewPager) findViewById(R.id.pager);
		Tab.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			public void onPageSelected(int position) {
				actionBar = getActionBar();
				actionBar.setSelectedNavigationItem(position);
			}
		});

		Tab.setAdapter(TabAdapter);
		actionBar = getActionBar();

		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		ActionBar.TabListener tabListener = new ActionBar.TabListener() {

			@Override
			public void onTabUnselected(Tab tab, FragmentTransaction ft) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onTabSelected(Tab tab, FragmentTransaction ft) {
				Tab.setCurrentItem(tab.getPosition());
				// TODO Auto-generated method stub

			}

			@Override
			public void onTabReselected(Tab tab, FragmentTransaction ft) {
				// TODO Auto-generated method stub

			}
		};

		actionBar.addTab(actionBar.newTab().setText("Collect Stamp!")
				.setTabListener(tabListener));
		actionBar.addTab(actionBar.newTab().setText("Collection")
				.setTabListener(tabListener));

	}

	// /////////////// S
	@Override
	public void onResume() {
		super.onResume();

		// Retrieve an instance of the NfcAdapter ("connection" to the NFC
		// system service):
		onResume_NFC();
	}

	// ////////// E
	/**
	 * Called when the activity loses focus.
	 */
	// //////// S
	@Override
	public void onPause() {
		super.onPause();
		onPause_NFC();
	}

	// //////// E
	/**
	 * Called when activity receives a new intent.
	 */
	// //////// S
	@Override
	public void onNewIntent(Intent data) {
		// Resolve the intent that re-invoked us:
		Resolve_NFC(data);
		// Toast.makeText(getBaseContext(), "NewIntent!!!!",
		// Toast.LENGTH_LONG).show();
	}

	// //////// E
	/**
	 * Called when a pending intent returns (e.g. our foreground dispatch).
	 */
	// //////// S
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case PENDING_INTENT_TECH_DISCOVERED:
			// Resolve the foreground dispatch intent:
			Resolve_NFC(data);
			break;
		}
	}

	// //////// E

	/**
	 * Called when a dialog is created.
	 */
	@Override
	protected Dialog onCreateDialog(int id, Bundle args) {
		switch (id) {
		case DIALOG_NEW_TAG:
			// A dialog that we show when we detected and read a tag:
			return new AlertDialog.Builder(this)
					.setTitle("Stamp Does Not exist or Network Error!!")
					.setMessage("")
					.setCancelable(true)
					.setNeutralButton(android.R.string.ok,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface d, int arg) {
									d.dismiss();
								}
							}).create();
		}

		return null;
	}

	/**
	 * Called before a dialog is shown to the user.
	 */
	@Override
	protected void onPrepareDialog(int id, Dialog dialog, Bundle args) {
		switch (id) {
		case DIALOG_WRITE_ERROR:
		case DIALOG_NEW_TAG:
			// Pass parameters to the tag detected and the write error dialog:
			if (args != null) {
				String message = args.getString(ARG_MESSAGE);
				if (message != null) {
					((AlertDialog) dialog).setMessage(message);
				}
			}
			break;
		}
	}

	// //////////////////////////////////////////////////////////////////////
	// NFC Related
	/**
	 * 
	 * @param data
	 */
	public void Resolve_NFC(Intent data) {
		NFC_Class test = new NFC_Class();
		String uri = test.resolveIntent(data, true);

		// Toast.makeText(getBaseContext(), "NFC_Message\n"+uri,
		// Toast.LENGTH_LONG).show();//for Testing

		new Main_Activity.HttpAsyncTask().execute(uri);

		firstOpen = false;

	}

	public void onResume_NFC() {

		NfcManager nfcManager = (NfcManager) this
				.getSystemService(Context.NFC_SERVICE);
		if (nfcManager != null) {
			mNfcAdapter = nfcManager.getDefaultAdapter();
		}

		if (mNfcAdapter != null) {
			// The isEnabled() method, if invoked directly after the NFC service
			// crashed, returns false regardless of the real setting (Android
			// 2.3.3+).
			// As a nice side-effect it re-establishes the link to the correct
			// instance
			// of NfcAdapter. Therefore, just execute this method twice whenever
			// we
			// re-request the NfcAdapter, so we can be sure to have a valid
			// handle.
			try {
				mNfcAdapter.isEnabled();
			} catch (NullPointerException e) {
				// Drop NullPointerException that is sometimes thrown
				// when NFC service crashed
			}
			try {
				mNfcAdapter.isEnabled();
			} catch (NullPointerException e) {
				// Drop NullPointerException that is sometimes thrown
				// when NFC service crashed
			}

			// Create a PendingIntent to handle discovery of Ndef and
			// NdefFormatable tags:
			PendingIntent pi = createPendingResult(
					PENDING_INTENT_TECH_DISCOVERED, new Intent(), 0);
			if (pi != null) {
				try {
					// Enable foreground dispatch for Ndef and NdefFormatable
					// tags:
					mNfcAdapter
							.enableForegroundDispatch(
									this,
									pi,
									new IntentFilter[] { new IntentFilter(
											NfcAdapter.ACTION_TECH_DISCOVERED) },
									new String[][] {
											new String[] { "android.nfc.tech.NdefFormatable" },
											new String[] { "android.nfc.tech.Ndef" } });
				} catch (NullPointerException e) {
					// Drop NullPointerException that is sometimes thrown
					// when NFC service crashed
				}
			}
		}
	}

	public void onPause_NFC() {
		if (mNfcAdapter != null) {
			try {
				// Disable foreground dispatch:
				mNfcAdapter.disableForegroundDispatch(this);
			} catch (NullPointerException e) {
				// Drop NullPointerException that is sometimes thrown
				// when NFC service crashed
			}
		}
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////
	// Server Connection related
	private class HttpAsyncTask extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... results) {
			// What to do in background
			// Ex. Call Server
			return POST("http://ecostamp.aosekai.net/api/stamp/"+results[0]+"/", "");
		}

		@Override
		protected void onPostExecute(String result) {
			// What to do after background process is done
			// Ex. Prints result

			img = (ImageView) findViewById(R.id.img); // Change if need
			if (result.contains("Exception_Catched!")
					|| result.contains("Did not work!")) {

				img.setImageResource(R.drawable.error);
				// new LoadImage().execute(DefaultError_Url); //For Testing
				StringBuilder tagInfo = new StringBuilder();
				tagInfo.append("Result: \"");
				tagInfo.append(result);
				tagInfo.append("\n");
				Bundle args = new Bundle();
				args.putString(ARG_MESSAGE, tagInfo.toString());
				showDialog(DIALOG_NEW_TAG, args);
			} else {
				StringBuilder tagInfo = new StringBuilder();
				tagInfo.append("Result: \"");
				tagInfo.append(result);
				tagInfo.append("\n");
				Bundle args = new Bundle();
				args.putString(ARG_MESSAGE, tagInfo.toString());
				showDialog(DIALOG_NEW_TAG, args);
				String name ="";
				String image_url ="";
				String url ="";
				String short_description ="";
				String description="";
				try {
					JSONObject jsonObject = new JSONObject(result);
					name = jsonObject.getString("name");
					image_url = jsonObject.getString("image_url");
					url = jsonObject.getString("url");
					short_description= jsonObject.getString("short_description");
					description= jsonObject.getString("description");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				((TextView)findViewById(R.id.text)).setText(name+"\n"+"Url: "+url+"\n"+"Short Description: "+short_description+"\n"+"Description: "+ description);
				new LoadImage().execute(image_url); // Use this one in actual
													// program
				Button CollectButton,ReturnButton;
				CollectButton=(Button)findViewById(R.id.CollectButton);
				ReturnButton=(Button)findViewById(R.id.CancelButton);
				CollectButton.setVisibility(View.VISIBLE);
				ReturnButton.setVisibility(View.VISIBLE);
				CollectButton.setOnClickListener(new OnClickListener() {
					 
					@Override
					public void onClick(View arg0) {
		 
					 
		 
					}
		 
				});
				
			}
		}
	}

	public static String POST(String url, String Results) {
		// Contact Server
		InputStream inputStream = null;
		String result = "";
		try {
			HttpClient httpclient = new DefaultHttpClient();

			HttpPost httpPost = new HttpPost(url);

			String json = "";

			JSONObject jsonObject = new JSONObject();
			// jsonObject.accumulate("name", person.getName());
			// jsonObject.accumulate("country", person.getCountry());
			jsonObject.accumulate("Sent", Results);

			json = jsonObject.toString();

			StringEntity se = new StringEntity(json);

			httpPost.setEntity(se);

			httpPost.setHeader("Accept", "application/json");
			httpPost.setHeader("Content-type", "application/json");

			HttpResponse httpResponse = httpclient.execute(httpPost);

			inputStream = httpResponse.getEntity().getContent();

			if (inputStream != null)
				result = convertInputStreamToString(inputStream);
			else
				result = "Did not work!";

		} catch (Exception e) {
			result = "Exception_Catched!" + e.getLocalizedMessage();
			Log.d("InputStream", e.getLocalizedMessage());
		}

		return result;
	}

	private static String convertInputStreamToString(InputStream inputStream)
			throws IOException {
		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(inputStream));
		String line = "";
		String result = "";
		while ((line = bufferedReader.readLine()) != null)
			result += line;
		inputStream.close();
		return result;
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////
	// Others
	private class LoadImage extends AsyncTask<String, String, Bitmap> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(Main_Activity.this);
			pDialog.setMessage("Loading Image ....");
			pDialog.show();
		}

		protected Bitmap doInBackground(String... urls) {
			String url = urls[0];
			Bitmap bitmap = null;
			InputStream stream = null;
			BitmapFactory.Options bmOptions = new BitmapFactory.Options();
			bmOptions.inSampleSize = 1;

			try {
				stream = getHttpConnection(url);
				bitmap = BitmapFactory.decodeStream(stream, null, bmOptions);
				stream.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			// bitmap = BitmapFactory.decodeStream((InputStream)new
			// URL(args[0]).getContent());

			return bitmap;
		}

		protected void onPostExecute(Bitmap image) {

			if (image != null) {
				img.setImageBitmap(image);
				pDialog.dismiss();
				// Toast.makeText(ECOStamp.this, "Loaded",
				// Toast.LENGTH_SHORT).show();

			} else {
				pDialog.dismiss();
				Toast.makeText(Main_Activity.this,
						"Image Does Not exist or Network Error",
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////

	private InputStream getHttpConnection(String urlString) throws IOException {
		InputStream stream = null;
		URL url = new URL(urlString);
		URLConnection connection = url.openConnection();

		try {
			HttpURLConnection httpConnection = (HttpURLConnection) connection;
			httpConnection.setRequestMethod("GET");
			httpConnection.connect();

			if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				stream = httpConnection.getInputStream();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return stream;
	}

	public boolean isConnected() {
		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected())
			return true;
		else
			return false;
	}

}
