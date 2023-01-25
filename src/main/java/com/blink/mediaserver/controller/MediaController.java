package com.blink.mediaserver.controller;

import com.blink.mediamanager.MediaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.blink.mediamanager.MediaTemplate;
import com.blink.mediamanager.MediaEndpoints;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import javax.websocket.server.PathParam;

@Controller
public class MediaController implements MediaTemplate{

    @Autowired
    private MediaTemplate mediaTemplate;

    @ResponseBody
    @RequestMapping(path = MediaEndpoints.UPLOAD, method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String upload(@RequestPart() MultipartFile multipartFile) throws IOException {

        return mediaTemplate.upload(toFile(multipartFile));
    }

    private static File toFile(MultipartFile multipartFile) {
        File file = new File(multipartFile.getOriginalFilename());
        try {
            FileOutputStream fos = new FileOutputStream(file);
            try {
                fos.write(multipartFile.getBytes());
            } finally {
                fos.close();
            }
        } catch (IOException e) {
            file = null;
        }

        return file;
    }


    @DeleteMapping(MediaEndpoints.DELETE+"/{id}")
    @ResponseBody
    @Override
    public void delete(@PathVariable String id) throws MediaException {
        mediaTemplate.delete(id);
    }

    @GetMapping(value = "/get/{id}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ResponseBody 
    public ResponseEntity<?> get(@PathVariable String id) {
    	UrlResource resource;
		try {
			resource = new UrlResource(mediaTemplate.getURL(id));
			if(!resource.exists())
				return ResponseEntity.notFound().build();
				
		} catch (MalformedURLException e) {
			return ResponseEntity.unprocessableEntity().build();
		}
    	return ResponseEntity.ok(resource);
    }
    


    @GetMapping(MediaEndpoints.LISTALL_METADATA)
    @ResponseBody
	@Override
	public List<?> listAllMetadata() {
        return mediaTemplate.listAllMetadata();
    }

    @GetMapping(MediaEndpoints.LISTALL)
    @ResponseBody
    public List<String> listall() {
        return mediaTemplate.listAllFullPath();
    }
    
    @GetMapping(MediaEndpoints.LISTALL_IDS)
    @ResponseBody
	@Override
	public List<String> listAllIDs() {
        return mediaTemplate.listAllIDs();
    }


    @GetMapping(MediaEndpoints.REMOTE_URL+"/{id}")
    @Override
	public String getURL(@PathVariable String id) {
		return mediaTemplate.getURL(id);
	}

	@Override
	public String getRemoteChecksum(String id) {
		return mediaTemplate.getRemoteChecksum(id);
	}

	@Override
	public Boolean uploadImpl(File file) {
		return mediaTemplate.uploadImpl(file);
	}

    @GetMapping(MediaEndpoints.GET+"{id}")
	@Override
	public File getFile(@PathVariable String id) throws MediaException {
		return mediaTemplate.getFile(id);
	}



}
