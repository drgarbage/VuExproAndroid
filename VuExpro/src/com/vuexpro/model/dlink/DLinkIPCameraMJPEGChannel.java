package com.vuexpro.model.dlink;

import com.vuexpro.control.UIChannelPTZActivity;
import com.vuexpro.model.Channel;
import com.vuexpro.model.StreamSource;
import com.vuexpro.model.sources.HttpMultipartStreamSource;
import com.vuexpro.service.HTTPRequestHelper;


public class DLinkIPCameraMJPEGChannel extends DLinkIPCameraChannel {
	@Override
	public void setConnectionString(String connectionString) {
		String host = connectionString.replace("http://", "");
		this.SecondaryStream.ConnectionString = "http://" + host + "/video/mjpg.cgi?profileid=1";
		super.setConnectionString(connectionString);
	}

}