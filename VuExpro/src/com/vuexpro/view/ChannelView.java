package com.vuexpro.view;


import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import com.vuexpro.R;
import com.vuexpro.model.Channel;
import com.vuexpro.model.IChannelViewer;

public class ChannelView extends TouchImageView implements
IChannelViewer, Runnable {
	private boolean _forzen = false;
	private Channel _channel = null;

	public ChannelView(Context context) {
		super(context);
		this.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.video_frame));
	}
	public ChannelView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.video_frame));
	}

	public Channel getChannel() { 
		return _channel;
	}
	public void setChannel(Channel channel){
		if (_channel!=null)
			_channel.removeViewer(this);
		_channel = channel;
		onForzenChanged();
	}
	public boolean getForzen(){
		return _forzen;
	}
	public void setForzen(boolean value){
		_forzen = value;
		onForzenChanged();
	}

	//
	// implement interfaces
	//
	@Override
	public void onFrameUpdate(Channel channel) {
		this.post(this);
		 
	}
	@Override
	public void run(){
		ChannelView.this.setImageBitmap(ChannelView.this._channel.snapshot());
	}
	private void onForzenChanged(){
		if (_channel==null) return;
		if(_forzen)
			_channel.removeViewer(this);
		else{
			if (_channel!=null)
				_channel.addViewer(this);
		}
	}
	

}