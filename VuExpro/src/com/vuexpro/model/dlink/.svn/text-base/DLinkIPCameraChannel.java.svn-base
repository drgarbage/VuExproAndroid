package com.vuexpro.model.dlink;

import android.util.Log;

import com.vuexpro.control.UIChannelPTZActivity;
import com.vuexpro.model.Channel;
import com.vuexpro.model.StreamSource;
import com.vuexpro.model.sources.HttpMultipartStreamSource;
import com.vuexpro.service.HTTPRequestHelper;

public class DLinkIPCameraChannel extends Channel {
	public static String PARAM_COMMAND_SINGLE_PAN = "single_pan";
	public static String PARAM_COMMAND_SINGLE_PATORL = "single_patrol";
	public static String PARAM_COMMAND_GO_HOME = "go_home";
	/*@Override  don't handle stream source, deletgate to child
	public void setConnectionString(String connectionString) {
		// Implement by child
		super.setConnectionString(connectionString);
		
		String host = connectionString.replace("http://", "");
//		this.SecondaryStream.ConnectionString = "http://" + host + "/video/mjpg.cgi?profileid=1";
		this.SecondaryStream.ConnectionString = "http://" + host + "/video/ACVS-H264.cgi?profileid=2";
	}*/
	@Override
	protected StreamSource preparePrimaryVideoSource() {
		return  new HttpMultipartStreamSource();
	}
	@Override
	protected StreamSource prepareSecondaryVideoSource() {
		return  new HttpMultipartStreamSource();
	}
	public boolean capableOf(String key){
		if (key.equals("pt")){
			return (this.getDevice().getCommonInfo().getProperty("ptz").toLowerCase().contains("p")||this.getDevice().getCommonInfo().getProperty("ptz").toLowerCase().contains("t") );
		}
		if (key.equals("zoom")){
			return (this.getDevice().getCommonInfo().getProperty("ptz").toLowerCase().contains("z") );
		}
		return false;
	}
	@Override
	public void relativePTZ(double pan, double titlt,double zoom){
		if (pan!=0.0 || titlt!=0.0){
			new HTTPRequestHelper().execute(this.getConnectionString()+"/cgi/ptdc.cgi?command=set_relative_pos&posX="+pan*10+"&posY="+titlt*10);
		} else	if (zoom !=0.0){
			new HTTPRequestHelper().execute(this.getConnectionString()+"/cgi/ptdc.cgi?command=set_relative_zoom&zoom_mag="+zoom);
		} else {
			new HTTPRequestHelper().execute(this.getConnectionString()+"/cgi/ptdc.cgi?command=go_home");
		}
	}
	@Override
	public void executeCommand(String command, String param){ 
		new HTTPRequestHelper().execute(this.getConnectionString()+"/cgi/ptdc.cgi?command="+command+param);
		if (command.equals("dioByIO")){
			new HTTPRequestHelper().execute(this.getConnectionString()+"/config/io.cgi&out1="+param);
		}
		if (command.equals("dioByCtrl")){
			new HTTPRequestHelper().execute(this.getConnectionString()+"/dev/gpioCtrl.cgi?out1="+param);
		}
		if (command.equals("dioByVb")){
			new HTTPRequestHelper().execute(this.getConnectionString()+"/vb.htm?language=ie&giooutalwayson="+param);
		}
		if (command.equals("dioBySetdo")){
			new HTTPRequestHelper().execute(this.getConnectionString()+"/cgi-bin/dido/setdo.cgi?do0="+param);
		}
		if (command.equals("presetByID")){
			new HTTPRequestHelper().execute(this.getConnectionString()+"/cgi/ptdc.cgi?command=goto_preset_position&presetId="+param);
		}
		if (command.equals("presetByName")){
			new HTTPRequestHelper().execute(this.getConnectionString()+"/cgi/ptdc.cgi?command=goto_preset_position&presetName="+param);
		}
		if (command.equals("patrol")){
			new HTTPRequestHelper().execute(this.getConnectionString()+"/cgi/ptdc.cgi?command="+param);
		}
		
	}
}