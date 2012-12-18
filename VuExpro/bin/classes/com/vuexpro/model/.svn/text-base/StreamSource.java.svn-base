package com.vuexpro.model;

import android.graphics.Bitmap;

public abstract class StreamSource implements  Runnable{
	transient private IStreamSourceListener _listener;
	public String ConnectionString;;

	public abstract void connect();
	public abstract void disconnect();
	public void setListenr(IStreamSourceListener istreansourceListener){
		_listener = istreansourceListener;
	}
	protected void onReceiveSegment(Bitmap frame){
		if(_listener == null) return;
		_listener.onReceiveSegment(frame);
	}
	protected void onError(){
		if(_listener == null) return;
		_listener.onError();
	}
	protected void onFinish(){
		if(_listener == null) return;
		_listener.onFinish();
	}
}
