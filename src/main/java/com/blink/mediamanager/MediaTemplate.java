package com.blink.mediamanager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.CRC32;


public interface MediaTemplate {
    default public String upload(File file) {
        String checksum = MediaTemplate.getCrc32(file);

        if (!fileExistsInRemote(file, checksum)) {
        	Boolean uploaded = upload(file, checksum);
        	if(!uploaded)
                throw new MediaError(String.format("Can't upload %s", file.getName()));
        		
        }
        return getFullPath(file.getName());

    }
    
    public void delete(String id);
    
    default public void delete(List<String> ids) {
        ids.forEach(id -> delete(id));
    }
    
    default public List<String> listAllFullPath(){
    	return listAllIDs()
                   .stream().map(id -> getFullPath(id)).collect(Collectors.toList());
      
    }
    
    public List<String> listAllIDs();
    
    public List<?> listAllMetadata();
      
    public String getFullPath(String id);
    
    public static String getCrc32(File file) {
        CRC32 crc32 = new CRC32();
        try {
            crc32.update(Files.readAllBytes(file.toPath()));
        } catch (IOException e) {
            throw new MediaError(e);
        }
        return String.valueOf(crc32.getValue());
    }
    
    default public List<String> upload(List<File> files) {
        List<String> res = new ArrayList<>();
        files.forEach(file -> {
            res.add(upload(file));
        });
        return res;
    }
    
    public boolean fileExistsInRemote(File file, String crc32) ;

    public Boolean upload(File file, String checksum);
        
  
}
