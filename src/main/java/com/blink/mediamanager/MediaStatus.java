package com.blink.mediamanager;

public enum MediaStatus {
	ok,
	err;
	
	private Exception exception;
	
	private MediaStatus() {
		
	}
	
	MediaStatus(Exception e) {
		this.setException(e);
	}

	public Exception getException() {
		return exception;
	}

	public void setException(Exception exception) {
		this.exception = exception;
	}
}
