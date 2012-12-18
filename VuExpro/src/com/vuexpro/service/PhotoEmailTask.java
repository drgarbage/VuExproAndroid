package com.vuexpro.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import com.vuexpro.R;
import com.vuexpro.model.Channel;

//AnsyncTask
public	class PhotoEmailTask  extends AsyncTask<Integer, Void, Integer> {

	private static final String TAG = "nevin";
	ArrayList<Channel> mChannels;
	boolean isSendEmail; 
	Activity mActivity;
	View mBGView ;

	public PhotoEmailTask(ArrayList<Channel> channels ,boolean isSendEmail ,Activity acttivity){
		this.isSendEmail = isSendEmail;
		this.mChannels = channels;
		this.mActivity = acttivity;
	}
	private void emailFiles(String path , ArrayList<String> fileNames) {

		Intent sendIntent ;//= new Intent(Intent.ACTION_SEND);
		
		if (fileNames!=null && fileNames.size()==1){
			sendIntent = new Intent(Intent.ACTION_SEND);
			sendIntent.setType("application/zip");
			sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(path+"/"+fileNames.get(0)));
			Log.d("test","-----2-----"+Uri.parse(path+"/"+fileNames.get(0)));
		}else{
			ArrayList<Uri> uris = new ArrayList<Uri>();
			for (String fileName : fileNames){
				uris.add(Uri.parse(path+"/"+fileName));
				Log.d("test","-----1-----"+Uri.parse(path+"/"+fileName));
			}
			sendIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
			sendIntent.setType("application/zip");
			sendIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
		}
		sendIntent.putExtra(Intent.EXTRA_SUBJECT, "VuExpro Snapshot Notification");
		sendIntent.putExtra(Intent.EXTRA_TEXT, "Please refer to attachment for details.");
		sendIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"".trim()});
		this.mActivity.startActivity(Intent.createChooser(sendIntent, "Select Destination"));

	}
	@Override
	protected Integer doInBackground(Integer... params) {
		try {
			if(this.mChannels==null||this.mChannels.size()==0){
				Log.e(TAG," imageview not ready");
				return 0;
			}

			String path = Environment.getExternalStorageDirectory().toString()+"/DCIM/VuExpro";
			File filePath=new File(path);
			if (!filePath.exists()){
				filePath.mkdir();
			} 
			ArrayList<String> fileNames = new ArrayList<String>();
			for (Channel ch: this.mChannels){
				if (ch.snapshot()!=null){
					java.util.Date date= new java.util.Date();
					String fileName = new Timestamp(date.getTime()).toString();
					fileName=fileName.replace(":", "").replace("-", "").replace(" ", "").replace(".", "")+".jpg";
					fileNames.add(fileName);
					File file=new File(path,fileName);
					if (file.exists()){
						return 0;
					}
					FileOutputStream out = new FileOutputStream(file);
					ch.snapshot().compress(android.graphics.Bitmap.CompressFormat.JPEG, 100, out);
					out.flush();
					out.close();
					out=null;
				}
			}
			if(isSendEmail){
				this.emailFiles("file://"+path,fileNames);
				isSendEmail=false;
			}
		} catch (Exception e) {
			Log.e(TAG,"CameraOnClickListener:"+e.toString());
			e.printStackTrace();
		} 
		return 100;
	}
	@Override
	protected void onPreExecute (){
		
//		if (this.mActivity==null) return;
		mBGView = this.mActivity.findViewById(R.id.dv_snap);
		mBGView.setBackgroundDrawable(null);
		if (mBGView!=null)
			mBGView.setBackgroundDrawable(null);
	}
	@Override
	protected void onPostExecute (Integer result){
		mBGView.setBackgroundResource(R.drawable.background);
		if(result==100){
			if (mBGView!=null)
				mBGView.setBackgroundResource(R.drawable.background);
		}
	}


}

