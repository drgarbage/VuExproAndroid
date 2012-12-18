package com.vuexpro.control;

import com.vuexpro.R;
import com.vuexpro.model.Core;
import com.vuexpro.service.DeviceFactory;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

// This activity is the entrance point to the APP
// It quickly load Core and  setup,
//   start the correspond Activity as a decision maker 
public class VuExproActivity extends Activity {
	private static final String TAG = "nevin";
	@Override 
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome);

		new Task().execute(1);
	}
	
	class Task extends AsyncTask<Integer, Void, Void> {
		@Override
		protected Void doInBackground(Integer... params) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				Log.e(TAG,"Exception when asleep");
			}
			return null;
		}
		@Override
		protected void onPostExecute(Void s) {
			
			if (Core.getCoreInstance().getProfiles().size() == 0 ) {
				Core.setContext(VuExproActivity.this);
				Core.load(); 
				Core system = Core.getCoreInstance();
				int size = system.getProfiles().size();
				for (int i=0;i<size;i++){
					DeviceFactory.createDevice(system.getProfiles().get(i), system.getProfiles().get(i));
				} 
			}
			if (Core.getCoreInstance().getProfiles().size() == 0){
				Intent intent = new Intent(VuExproActivity.this, UIConfigurateDeviceActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
				
			Intent intent = new Intent();
			intent.setClass(VuExproActivity.this, UIDeviceViewActivity.class);
			intent.putExtra(UIDeviceViewActivity.PARAM_INT_PROFILE_ID,-1);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
			VuExproActivity.this.startActivity(intent);
			VuExproActivity.this.finish();
		}
	}
}
