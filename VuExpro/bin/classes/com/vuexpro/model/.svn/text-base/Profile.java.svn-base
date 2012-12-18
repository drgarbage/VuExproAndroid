package com.vuexpro.model;

import java.io.Serializable;
import java.util.Properties;

import com.vuexpro.service.HTTPRequestHelper;

public class Profile implements Serializable {
	private static final long serialVersionUID = 1L;
	
	transient private Device mDevice ; 
	
	public static final String TYPE_NVR ="nvr";
	public static final String TYPE_IPCAM ="ipcam";

	private String mName;
	private String mHost;
	private String mType;
	
	public Profile(){
	}
	public Profile(Device device){
		mDevice = device;
	}
	public Device getDevice() {
		return mDevice;
	}
	public void setDevice(Device mDevice) {
		this.mDevice = mDevice;
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
	public String getType() {
		return this.mType;
	}
	public void setType(String typeIpcam) {
		this.mType = typeIpcam ;
	}
	
}
