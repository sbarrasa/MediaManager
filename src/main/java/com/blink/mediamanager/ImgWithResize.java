package com.blink.mediamanager;

import java.io.InputStream;
import java.util.Collection;
import java.util.List;

public class ImgWithResize extends Media {
	private Collection<Media> resizes;
	private ImgResizer resizer;
	
	
	public ImgWithResize(String id, InputStream stream, List<Integer> sizes) {
		super(id, stream);
		resizer = new ImgResizer(this, sizes);
	}
	
	public Collection<Media> getResizes() {
		if(resizes == null) {
			resizes = resizer.getAll();
		}
		
		return resizes;
	}
	
	public ImgResizer getResizer() {
		return resizer;
	}

}
