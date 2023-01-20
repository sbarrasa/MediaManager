package com.blink.media;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public interface MediaService {
    public String upload(File file);
    public List<String> upload(List<File> file);
    
    public void delete(String id);
    public List<String> listAll();
    
    default public File download(String id) {
    	return null;
    }

    public String getFullPath(String id);
    
    default public URL getMediaURL(String id) throws MalformedURLException {
    	return new URL(getFullPath(id));
    }
}
