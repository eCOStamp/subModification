package NFC;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

import ndef.UriRecordHelper;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

/**
 *
 * @author Test
 */

public class NFC_Class extends Activity {
    private static final int PENDING_INTENT_TECH_DISCOVERED = 1;
    private static final int DIALOG_WRITE_URL = 1;
    private static final int DIALOG_WRITE_ERROR = 2;
    private static final int DIALOG_NEW_TAG = 3;
    private static final String ARG_MESSAGE = "message";
    private NfcAdapter mNfcAdapter;
    private EditText mMyUrl;
    private Button mMyWriteUrlButton;
    private boolean mWriteUrl = false;

     /**
     *
     * @param data
     * @param foregroundDispatch
     */ 
    
    
     public String resolveIntent (Intent data, boolean foregroundDispatch)  {
        this.setIntent(data);
        String action = data.getAction();
        String uri="none";
        // We were started from the recent applications history: just show our main activity
        // (otherwise, the last intent that invoked us will be re-processed)
        if ((data.getFlags() & Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY) != 0) { return "Error!!"; }

        // Intent is a tag technology (we are sensitive to Ndef, NdefFormatable) or
        // an NDEF message (we are sensitive to URI records with the URI http://www.mroland.at/)
        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            
            // The reference to the tag that invoked us is passed as a parameter (intent extra EXTRA_TAG)
            Tag tag = data.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            
            // Retrieve information from tag and display it
                StringBuilder tagInfo = new StringBuilder();
                // Get tag's UID:
                byte[] uid = tag.getId();
                //tagInfo.append("Message_ID: ").append(StringUtils.convertByteArrayToHexString(uid)).append("\n\n");
                // Get tag's NDEF messages: The NDEF messages are passed as parameters (intent
                // extra EXTRA_NDEF_MESSAGES) and have to be casted into an NdefMessage array.
                Parcelable[] ndefRaw = data.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
                NdefMessage[] ndefMsgs = null;
                if (ndefRaw != null) {
                    ndefMsgs = new NdefMessage[ndefRaw.length];
                    for (int i = 0; i < ndefMsgs.length; ++i) {
                        // Cast each NDEF message from Parcelable to NdefMessage:
                        ndefMsgs[i] = (NdefMessage) ndefRaw[i];
                    }
                }
                // Find URI records:
                if (ndefMsgs != null) {
                    // Iterate through all NDEF messages on the tag:
                    for (int i = 0; i < ndefMsgs.length; ++i) {
                        // Get NDEF message's records:
                        NdefRecord[] records = ndefMsgs[i].getRecords();
                        if (records != null) {
                            // Iterate through all NDEF records:
                            for (int j = 0; j < records.length; ++j) {
                                // Test if this record is a URI record:
                                if ((records[j].getTnf() == NdefRecord.TNF_WELL_KNOWN)
                                        && Arrays.equals(records[j].getType(), NdefRecord.RTD_URI)) {
                                    // Get record payload:
                                    byte[] payload = records[j].getPayload();                                 
                                    // Use UriRecordHelper to decode URI record payload:
                                    uri = UriRecordHelper.decodeUriRecordPayload(payload);                                   
                                    //uri=uri+"_"+Convert_String(uri);     
                                    //Toast.makeText(getBaseContext(), "Login Failed "+ uri, Toast.LENGTH_LONG).show();
                                    //new NFC_Class.HttpAsyncTask().execute(uri);
                                 
                                   
                                }
                            }
                        }
                    }
                }
        }
        return uri;
     }
    
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
     public class HttpAsyncTask extends AsyncTask<String, Void, String> {
        
         @Override
		protected String doInBackground(String...results){
          
			return POST("http://ecostamp.aosekai.net/api/dummy/",results[0]);
		}
		
		@Override
		protected void onPostExecute(String result){
                     
		}

       
     }
     public static String POST(String url, String Results){
		InputStream inputStream = null;
		String result = "";
		try{
			HttpClient httpclient = new DefaultHttpClient();
			
			HttpPost httpPost = new HttpPost(url);
			
			String json = "";
			
			JSONObject jsonObject = new JSONObject();
			//jsonObject.accumulate("name", person.getName());
			//jsonObject.accumulate("country", person.getCountry());
			jsonObject.accumulate("Sent", Results);
			
			json = jsonObject.toString();
			
			StringEntity se = new StringEntity(json);
			
			httpPost.setEntity(se);
			
			httpPost.setHeader("Accept","application/json");
			httpPost.setHeader("Content-type","application/json");
			
			HttpResponse httpResponse = httpclient.execute(httpPost);
			
			inputStream = httpResponse.getEntity().getContent();
			
			if(inputStream != null)
				result = convertInputStreamToString(inputStream);
			else
				result = "Did not work!";
			
			
		} catch (Exception e){
                        result = "Exception_Catched!"+ e.getLocalizedMessage();
			Log.d("InputStream",e.getLocalizedMessage());
		}
		
		return result;
	}
    
    private static String convertInputStreamToString(InputStream inputStream) throws IOException{
		BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
		String line = "";
		String result = "";
		while((line = bufferedReader.readLine()) != null)
			result += line;
		inputStream.close();
		return result;
	}
    
    
    
    public static String Convert_String(String uri)
    {
         if(uri.contains("Deus"))
         {
             uri="CodeName0001";    
         }
         else if(uri.contains("Bevis"))
         {
             uri="CodeName0002";    
         }
         else if(uri.contains("Judy"))
         {
             uri="CodeName0003";    
         }
         else if(uri.contains("Chuo"))
         {
             uri="CodeName0004";    
         }
         else if(uri.contains("Build"))
         {
             uri="CodeName0005";    
         }
         else if(uri.contains("Nachai"))
         {
             uri="CodeName0006";    
         }
         else
         {
             uri="Not Detect Any Known Name";
         }    
        return uri;
    }
    
}
    
