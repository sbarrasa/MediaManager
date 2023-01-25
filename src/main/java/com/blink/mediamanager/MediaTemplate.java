package com.blink.mediamanager;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.CRC32;


public interface MediaTemplate  {
	    
	default public String upload(Media media) {

        if (!fileExistsInRemote(media)) {

        	Boolean uploaded = uploadImpl(media);
        	if(!uploaded)
                throw new MediaError(String.format("Can't upload %s", media.getId()));
        		
        }
        return getURL(media.getId());

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
    
    public default String getChecksum(Media media) {
        CRC32 crc32 = new CRC32();
        try {
            crc32.update(media.getStream().readAllBytes());
        } catch (IOException e) {
            throw new MediaError(e);
        }
        return String.valueOf(crc32.getValue());
    }

    public String getRemoteChecksum(String id) ;	

    default public List<String> upload(List<Media> medias) {
        List<String> res = new ArrayList<>();
        medias.forEach(media -> {
            res.add(upload(media));
        });
        return res;
    }

    

    default public boolean fileExistsInRemote(Media media) {
        
    	String remoteChecksum = getRemoteChecksum(media.getId());
        
    	String fileChecksum = getChecksum(media);
          
    	return fileChecksum.equals(remoteChecksum);
    };


    public Boolean uploadImpl(Media media);
    
    public Media get(String id) throws MediaException;
    
        
}
