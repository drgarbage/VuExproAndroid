package com.vuexpro.control;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.vuexpro.R;
import com.vuexpro.model.Channel;
import com.vuexpro.model.Core;
import com.vuexpro.model.Profile;
import com.vuexpro.service.PhotoEmailTask;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

public class UIDeviceListActivity extends Activity implements ListAdapter,UIDeviceListItemView.DeviceListItemCallBack{
	private String TAG="nevin";
	private ListView listview;
	private LayoutInflater mInflater;
	private Core _system;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// General Menu Bar Setup  
		ActionBar actionBar = this.getActionBar();
		actionBar.setHomeButtonEnabled(true);
		actionBar.setIcon(R.drawable.ic_menu_back);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayShowTitleEnabled(false);
		// Specify which menu item will be used 
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_device_list, menu);
		return super.onCreateOptionsMenu(menu);
	}
	@Override
	public boolean onPrepareOptionsMenu(Menu menu){ 
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent ;
		switch (item.getItemId()) {
		case R.id.menu_add:
			intent = new Intent(this, UIConfigurateDeviceActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			return true;
		case R.id.menu_list:
			
			if (_currentListMode == ITEM_VIEW_TYPE_EDIT){
				item.setIcon(android.R.drawable.ic_menu_edit);
				_currentListMode = ITEM_VIEW_TYPE_DEFAULT;
				this.notifyDataSetChanged();
			}else{
				item.setIcon(R.drawable.edit_query);
				_currentListMode = ITEM_VIEW_TYPE_EDIT;
				this.notifyDataSetChanged();
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.device_list);

	}
	@Override
	public void onResume() {
		super.onResume();
		_system = Core.getCoreInstance();

		listview = (ListView)this.findViewById(R.id.device_list);
		listview.setAdapter(this);
		mInflater = LayoutInflater.from(listview.getContext());
		this.notifyDataSetChanged();
		
	}
	@Override
	public void onPause() {
		super.onPause();
		// TODO stop all channels
		Core system  = Core.getCoreInstance();
//		for (int i=0;i<system.getProfiles().size();i++){
		for(Profile p : system.getProfiles()){
			for (Channel c :p.getDevice().getChannels())
				c.removeAllViewer();
		}
	}

	//
	// ListItem
	//
	public void edit(Profile profile) {
		Context context = listview.getContext();
		int profileid = _system.indexOfProfile(profile);
		Intent intent = new Intent();
		intent.setClass(context, UIConfigurateDeviceActivity.class);
		intent.putExtra(UIConfigurateDeviceActivity.PARAM_INT_PROFILE_ID,profileid);
		context.startActivity(intent);
	}
	public void delete(Profile profile){
		// Remove those channel in dummy profile, Not tested
		Core.getCoreInstance().getProfile(-1).getDevice().getChannels().removeAll(profile.getDevice().getChannels());
		// Remove the profile from Core
		_system.getProfiles().remove(profile);
		Core.save();
		this.notifyDataSetChanged();
	}
	public void view(Profile profile){
		// 1: 	At phone, after you click a device and go to deviceView,
		//		you'll be direct to a deviceView with dummy device. Hence you'll
		//		need to pass the position the first channel will be in the dummy device
		//		This is done by counting how many channels are before you.(TODO:implement it)
		//	2. 	If at tablet, you should send the correct profile and ID to it
		Context context = listview.getContext();
		boolean isPhone=false;
		int profileid = 0 ; 
		if (isPhone){
			profileid = _system.indexOfProfile(profile);	// send your really profile ID when at tablet
		}else{
			profileid = -1 ;								// -1 means dummy device
		}
		int deviceId = 0 ;
		for(Profile p : Core.getCoreInstance().getProfiles()){
			if (p == profile)
				break ;
			deviceId++;
		}
		Intent intent = new Intent();
		intent.setClass(context, UIDeviceViewActivity.class);
		intent.putExtra(UIDeviceViewActivity.PARAM_INT_PROFILE_ID,profileid);
		intent.putExtra(UIDeviceViewActivity.PARAM_INT_SELECTED_CHANNEL_INDEX,deviceId);
		context.startActivity(intent);
	}
	public void moveUp(Profile profile){
		List<Profile> profiles = _system.getProfiles();
		int currentPos = profiles.indexOf(profile);
		if (currentPos==0) return;
		Collections.swap(profiles,currentPos,currentPos-1);
		Core.save();
		this.notifyDataSetChanged();
	}
	public void moveDown(Profile profile){
		List<Profile> profiles = _system.getProfiles();
		int currentPos = profiles.indexOf(profile);
		int profileCount = profiles.size();
		if(currentPos >= profileCount - 1) return;
		Collections.swap(profiles,currentPos,currentPos+1);
		Core.save();
		this.notifyDataSetChanged();
	}
	public void photo(Profile _profile) {
		new PhotoEmailTask(_profile.getDevice().getChannels(),false,this).execute();
		Vibrator myVibrator = (Vibrator) getApplication().getSystemService(Service.VIBRATOR_SERVICE);
		myVibrator.vibrate(500);
		
	}

	//
	// implements ListAdapter
	//
	private static final int ITEM_VIEW_TYPE_DEFAULT = 0;
	private static final int ITEM_VIEW_TYPE_EDIT = 1;
	private int _currentListMode = ITEM_VIEW_TYPE_DEFAULT;
	private List<DataSetObserver> _dataSetObservers = new ArrayList<DataSetObserver>();

	@Override
	public int getCount() {
		return _system.getProfiles().size();
	}
	@Override
	public Object getItem(int position) {
		return _system.getProfile(position);
	}
	@Override
	public long getItemId(int position) {
		return position;
	}
	@Override
	public int getItemViewType(int position) {
		return _currentListMode;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		UIDeviceListItemView dliView = null ;
		if (convertView == null){
			dliView = (UIDeviceListItemView) mInflater.inflate(R.layout.device_list_detail, null);
			dliView.setParent(this);
		}else{
			dliView = (UIDeviceListItemView) convertView;
		}
		dliView.setProfile(_system.getProfile(position));
		switch(_currentListMode) {
		case ITEM_VIEW_TYPE_DEFAULT:
			dliView.setEditMode(false);
			break;
		case ITEM_VIEW_TYPE_EDIT:
			dliView.setEditMode(true);
			break;
		}
		return dliView;
	}
	@Override
	public int getViewTypeCount() {
		return 2;
	}
	@Override
	public boolean hasStableIds() {
		// TODO Use object reference instead of id.
		//      But I'm not sure why id is required.
		//      So I kept this todo as a reminder.
		return true;
	}
	@Override
	public boolean isEmpty() {
		return _system.getProfiles().isEmpty();
	}
	@Override
	public void registerDataSetObserver(DataSetObserver observer) {
		if(!_dataSetObservers.contains(observer))
			_dataSetObservers.add(observer);
	}
	@Override
	public void unregisterDataSetObserver(DataSetObserver observer) {
		_dataSetObservers.remove(observer);
	}
	@Override
	public boolean areAllItemsEnabled() {
		// TODO in case of nvr, when the device info isn't load, the item should be disable.
		return true;
	}
	@Override
	public boolean isEnabled(int position) {
		// TODO in case of nvr, when the device info isn't load, the item should be disable.
		return true;
	}
	private void notifyDataSetChanged(){
		Iterator<DataSetObserver> iterator = _dataSetObservers.iterator();
		while(iterator.hasNext())
			iterator.next().onChanged();
	}
	
}