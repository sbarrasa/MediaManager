package com.blink.mediamanager;


public enum MediaStatus {
	ok,
	err;
	
	private Exception exception;
	

	public Exception getException() {
		return exception;
	}

	
		
	public static MediaStatus err(Exception e) {
		MediaStatus m = err;
		m.exception = e;
		return m;
	}

	public String getMsg() {
		switch (this) {
			case ok: return "Ok";
			case err: return "Err: "+exception.getMessage();
		}
		return null;	
	}
}
