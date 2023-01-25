package com.blink.mediamanager;

public enum MediaStatus {
	ok,
	err;
	
	private Exception exception;
	private String link;
	

	public Exception getException() {
		return exception;
	}

	public static MediaStatus ok(String link) {
		MediaStatus m = ok;
		m.link = link;
		return m;
	}

	public static MediaStatus err(Exception e) {
		MediaStatus m = err;
		m.exception = e;
		return m;
	}
}
