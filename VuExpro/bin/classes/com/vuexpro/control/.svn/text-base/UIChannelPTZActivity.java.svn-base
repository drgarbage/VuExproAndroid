package com.vuexpro.control;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Properties;

import com.vuexpro.R;
import com.vuexpro.model.Channel;
import com.vuexpro.model.Core;
import com.vuexpro.model.Profile;
import com.vuexpro.service.DeviceFactory;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.GridView;
import android.widget.Switch;
import android.widget.TableRow;
import android.widget.TextView;

public class UIChannelPTZActivity extends Activity implements OnCheckedChangeListener ,OnItemClickListener{
	String TAG = "nevin";

	// Channel 
	Channel mChannel;

	// View
	GridView gvControl ;
	GridView gvPreset  ;
	Switch   stDO;
	TextView tvControl ;
	TextView tvPreset;
	TextView tvDO;
	TableRow trDO;

	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_channel, menu);
		ActionBar actionBar = this.getActionBar();
		actionBar.setHomeButtonEnabled(true);
		actionBar.setIcon(R.drawable.ic_menu_back);
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setDisplayShowTitleEnabled(false);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case android.R.id.home:
			this.backToChannel();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ptz);

		int profileIndex = 0;
		int selectedChannelIndex = -1;

		Intent currentIntent = this.getIntent();

		profileIndex = currentIntent.getIntExtra(UIDeviceViewActivity.PARAM_INT_PROFILE_ID, profileIndex);
		selectedChannelIndex = currentIntent.getIntExtra(UIDeviceViewActivity.PARAM_INT_SELECTED_CHANNEL_INDEX, selectedChannelIndex);

		Profile profile = Core.getCoreInstance().getProfile(profileIndex);
		
		this.mChannel= profile.getDevice().getChannel(selectedChannelIndex);
		
		Log.d(TAG,"Recieve profileID--["+profileIndex+"]---"+profile);
		Log.d(TAG,"Recieve channelID--["+selectedChannelIndex+"]---"+mChannel+"-------"+mChannel.getConnectionString());
		
		trDO = (TableRow)findViewById(R.id.tableRow_do);
		tvDO = (TextView)findViewById(R.id.tv_do);
		stDO = (Switch)findViewById(R.id.switch_do);
		

		tvControl  = (TextView)findViewById(R.id.tv_control);
		gvControl  = (GridView)findViewById(R.id.gridView_ptz);
		ArrayList<String> defaultActions= new ArrayList<String>();		defaultActions.add("Partrol");defaultActions.add("Preset Tour");defaultActions.add("Home");
		gvControl.setAdapter(new ArrayAdapter<String>(UIChannelPTZActivity.this,android.R.layout.simple_list_item_activated_1, defaultActions));		// Set list view
		gvControl.setOnItemClickListener(this);
//		gvControl.setAdapter(new ArrayAdapter<String>(UIChannelPTZActivity.this,android.R.layout.simple_list_item_activated_1, Properties2ArrayList(this.mChannel.getDevice().getPreset())));

		tvPreset = (TextView)findViewById(R.id.tv_preset);
		gvPreset  = (GridView)findViewById(R.id.gridView_preset);
		if (this.mChannel.getDevice().getPreset()!=null){
			gvPreset.setOnItemClickListener(this);
			gvPreset.setAdapter(new ArrayAdapter<String>(UIChannelPTZActivity.this,android.R.layout.simple_list_item_activated_1, Properties2ArrayList(this.mChannel.getDevice().getPreset())));
		}
	}

	@Override
	public void onResume(){
		super.onResume();
		DeviceFactory.refreshInfo(this.mChannel,this);
		this.updateUI();
		stDO.setOnCheckedChangeListener(this);	// set listener must be after updateUI. Or Initial update UI will trigger the 'OnChechedChange' event
	}

	
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (buttonView == stDO){
			if (isChecked){
				//					Log.d(TAG,"onCheckedChanged. isChecked ");
				this.mChannel.executeCommand("dioByIO", "on");
				this.mChannel.executeCommand("dioByCtrl", "1");
				this.mChannel.executeCommand("dioByVb", "1");
				this.mChannel.executeCommand("dioBySetdo", "1");
			}else{
				this.mChannel.executeCommand("dioByIO", "off");
				this.mChannel.executeCommand("dioByCtrl", "0");
				this.mChannel.executeCommand("dioByVb", "0");
				this.mChannel.executeCommand("dioBySetdo", "0");
			}
			backToChannel();
		}

	}
	@Override
 	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (parent==gvControl){
			switch(position){
			case 0:
				this.mChannel.executeCommand("patrol","single_pan");
				backToChannel();
				break;
			case 1:
				this.mChannel.executeCommand("patrol","single_patrol");
				backToChannel();
				break; 
			case 2:
				this.mChannel.executeCommand("patrol","go_home");
				backToChannel();
				break;
			}
		}
		if (parent ==gvPreset){
			this.mChannel.executeCommand("presetByID", position+"");
			this.mChannel.executeCommand("presetByName", ((TextView)view).getText()+"");
			backToChannel();
		}

	}

	private ArrayList<String> Properties2ArrayList(Properties prop){
		if (prop==null) return null;
		ArrayList<String>  propKey= new ArrayList<String>();;
		for (Enumeration<Object> e = prop.keys(); e.hasMoreElements(); /**/) {
			String key = (String) e.nextElement();
			if (key.equals("customizedhome")||key.equals("presets")){
			}else{
				propKey.add(key);
			}
		}
		return propKey;
	}
	private void updateUI() {
		// DIO
		if (this.mChannel.getDevice().getDIO()==null || this.mChannel.getDevice().getDIO().getProperty("out1")==null){
			trDO.setVisibility(View.GONE);
			tvDO.setVisibility(View.GONE);
			stDO.setVisibility(View.GONE);
		}else if (this.mChannel.getDevice().getDIO().getProperty("out1").equals("on")) {
			this.stDO.setActivated(true);
			this.stDO.setChecked(true);
		}else if (this.mChannel.getDevice().getDIO().getProperty("out1").equals("off")) {
			this.stDO.setActivated(true);
			this.stDO.setChecked(false);
		}
		
		// Preset
		if (this.mChannel.getDevice().getPreset()==null){
			tvPreset.setVisibility(View.GONE);	// Caption
			gvPreset.setVisibility(View.GONE);	// Content
		}

	}
	private void backToChannel(){
		finish();
	}
}
