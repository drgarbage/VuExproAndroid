package com.vuexpro.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

import android.content.Context;
import android.util.Log;


public class Core implements Serializable{

	public static final String TAG = "nevin";
	public static final String DUMMY_PROFILE = "DUMMY_PROFILE";
	
	private static final long serialVersionUID = 1L;
	
	private static Core mCore;
	transient private static Context mContext;
	
	private ArrayList<Profile> mProfiles;
	private static Profile mDummyProfile;
	transient private Device mDummyDevice;
	
	private class DummyDevice extends Device {
		@Override
		public void sync() {
			 
		}

	}
	
	private Core(){
		
		if (mProfiles==null){
			mProfiles = new ArrayList<Profile>();
		}
		mDummyDevice = new DummyDevice();
		mDummyProfile = new Profile();
		mDummyProfile.setDevice(mDummyDevice);
		mDummyDevice.setProfile(mDummyProfile);
	}
	public static void setContext(Context context){
		mContext = context;
	}
	public void addProfile(Profile object){
		mProfiles.add(object);
	}
	public void removeProfile(Profile object){
		mProfiles.remove(object);		
	}
	public ArrayList<Profile> getProfiles() {
		return mProfiles;
	}
	public Profile getProfile(int index) {
		if(index < 0){
			if (mDummyProfile == null)
				mDummyProfile = new Profile();
			if (mDummyProfile.getDevice()==null){
				mDummyDevice = new DummyDevice();
				mDummyProfile.setDevice(mDummyDevice);
				mDummyDevice.setProfile(mDummyProfile);}
			return mDummyProfile;
		}else
			return mProfiles.get(index);
	}
	public Profile getProfile(String key) {
		if (key==null) return null;
		if(key.equals(DUMMY_PROFILE)) {
			return mDummyProfile;
		} else {
			int index = Integer.valueOf(key);
			return mProfiles.get(index);
		}
	}
	public int indexOfProfile(Profile profile){
		return mProfiles.indexOf(profile);
	}
	public void setProfiles(ArrayList<Profile> mProfiles) {
		this.mProfiles = mProfiles;
	}

	public static synchronized Core getCoreInstance(){
		if (mCore==null){
			mCore = new Core();
		} 
		return mCore;

	}

	/////////////////////////////////////////////////////////////////
	private static String fileName = "core.ser";
	public static void save(){
		FileOutputStream fos = null ;
		ObjectOutputStream os = null ;
		try {
			fos = mContext.openFileOutput(fileName, Context.MODE_PRIVATE);
			
			if (fos==null){
				Log.e(TAG," Core save but null file");
				return;
			}
			os = new ObjectOutputStream(fos);
			os.writeObject(mCore);
			Log.d(TAG," Core saved!");
		} catch (FileNotFoundException e) {
			Log.e(TAG," ClassNotFoundException"+e.toString());
			e.printStackTrace();
		} catch (IOException e) {
			Log.e(TAG," IOException"+e.toString());
			e.printStackTrace();
		}finally{
			try {
				if (fos!=null)
					fos.close();
				if (os!=null)
					os.close();
			} catch (IOException e) {
				Log.e(TAG,"IOException closing os "+e);
			}
		}
	}
	@Override
	public String toString(){
		String returnString="";
		for (Profile p : mCore.getProfiles()){
			returnString+= "\nProfile [Name:"+p.getName()+"] [Host:"+p.getHost()+"] [Type:"+p.getType()+"]" ;
		}
		return returnString;
	}
	public static void load() {
		ObjectInputStream is = null ;
		try {
			FileInputStream fis = mContext.openFileInput(fileName);
			 is = new ObjectInputStream(fis);
			mCore  = (Core) is.readObject();
			Log.d(TAG," Core load!" + mCore.toString());
		} catch (FileNotFoundException e) {
			Log.e(TAG," File Not Found ExceptionS:"+e.toString());
			e.printStackTrace();
		} catch (IOException e) {
			Log.e(TAG," IOExceptionS :"+e.toString());
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			Log.e(TAG," ClassNotFoundExceptionS:"+e.toString());
			e.printStackTrace();
		
		}finally{
			try {
				if (is!=null)
					is.close();
			} catch (IOException e) {
				Log.e(TAG," IOException at close "+e.toString());
			}
		}
		/*
		for (Profile p : mCore.getProfiles()){
			//p.setDevice(DeviceHelper.getSingleton().createDevice(p));
		}
		*/

	}

	/*
	public static int CURRENT_PROFILE_IDX;
	public static int CURRENT_DEVICE_IDX;
	public static int CURRENT_CHANNEL_IDX;
	*/
	
	/*
	private void onProfileListChanged(){
		// update mDummyProfile
		mDummyDevice.getChannels().clear();
		
		for(Profile profile : mProfiles) {
			Device device = profile.getDevice();
			if(device!=null) {
				for(Channel channel : device.getChannels())
				{	Log.d("tmp","this should not be called "+device.getConnectionString()+"@~~~"+device.getChannels().size());
					mDummyDevice.addChannel(channel);
				}
			}
		}
	}*/
	/*
	public ArrayList<Channel> getDlinkDummyDevice(){
		ArrayList<Channel> chs = new ArrayList<Channel>();
		for(int i=0 ; i <mProfiles.size();i++){
			for (int j=0; j <mProfiles.get(i).getDevice().getChannels().size();j++){
				chs.add(mProfiles.get(i).getDevice().getChannels().get(j));
			}
		}
		return chs;
	}
	*/

}
