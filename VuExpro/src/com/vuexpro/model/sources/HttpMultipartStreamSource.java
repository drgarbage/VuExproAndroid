package com.vuexpro.model.sources;

import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.example.test.H264StreamSource;
import com.vuexpro.model.StreamSource;


import android.graphics.Bitmap;
import android.util.Log;


public class HttpMultipartStreamSource extends StreamSource implements Runnable{
	//	private volatile Thread _currentThread;
	//	private volatile boolean playingFlag=false;
	private volatile Thread blinker;
	private volatile boolean playingFlag;

	@Override
	public  void connect() {
		if (this.playingFlag ) return;
		this.playingFlag=true;
		blinker = new Thread(this);
		blinker.start();
	}

	@Override 
	public void disconnect() {
		if (this.blinker==null) return;
		this.playingFlag=false;
		try {
			this.blinker.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	} 

	@Override
	public void run() {

		String mURL = this.ConnectionString;
		String acc="";
		String pas="";
		if (mURL!=null && mURL.toLowerCase().contains("http://"))
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
		UsernamePasswordCredentials cred=null;
		URI uri =null;
		HttpResponse res=null ;
		DefaultHttpClient httpclient=null;
		ISapDataInputStream in = null;
		try {
			uri = URI.create("http://"+mURL);
			cred = new UsernamePasswordCredentials(acc,pas);
			httpclient = new DefaultHttpClient();  
			httpclient.getCredentialsProvider().setCredentials(
					new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT),
					cred);
			res = httpclient.execute(new HttpGet(uri));

			if(res.getStatusLine().getStatusCode()!=200){
				this.disconnect();
			}else{
				if (this.ConnectionString.contains("H264"))
					in= new H264StreamSource(res.getEntity().getContent());
				else
					in= new MjpegInputStream(res.getEntity().getContent());
				while(playingFlag){   
					Bitmap frame =  in.getCurrentFrame();
					this.onReceiveSegment(frame);
				}
				in.close();
				in.bye();
				this.onFinish();
			}
		} catch (Exception e) {
			Log.e("nevin","HttpMultipartStreamSource Exception: "+e.toString());
			e.printStackTrace();
		}finally{
			cred=null;
			uri =null;
			res =null;
			httpclient=null;
			in=null;
		}
	}

}
