package com.vuexpro.service;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Properties;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;


public class HTTPRequestHelper extends AsyncTask<String, Integer, Properties> {
	private static final String TAG = "nevin";
	private Activity mActivity;
	private ProgressDialog dialog;

	public HTTPRequestHelper(Activity activity, String message) {
		this.mActivity = activity ;
		this.dialog = new ProgressDialog(this.mActivity);
		this.dialog.setMessage(message);

	}
	public HTTPRequestHelper() {


	}
	@Override
	protected void onPreExecute() {
		if (this.dialog==null)
			return;
		this.dialog.show();
	}
	@Override
	protected void onPostExecute( Properties result) {
		if (this.dialog==null)
			return;
		if (dialog.isShowing()) 
			dialog.dismiss();

	}

	@Override
	protected Properties doInBackground(String... url) {
//		Log.d(TAG,"HTTP Request[info] url :"+url[0]);
		if (isCancelled())	return null;
		int rpCode;
		//***************************************
		String mURL = url[0];
		String acc="";
		String pas="";
		if (mURL.toLowerCase().contains("http://"))
			mURL= mURL.substring(7);
		String[] pair = mURL.split("@");
		if (pair.length==2){
			if (pair[0].split(":").length==2){
				acc= pair[0].split(":")[0];
				pas=pair[0].split(":")[1];
				mURL=pair[1];
			}else{
				acc=pair[0].split(":")[0];
				mURL= pair[1];
			}
		}
		//******************************************************

		UsernamePasswordCredentials cred=null;
		URI uri =null;
		HttpResponse res=null ;
		DefaultHttpClient httpclient=null;
		DataInputStream in =null;
	
//		Log.d(TAG,"HTTP Request[info] acc :"+acc+", password : "+pas+", URL : "+ mURL);
		try {
			uri	 = URI.create("http://"+mURL);
			cred = new UsernamePasswordCredentials(acc,pas);

			HttpParams httpParameters = new BasicHttpParams();
			int timeoutConnection = 3000;
			HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
			int timeoutSocket = 3000;
			HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
			httpclient = new DefaultHttpClient(httpParameters);
			httpclient.getCredentialsProvider().setCredentials(new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT),cred);
			res = httpclient.execute(new HttpGet(uri));
			// if not response success , release resource
			rpCode = res.getStatusLine().getStatusCode();
			if(rpCode!=200){
				return null;
			}else{
				in= new DataInputStream(res.getEntity().getContent());
				Properties p = new Properties();
				p.load(in);
				return p;
			}  
		} catch (Exception e) {
			Log.e(TAG, "Request to["+uri.toString()+"] ConnectTimeoutException:", e);
		} finally{
			cred=null;
			uri =null;
			res =null;
			httpclient=null;
			in=null;
			
		}

		return null;
	}

}