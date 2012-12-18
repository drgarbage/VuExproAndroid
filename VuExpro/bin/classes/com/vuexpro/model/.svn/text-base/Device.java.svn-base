package com.vuexpro.model;

import java.util.ArrayList;
import java.util.Properties;

import android.nfc.Tag;
import android.util.Log;

import com.vuexpro.service.HTTPRequestHelper;

public abstract class Device  {
	
	public abstract void sync();
	
	public static final String TYPE_NVR ="nvr";
	public static final String TYPE_IPCAM ="ipcam";

	transient protected ArrayList<Channel> mChannels;

	private Profile mProfile;
	private String mName;
	private String mHost;
	private String mType;
	
	
	private Properties CommonInfo;
	private Properties DIO;
	private Properties Preset;

	public Properties getCommonInfo() {
		return CommonInfo;
	}

	public void setCommonInfo(Properties commonInfo) {
		CommonInfo = commonInfo;
	}

	public Properties getDIO() {
		return DIO;
	}

	public void setDIO(Properties dIO) {
		DIO = dIO;
	}

	public Properties getPreset() {
		return Preset;
	}

	public void setPreset(Properties preset) {
		Preset = preset;
	}
	public Device(){
		mChannels= new ArrayList<Channel>();
	}

	public Device(String name, String connectionString) {
		this.mName = name;
		this.mHost = connectionString;
	}

	public void addChannel(Channel c){
		mChannels.add(c);
	}
	public boolean removeChannel(Channel c){
		return mChannels.remove(c);
	}
	public Channel getChannel(int index){
		return mChannels.get(index);
	}
	public ArrayList<Channel> getChannels(){
		return mChannels;
	}
	public String getName() {
		return mName;
	} 
	public void setName(String mName) {
		this.mName = mName;
	}
	public String getHost() {
		return mHost;
	}
	public void setHost(String host) {
		this.mHost = host;
	}

	public void setType(String typeIpcam) {
		this.mType = typeIpcam ;
	}
	public String getType() {
		return this.mType;
	}

	public String getChannelKey(Channel c) {
		return this.mChannels.indexOf(c) + "";
	}

	public Profile getProfile() {
		return mProfile;
	}

	public void setProfile(Profile mProfile) {
		this.mProfile = mProfile;
	}
	@Override
	public String toString(){
		String returnString="";
		for (Channel c: this.getChannels()){
			returnString+= "Device have Channels [getConnectionString:"+c.getConnectionString()+"]" ;
		}
		return returnString;
	}
}
