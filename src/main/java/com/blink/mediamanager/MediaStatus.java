package com.blink.mediamanager;

import java.net.URL;
import java.util.List;

public enum MediaStatus {
	ok,
	err;
	
	private Exception exception;
	private List<URL> urls;
	

	public Exception getException() {
		return exception;
	}

	public static MediaStatus ok(List<URL> urls) {
		MediaStatus m = ok;
		m.urls = urls;
		return m;
	}

	public static MediaStatus err(Exception e) {
		MediaStatus m = err;
		m.exception = e;
		return m;
	}

	public List<URL> getUrls() {
		return urls;
	}
}
