package com.blink.mediamanager;

import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class Media {
    private String id;
    private InputStream stream;
    private MediaStatus status = MediaStatus.unknown;
    private URL url;
	private Integer lenght;

  
    public Media() {

    }

    public Media(String id, InputStream stream) {
        this.id = id;
        setStream(stream);

    }

    public InputStream getStream() {
        return stream;
    }

    public Media setStream(InputStream stream) {
        ByteArrayOutputStream bstream = new ByteArrayOutputStream();
        try {
            IOUtils.copy(stream, bstream);
	        byte[] bytes = bstream.toByteArray();
	        this.lenght = bytes.length;
	        this.stream = new ByteArrayInputStream(bytes);
	        this.setStatus(MediaStatus.streamLoaded);
        } catch (IOException e) {
            this.setStatus( MediaStatus.err(e));;
        }
        
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

    public MediaStatus getStatus() {
        return status;
    }

    public void setStatus(MediaStatus status) {
        this.status = status;
    }

    public URL getUrl() {
        return url;
    }

    void setUrl(URL url) {
        this.url = url;
    }

	public Integer lenght() {
		return lenght;
	}

}
