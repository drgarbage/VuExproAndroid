package com.vuexpro.model.sources;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import android.graphics.Bitmap;

public abstract class ISapDataInputStream extends DataInputStream{

	public ISapDataInputStream(InputStream in) {
		super(in);
	}
	public abstract Bitmap getCurrentFrame() throws IOException;
	public abstract void bye();

}
