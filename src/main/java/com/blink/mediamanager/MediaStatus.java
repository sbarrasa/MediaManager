package com.blink.mediamanager;

import java.net.URL;

public enum MediaStatus {
	ok,
	err;
	
	private Exception exception;
	private URL url;
	

	public Exception getException() {
		return exception;
	}

	public static MediaStatus ok(URL url) {
		MediaStatus m = ok;
		m.url = url;
		return m;
	}

	public static MediaStatus err(Exception e) {
		MediaStatus m = err;
		m.exception = e;
		return m;
	}

	public URL getUrl() {
		return url;
	}
}
