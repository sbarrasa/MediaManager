package com.blink.mediamanager;


public enum MediaStatus {
	unknown,
	streamLoaded,
	remoteUnchanged,
	remoteUploaded,
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
		String msg = this.name();
		if(this == err
			&& exception != null)
			msg = msg + ": " +exception.getMessage();
		
		return msg;
	}
}
