package com.vuexpro.service;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import android.app.Activity;
import android.util.Log;

import com.vuexpro.model.Channel;
import com.vuexpro.model.Core;
import com.vuexpro.model.Device;
import com.vuexpro.model.Profile;
import com.vuexpro.model.dlink.DLinkIPCameraDevice;

public class DeviceFactory {
	static final String TAG = "nevin";
	static final String DEVICE_DLINKIPCAM = "dlinkipcam";



	// =======================public method============================
	public static String checkType(URI host){
		// TODO:Get camera model info , determine whether H264 or MJpeg to use
		return DEVICE_DLINKIPCAM ;
	}	

	public static boolean createDevice(Profile challenger,Profile defender){
		if (challenger.getType().equals(Device.TYPE_IPCAM)){
			DLinkIPCameraDevice device = new DLinkIPCameraDevice();
			challenger.setDevice(device);
			device.setProfile(challenger);
			device.sync();
			// Common Info is required.
			if (challenger.getDevice().getCommonInfo()==null){
				return false;
			}else{
				if (defender!=null){
					for (Channel c : defender.getDevice().getChannels()	)
						c.removeAllViewer();
					defender.setName( challenger.getName());
					defender.setHost(challenger.getHost());
					defender.setType(challenger.getType());
					defender.setDevice(challenger.getDevice());
				}else{
					Core.getCoreInstance().addProfile(challenger);
				}
				
				return true;
			}
		}
		return false;
	}


	// =======================private method============================
	private DeviceFactory(){

	}

	public static void refreshInfo(Channel channel,	Activity mActivity) {
		try {
			channel.getDevice().setDIO(new HTTPRequestHelper(mActivity,"Updating DIO Status").execute(channel.getConnectionString()+"/config/io.cgi").get());
			if (channel.getDevice().getPreset()==null){
				channel.getDevice().setPreset(new HTTPRequestHelper().execute(channel.getDevice().getHost()+"/config/ptz_preset_list.cgi").get());
			}
		} catch (Exception e) {
			Log.e(TAG,"refreshDIO Exception" + e.toString());
		}
	}

}
