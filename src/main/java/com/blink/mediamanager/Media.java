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
    private MediaStatus status;
    private URL url;

    private ByteArrayInputStream bis;

    public Media() {

    }

    public Media(String id, InputStream stream) {
        this.id = id;
        this.stream = stream;

    }

    public ByteArrayInputStream getStream() {
        return bis;
    }

    public Media setStream(InputStream stream) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            IOUtils.copy(stream, baos);
        } catch (IOException e) {
            throw new MediaError(e);
        }
        byte[] bytes = baos.toByteArray();

        this.bis = new ByteArrayInputStream(bytes);
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

    void setStatus(MediaStatus status) {
        this.status = status;
    }

    public URL getUrl() {
        return url;
    }

    void setUrl(URL url) {
        this.url = url;
    }

}
