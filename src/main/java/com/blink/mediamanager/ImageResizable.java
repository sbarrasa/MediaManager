package com.blink.mediamanager;

import java.io.InputStream;
import java.util.Collection;
import java.util.List;

public class ImageResizable extends Media {
	private ImageResizer resizer;
	
	public ImageResizable(String id, InputStream stream) {
		super(id, stream);
		
		resizer = new ImageResizer(this);
	}
	
	public ImageResizable(String id, InputStream stream, List<Integer> sizes) {
		super(id, stream);
		resizer = new ImageResizer(this, sizes);
	}
	
	
	public ImageResizer getResizer() {
		return resizer;
	}

}
