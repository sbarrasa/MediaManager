package com.blink.mediamanager;

import java.io.InputStream;

public class Media {
	private String id;
	private InputStream stream;
	
	public Media() {
		
	}
	
	public Media(String id, InputStream stream) {
		this.id = id;
		this.stream = stream;
	}
		
	public InputStream getStream() {
		
		return stream;
	}
	
	public Media setStream(InputStream stream) {
		this.stream = stream;
		return this;
	}
	public String getId() {
		return id;
	}
	public Media setId(String id) {
		this.id = id;
		return this;
	}

	public String toString() {
		return id;
	}
	
}
