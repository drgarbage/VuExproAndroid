package com.vuexpro.control;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import com.vuexpro.R;
import com.vuexpro.model.Core;
import com.vuexpro.model.Device;
import com.vuexpro.model.Profile;
import com.vuexpro.service.DeviceFactory;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class UIConfigurateDeviceActivity extends Activity {
	public static final String PARAM_INT_PROFILE_ID = "PARAM_INT_PROFILE_ID";

	// Share Object
	static String TAG = "nevin";
	Profile mProfile;

	// Render View
	Button mBtnSave;
	RadioGroup mRbContype;
	EditText mEtUser,mEtPassword,mEtAddress ,mEtName;
	
	TextView mTVSearchlist;  
	ProgressBar pgSearchList;


	// Search Camera
	ArrayList<HashMap<String,String>> searchList = new ArrayList<HashMap<String,String>>();

	SimpleAdapter adapter=null;

	private void FindNewDevice(String message) {  

		// prepare collection
		searchList.clear();

		// Sending UDP to request ack
		byte[] data ={(byte)0xfd,(byte)0xfd,(byte)0x01,(byte)0x00,(byte)0xa1,(byte)0x00,(byte)0xff,(byte)0xff,
				(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,
				(byte)0x00,(byte)0x00,(byte)0x01,(byte)0x00,(byte)0x00,(byte)0x00};

		// Identifier port
		int server_port = 62976;
		DatagramSocket s = null;
		int timeout = 7000;
		try {   
			DatagramPacket p = new DatagramPacket(data, data.length, InetAddress.getByName("255.255.255.255"),server_port);  
			s = new DatagramSocket(server_port);
			s.setSoTimeout(timeout);
			s.setBroadcast(true);
			s.send(p);
			while(true){
				byte[] buffer = new byte[400]; 
				DatagramPacket packetReceived = new DatagramPacket(buffer, buffer.length);
				s.receive(packetReceived);
				int posBrandNameStart=21 ; 
				int posBrandNameEnd= posBrandNameStart+64 ; 
				int modelNameS =posBrandNameEnd+64;
				int modelNameE =modelNameS + 32;
				int posCamNameS = modelNameE+45; 
				int posCamNameE =modelNameE+64 ; 

				String foundBrand= new String(Arrays.copyOfRange(buffer, posBrandNameStart, posBrandNameEnd)).trim();
				String foundModel= new String(Arrays.copyOfRange(buffer, modelNameS, modelNameE)).trim();
				String foundName = new String(Arrays.copyOfRange(buffer, posCamNameS, posCamNameE)).trim();
				String foundIP 	 = packetReceived.getAddress() .getHostAddress().toString(); 
				if (!foundModel.equals("") && !isDeviceExist(foundIP)){
					HashMap<String,String> item = new HashMap<String,String>();

					item.put( "foundcam", foundName);
					item.put( "foundIP", foundIP );
					searchList.add( item );

				}
			}
		} catch (SocketException e) {  
			Log.e(TAG,"SocketException---"+e.toString());
		} catch (UnknownHostException e) {
			Log.e(TAG,"UnknownHostException---"+e.toString()); 
		} catch (SocketTimeoutException e) {
			Log.e(TAG,"SocketTimeoutException---"+e.toString());
		} catch (IOException e) {
			Log.e(TAG,"IOException---"+e.toString());

		}finally{
			if (s!=null)
				s.close();
			this.runOnUiThread(new Runnable(){
				@Override
				public void run() {

					pgSearchList.setVisibility(View.GONE);
					if (searchList.size()==0){
						HashMap<String,String> item  = new HashMap<String,String>();
						item.put( "foundcam","Not Found");
						item.put( "foundIP","There's no other camera in your network");
						searchList.add(item);
					}
					UIConfigurateDeviceActivity.this.adapter = new SimpleAdapter( 
							UIConfigurateDeviceActivity.this, 
							searchList,
							android.R.layout.simple_list_item_2,
							new String[] { "foundcam","foundIP" },
							new int[] { android.R.id.text1, android.R.id.text2 } );

					ListView lv = ((ListView)UIConfigurateDeviceActivity.this.findViewById(R.id.searchlist));
					lv.setAdapter(adapter);
					lv.setTextFilterEnabled(true);
					lv.setOnItemClickListener(new OnItemClickListener(){

						@Override
						public void onItemClick(AdapterView<?> parent,
								View view, int position, long id) {
							if (searchList.size()!= 0 && !searchList.get(0).get("foundcam").equals("Not Found")){
								mEtAddress.setText(searchList.get(position).get("foundIP"));
								mEtName.setText(searchList.get(position).get("foundcam"));
							}

						}});

				}});

		}
	}

	private boolean isDeviceExist(String ip){
		for (Profile p  :Core.getCoreInstance().getProfiles()){
			if(p.getHost().contains(ip))
				return true;
		}
		return false;
	}


	private void fnCreateSample(){
		mEtUser.setText("admin");
		mEtPassword.setText("");
		mEtAddress.setText("demo.isapsolution.com:84");	//mobile.isapsolution.com:81 203.125.227.70, 203.69.170.40:8091~203.69.170.40:8099  mobile.isapsolution.com:81
		((RadioGroup)findViewById(R.id.rg_con_group)).check(R.id.rb_ipcam);
	}

	private void updateUI(){

		mEtName.setText(this.mProfile.getName());
		String type = this.mProfile.getType();
		if (type!=null && type.equals(Device.TYPE_IPCAM)){
			mRbContype.check(R.id.rb_ipcam);
		}else if (type!=null && type.equals(Device.TYPE_NVR)){
			mRbContype.check(R.id.rb_nvr); 
		}
		String url = this.mProfile.getHost();
		if (url.toLowerCase().contains("http://"))
			url= url.substring(7);
		String[] pair = url.split("@");
		if (pair.length==2){
			if (pair[0].split(":").length==2){
				mEtUser.setText(pair[0].split(":")[0]);
				mEtPassword.setText(pair[0].split(":")[1]);
				mEtAddress.setText(pair[1]);
			}else{
				mEtUser.setText(pair[0].split(":")[0]);
				mEtPassword.setText("");
				mEtAddress.setText(pair[1]);
			}

		}
	} 

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_edit, menu);
		ActionBar actionBar = this.getActionBar();
		actionBar.setHomeButtonEnabled(true);
		actionBar.setIcon(R.drawable.ic_menu_back);
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setDisplayShowTitleEnabled(false);
		// return true means onCreateOptionsMenu has handle this event. 
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent ;
		switch (item.getItemId()) { 

		case R.id.menu_save:
//			Toast.makeText(this, "Creating New Device....", Toast.LENGTH_SHORT).show();
			// TODO: Remove below block after implement NVR
			if (this.mRbContype.getCheckedRadioButtonId()!=R.id.rb_ipcam){
				Toast.makeText(this, "NVR Not Implemented", Toast.LENGTH_SHORT).show();
				return true;
			}
			
			// Initial Candidate Profile
			Profile challenger = new Profile();
			
			// Set Candidate Profile Host From UI
			String host="";
			if (mEtPassword.getText().toString().equals(""))
				host="http://"+mEtUser.getText().toString()+"@"+mEtAddress.getText().toString();
			else
				host="http://"+mEtUser.getText().toString()+":"+mEtPassword.getText().toString()+"@"+mEtAddress.getText().toString();
			challenger.setHost(host);

			// Set Candidate Profile Type from UI
			if (this.mRbContype.getCheckedRadioButtonId()==R.id.rb_ipcam){
				challenger.setType(Device.TYPE_IPCAM);
			}else if (this.mRbContype.getCheckedRadioButtonId()==R.id.rb_nvr){
				challenger.setType(Device.TYPE_NVR);
			}
			
			// Set Candidate Profile Name from UI
			challenger.setName(this.mEtName.getText().toString());

			boolean success = DeviceFactory.createDevice(challenger,this.mProfile);

			if (success){
				Core.save();
				finish();
				return true;
			}else{
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setMessage("Please make sure info is currect or network is truned on");
				builder.setCancelable(false);
				builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
					}
				});
				AlertDialog alert = builder.create();
				alert.show();
				return true;
			}
			
		case android.R.id.home:
			this.finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.device_config);

		// Initial View
		mEtUser  = (EditText)findViewById(R.id.et_user);
		mEtPassword = (EditText)findViewById(R.id.et_password);
		mEtAddress = (EditText)findViewById(R.id.et_address);
		mEtName = (EditText)findViewById(R.id.et_name);
		mRbContype =  (RadioGroup)findViewById(R.id.rg_con_group);
		pgSearchList = ((ProgressBar)UIConfigurateDeviceActivity.this.findViewById(R.id.progressBar1));
		mTVSearchlist = ((TextView)UIConfigurateDeviceActivity.this.findViewById(R.id.tv_searchlist));

		// Initial Shared Objects
		int key = this.getIntent().getIntExtra(PARAM_INT_PROFILE_ID, -1);
		if (key==-1 ){
			// Create device
			mTVSearchlist.setVisibility(View.VISIBLE);
			pgSearchList.setVisibility(View.VISIBLE);
			new Thread(new Runnable(){
				public void run() { 
					FindNewDevice("");
				}}).start();
//			this.fnCreateSample();
		}else{
			// Update Device
			this.mProfile = Core.getCoreInstance().getProfile(key);
			this.updateUI();
		}
	}

	@Override
	public void onPause() {
		super.onPause();
	}
	

}
