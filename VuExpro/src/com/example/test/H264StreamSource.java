package com.example.test;


import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
//import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.vuexpro.model.sources.ISapDataInputStream;

//import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
//import android.graphics.BitmapFactory;
import android.util.Log;

public class H264StreamSource extends ISapDataInputStream{
	private static final String TAG = "nevin";

	private byte [] frameData = null;

	private final static int FRAME_MAX_LENGTH = 1024000;

	private int ulHdrID = 0;
	private int ulHdrLength = 0;
	private int ulDataLength = 0;
	private int ulSequenceNumber = 0;
	private int ulTimeSec = 0;
	private int ulTimeUSec = 0;
	private int ulDataCheckSum = 0;
	private int usCodingType = 0;
	private int usFrameRate = 0;
	private int usWidth = 0;
	private int usHeight = 0;
	private int ucMDBitmap = 0;
	private int ucMDPowers1 = 0;
	private int ucMDPowers2 = 0;
	private int ucMDPowers3 = 0;

	private int nDecoderObj = 0;
	Bitmap mBitmap;

	public native String  printHello();
	public native int H264DecoderOpen();
	public native void H264DecoderClose( int nObj );
	public native void H264DecoderDecodeFrame( int nObj, byte [] array, int arraySize, Bitmap bitmap );
	static 
	{
		System.loadLibrary("DecoderInterface");
	}

	public H264StreamSource(InputStream in) {
		super(new BufferedInputStream(in, FRAME_MAX_LENGTH));
	}

	private int getFrameHeader( DataInputStream in ) throws IOException {
		// Total 40 bytes

		int i = 0;
		int b1 = 0; 
		int b2 = 0; 
		int b3 = 0; 
		int b4 = 0; 

		b1 = in.read(); 
		b2 = in.read(); 
		b3 = in.read(); 
		b4 = in.read(); 
		i = ((0xFF & b4) << 24) | ((0xFF & b3) << 16) | 
				((0xFF & b2) << 8) | (0xFF & b1);
		ulHdrID = i;//in.readInt();

		b1 = in.read(); 
		b2 = in.read(); 
		b3 = in.read(); 
		b4 = in.read(); 
		i = ((0xFF & b4) << 24) | ((0xFF & b3) << 16) | 
				((0xFF & b2) << 8) | (0xFF & b1);
		ulHdrLength = i;//in.readInt();

		b1 = in.read(); 
		b2 = in.read(); 
		b3 = in.read(); 
		b4 = in.read(); 
		i = ((0xFF & b4) << 24) | ((0xFF & b3) << 16) | 
				((0xFF & b2) << 8) | (0xFF & b1);
		ulDataLength = i;//in.readInt();

		b1 = in.read(); 
		b2 = in.read(); 
		b3 = in.read(); 
		b4 = in.read(); 
		i = ((0xFF & b4) << 24) | ((0xFF & b3) << 16) | 
				((0xFF & b2) << 8) | (0xFF & b1);
		ulSequenceNumber = i;//in.readInt();

		b1 = in.read(); 
		b2 = in.read(); 
		b3 = in.read(); 
		b4 = in.read(); 
		i = ((0xFF & b4) << 24) | ((0xFF & b3) << 16) | 
				((0xFF & b2) << 8) | (0xFF & b1);
		ulTimeSec = i;//in.readInt();

		b1 = in.read(); 
		b2 = in.read(); 
		b3 = in.read(); 
		b4 = in.read(); 
		i = ((0xFF & b4) << 24) | ((0xFF & b3) << 16) | 
				((0xFF & b2) << 8) | (0xFF & b1);
		ulTimeUSec = i;//in.readInt();

		b1 = in.read(); 
		b2 = in.read(); 
		b3 = in.read(); 
		b4 = in.read(); 
		i = ((0xFF & b4) << 24) | ((0xFF & b3) << 16) | 
				((0xFF & b2) << 8) | (0xFF & b1);
		ulDataCheckSum = i;//in.readInt();

		b1 = in.read(); 
		b2 = in.read(); 
		i = ((0xFF & b2) << 8) | (0xFF & b1);
		usCodingType = i;//in.readInt();

		b1 = in.read(); 
		b2 = in.read(); 
		i = ((0xFF & b2) << 8) | (0xFF & b1);
		usFrameRate = i;//in.readInt();

		b1 = in.read(); 
		b2 = in.read(); 
		i = ((0xFF & b2) << 8) | (0xFF & b1);
		usWidth = i;//in.readShort();

		b1 = in.read(); 
		b2 = in.read(); 
		i = ((0xFF & b2) << 8) | (0xFF & b1);
		usHeight = i;//in.readShort();


		ucMDBitmap =  in.read();
		ucMDPowers1 = in.read();
		ucMDPowers2 = in.read();
		ucMDPowers3 = in.read();

		return 0;
	}

	private int getFrameData( DataInputStream in, byte [] buffer, int nGetLen ) throws IOException {

		in.readFully( buffer, 0, nGetLen );
		return 0;
	}

	public Bitmap getCurrentFrame() throws IOException {
		mark( FRAME_MAX_LENGTH );

		getFrameHeader( this );

		if( frameData == null )
		{
			frameData = new byte[ FRAME_MAX_LENGTH ];
		}

		getFrameData( this, frameData, ulDataLength );

		if( ulHdrID == -184483840 ) // video 0xF5010000
		{
			//BitmapFactory.

			if( usCodingType == 0 || usCodingType == 10 ) // I Frame
			{
//				Log.d( TAG, "I Frame " + ulDataLength );

				if( nDecoderObj == 0 ) // open decoder, should call H264DecoderClose while you don't need anymore.
				{
					nDecoderObj = H264DecoderOpen();
//					Log.d(TAG,"-----createBitmap start---"+mBitmap);
					mBitmap = Bitmap.createBitmap( usWidth, usHeight, Bitmap.Config.ARGB_8888 );
//					Log.d(TAG,"-----createBitmap end  ---"+mBitmap);
				}

				if( nDecoderObj != 0 )
				{
//					Log.d(TAG,"-----H264DecoderDecodeFrame I start---"+mBitmap);
					H264DecoderDecodeFrame( nDecoderObj, frameData, ulDataLength, mBitmap );
//					Log.d(TAG,"-----H264DecoderDecodeFrame I end  ---"+mBitmap);
					return mBitmap;
				}

			} 

			else if( usCodingType == 1 || usCodingType == 11 ) // P Frame
			{
//				Log.d( TAG, "P Frame " + ulDataLength );

				if( nDecoderObj != 0 )
				{
//					Log.d(TAG,"-----H264DecoderDecodeFrame p start ---"+mBitmap);
					H264DecoderDecodeFrame( nDecoderObj, frameData, ulDataLength, mBitmap );
//					Log.d(TAG,"-----H264DecoderDecodeFrame p end   ---"+mBitmap);
					return mBitmap;
				}
			}

			return null;
		}

		/*		
		int headerLen = getStartOfSequence(this, SOI_MARKER);
		reset();
		byte[] header = new byte[headerLen];
		readFully(header);
		try {
			mContentLength = parseContentLength(header);
		} catch (NumberFormatException nfe) { 
			nfe.getStackTrace();
			Log.d(TAG, "catch NumberFormatException hit", nfe);
			mContentLength = getEndOfSeqeunce(this, EOF_MARKER); 
		}
		reset();
		byte[] frameData = new byte[mContentLength];      

		skipBytes(headerLen);
		readFully(frameData);
		 */

		return null;
		//return BitmapFactory.decodeStream(new ByteArrayInputStream(frameData));
	}
	@Override
	public void bye() { 
		Log.d(TAG,"-----bye---");
		this.H264DecoderClose(nDecoderObj);
	}

}