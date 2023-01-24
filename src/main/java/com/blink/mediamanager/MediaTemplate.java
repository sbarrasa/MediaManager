package com.blink.mediamanager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.CRC32;


public interface MediaTemplate {
	public static final String PROPERTY_PATH = "com.blink.mediamanager.mediaserver.path";

	    
	default public String upload(File file) {
        String filename = file.getName();

        if (!fileExistsInRemote(file)) {

        	Boolean uploaded = uploadImpl(file);
        	if(!uploaded)
                throw new MediaError(String.format("Can't upload %s", filename));
        		
        }
        return getURL(filename);

    }
    
    
	public void delete(String id) throws MediaException;
    
    default public void delete(List<String> ids) {
        ids.forEach(id -> {
			try {
				delete(id);
			} catch (MediaException ignored) {
			}
		});
    }
    
    default public List<String> listAllFullPath(){
    	return listAllIDs()
                   .stream().map(this::getURL).collect(Collectors.toList());
      
    }
    
    public List<String> listAllIDs();
    
    public List<?> listAllMetadata();
      
    public String getURL(String id);
    
    public default String getChecksum(File file) {
        CRC32 crc32 = new CRC32();
        try {
            crc32.update(Files.readAllBytes(file.toPath()));
        } catch (IOException e) {
            throw new MediaError(e);
        }
        return String.valueOf(crc32.getValue());
    }

    public String getRemoteChecksum(String id) ;	

    default public List<String> upload(List<File> files) {
        List<String> res = new ArrayList<>();
        files.forEach(file -> {
            res.add(upload(file));
        });
        return res;
    }

    

    default public boolean fileExistsInRemote(File file) {
        
    	String remoteChecksum = getRemoteChecksum(file.getName());
        
    	String fileChecksum = getChecksum(file);
          
    	return fileChecksum.equals(remoteChecksum);
    };


    public Boolean uploadImpl(File file);
    
    public File getFile(String id) throws MediaException;
    
        
}
