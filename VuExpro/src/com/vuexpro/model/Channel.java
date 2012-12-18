package com.vuexpro.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.vuexpro.control.UIChannelPTZActivity;
import com.vuexpro.service.HTTPRequestHelper;

import android.graphics.Bitmap;
import android.util.Log;

public abstract class Channel implements IStreamSourceListener {

	public String Name;
	private String ConnectionString;

	private Device mDevice;
	private boolean isActive = false;
	private Bitmap CurrentFrame;
	protected StreamSource PrimaryStream;
	protected StreamSource SecondaryStream;
	public List<IChannelViewer> _channelViewers;
	private List<IChannelViewer> _focusViewers;

	public Channel(){
		this.SecondaryStream = this.prepareSecondaryVideoSource();
		this.SecondaryStream.setListenr(this);
		this.PrimaryStream = this.preparePrimaryVideoSource();
		this.PrimaryStream.setListenr(this);

		_channelViewers = new ArrayList<IChannelViewer>();
		_focusViewers = new ArrayList<IChannelViewer>();
	}

	// properties
	public Device getDevice() {
		return mDevice;
	}
	public void setDevice(Device mDevice) {
		this.mDevice = mDevice;
	}
	public String getConnectionString() {
		return ConnectionString;
	}
	public void setConnectionString(String connectionString) {
		ConnectionString = connectionString;
	}
	public boolean capableOf(String key){
		return false;
	}

	// methods

	public void active()	{
		if(isActive)return; 
		isActive=true;  
		onStatusChanged();	
	}
	public void deactive()	{
		if(!isActive)return;
		isActive=false;	
		onStatusChanged();
	}
	public void focus(IChannelViewer viewer) {
		if(_focusViewers.contains(viewer)) return;
		_focusViewers.add(viewer);
		onStatusChanged();
	}
	public void blur(IChannelViewer viewer)	{
		if(!_focusViewers.contains(viewer)) return;
		_focusViewers.remove(viewer);
		onStatusChanged();
	}
	public void addViewer(IChannelViewer viewer){
		if(_channelViewers.contains(viewer)) return;
		_channelViewers.add(viewer);
		Log.d("nevin","Add Viewer : now Channel " + this +" has "+this._channelViewers.size()+" viewers");
		onStatusChanged();
	}
	public void removeViewer(IChannelViewer viewer){
		if(!_channelViewers.contains(viewer)) return;
		_channelViewers.remove(viewer);
		onStatusChanged();
	}
	public void removeAllViewer(){
		_channelViewers.clear();
		onStatusChanged();
	}

	public Bitmap snapshot(){ return this.CurrentFrame; }

	// ptz
	public void relativePTZ(double pan, double titlt,double zoom){ }
	public void absolutePTZ(double pan, double titlt,double zoom){ }

	// playback
	public void seek(long time){ }
	public void executeCommand(String command, String param){ }


	// event handlers & internal flows
	protected abstract StreamSource preparePrimaryVideoSource();
	protected abstract StreamSource prepareSecondaryVideoSource();

	private void onStatusChanged(){
		int state = 0;
		int viewerCnt = 0;
		int focusCnt = 0;

		viewerCnt = _channelViewers.size();
		focusCnt = _focusViewers.size();

		if (isActive && viewerCnt > 0 && focusCnt>0) {
			state = 3; // run primary
		} else if (isActive && viewerCnt > 0 && focusCnt<=0) {
			state = 2; // run secondary
		} else if (isActive && viewerCnt == 0) {
			state = 1; // stop stream
		} else if (isActive) {
			state = 0;
		} else {
			state = -1;
		}
		switch(state){
		case 3:
			this.SecondaryStream.connect();
			break;
		case 2:
			// TODO: Decode I Frame only
			this.SecondaryStream.connect();
			break;
		case 1:
		case 0:
		default:
			this.SecondaryStream.disconnect();
			break;
		} 

	}

	@Override
	public void onReceiveSegment(Bitmap frame) {
		this.CurrentFrame = frame; 
		int size = _channelViewers.size();
		for(int i=0; i < size;i++) {
			_channelViewers.get(i).onFrameUpdate(this);
		}
	}
	@Override
	public void onError() {
		// TODO Auto-generated method stub
	}
	@Override
	public void onFinish() {
		// TODO Auto-generated method stub
	}



}
