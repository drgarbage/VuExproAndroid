package com.vuexpro.model.dlink;

import com.vuexpro.control.UIChannelPTZActivity;
import com.vuexpro.model.Channel;
import com.vuexpro.model.StreamSource;
import com.vuexpro.model.sources.HttpMultipartStreamSource;
import com.vuexpro.service.HTTPRequestHelper;


public class DLinkIPCameraH264Channel extends DLinkIPCameraChannel {
	public void setConnectionString(String connectionString) {
		String host = connectionString.replace("http://", "");
		this.SecondaryStream.ConnectionString = "http://" + host + "//video/ACVS-H264.cgi?profileid=2";
		super.setConnectionString(connectionString);
		///video/ACVS-H264.cgi?profileid=2";
	}
}