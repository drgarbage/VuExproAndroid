package com.vuexpro.model.dlink;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.vuexpro.control.UIDeviceViewActivity;
import com.vuexpro.model.Channel;
import com.vuexpro.model.Core;
import com.vuexpro.model.Device;
import com.vuexpro.service.HTTPRequestHelper;


public class DLinkIPCameraDevice extends Device {


	private static final String TAG = "nevin";

	@Override
	public void sync() {
		// Retrieve info from Profile
		this.setName(this.getProfile().getName());
		this.setHost(this.getProfile().getHost());
		this.setType(this.getProfile().getType());
		// Retrieve info from HTTP
		try{
			this.setCommonInfo(new HTTPRequestHelper().execute(this.getHost()+"/common/info.cgi").get());
			this.setPreset(new HTTPRequestHelper().execute(this.getHost()+"/config/ptz_preset_list.cgi").get());
		} catch (Exception e) {
			Log.e(TAG, "InterruptedException @ DeviceFactory : " + e.toString());
		}
		if (this.getCommonInfo()==null){
			Log.e(TAG,"Error in sync , common info is null");
			return;
		}
		// TODO: Specify which camera type of this camera if H264, Create a DlinkIPCameraH264Channel  else a normal Channel will do

//		for(int i = 0; i < 3; i++){
			DLinkIPCameraChannel channel = null ;
			int model = getCamType(this.getCommonInfo());
			switch(model){
			case LINK_TYPE_1 :
				channel = new DLinkIPCameraH264Channel();
				break;
			case LINK_TYPE_2 :
				channel = new DLinkIPCameraMJPEGChannel();
				break;
			case LINK_TYPE_3 :
				channel = new DLinkIPCameraMJPEGChannel();
				break;
			default:	
				channel = new DLinkIPCameraChannel();
			}
			if (channel!=null){
				this.addChannel(channel);
				Core.getCoreInstance().getProfile(-1).getDevice().addChannel(channel);
				channel.setDevice(this);
				channel.active();
				channel.setConnectionString(this.getHost());
			}
//		}

		Log.d(TAG,"DLinkIPCameraDevice Sync ["+this.getChannels().size()+"] channels");
	}


	// =======================Camera Connection Type Lookup =========================
	private static Hashtable<String,Integer> mCamConTypeLookup = new Hashtable<String,Integer>() ;
	private static final int LINK_TYPE_1 = 1 ;
	private static final int LINK_TYPE_2 = 2 ;
	private static final int LINK_TYPE_3 = 3 ;

	private static int getCamType(Properties modelprop){
		if (modelprop==null) return LINK_TYPE_1;
		String model = modelprop.getProperty("model");
		// if empty , initial
		if (mCamConTypeLookup.size()==0){
			// Type 1 
			mCamConTypeLookup.put( "DCS-2312",LINK_TYPE_1);
			mCamConTypeLookup.put( "DCS-2130",LINK_TYPE_1);
			mCamConTypeLookup.put( "DCS-2210",LINK_TYPE_1);
			mCamConTypeLookup.put( "DCS-3710B1",LINK_TYPE_1);
			mCamConTypeLookup.put( "DCS-5222L",LINK_TYPE_1);
			mCamConTypeLookup.put( "DCS-5605",LINK_TYPE_1);
			mCamConTypeLookup.put( "DCS-5635",LINK_TYPE_1);
			mCamConTypeLookup.put( "DCS-6111",LINK_TYPE_1);
			mCamConTypeLookup.put( "DCS-6510",LINK_TYPE_1);
			mCamConTypeLookup.put( "DCS-6511",LINK_TYPE_1);
			mCamConTypeLookup.put( "DCS-942L",LINK_TYPE_1);
			mCamConTypeLookup.put( "DCS-3430",LINK_TYPE_1);
			mCamConTypeLookup.put( "DCS-6818B1",LINK_TYPE_1);
			mCamConTypeLookup.put( "DCS-3411",LINK_TYPE_1);
			mCamConTypeLookup.put( "DCS-7010L",LINK_TYPE_1);
			mCamConTypeLookup.put( "DCS-2132L",LINK_TYPE_1);
			mCamConTypeLookup.put( "DVS-310-1",LINK_TYPE_1);
			// Type 2
			mCamConTypeLookup.put("DCS-932LB1",LINK_TYPE_2);
			mCamConTypeLookup.put("DCS-930L",LINK_TYPE_2);
			mCamConTypeLookup.put("DCS-3110",LINK_TYPE_2);
			// Type 3
			mCamConTypeLookup.put("DCS-5230",LINK_TYPE_3);
			mCamConTypeLookup.put("DCS-7110",LINK_TYPE_3);
			mCamConTypeLookup.put("IP8332",LINK_TYPE_3);
			mCamConTypeLookup.put("DCS-6112V",LINK_TYPE_3);
			mCamConTypeLookup.put("DCS-6113V",LINK_TYPE_3);
			mCamConTypeLookup.put("DCS-6112",LINK_TYPE_3);
			mCamConTypeLookup.put("DCS-6113",LINK_TYPE_3);
			mCamConTypeLookup.put("DCS-3715",LINK_TYPE_3);
			mCamConTypeLookup.put("DCS-6410",LINK_TYPE_3);
			mCamConTypeLookup.put("DCS-3410",LINK_TYPE_3);
			mCamConTypeLookup.put("DCS-7410",LINK_TYPE_3);
			mCamConTypeLookup.put("DCS-7510",LINK_TYPE_3);
		}
		// if not in the list , return default type 
		return mCamConTypeLookup.get(model)==null?LINK_TYPE_1:mCamConTypeLookup.get(model);
	}


}