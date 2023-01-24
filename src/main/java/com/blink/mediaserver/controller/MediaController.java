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

@Controller
public class MediaController {

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


    @DeleteMapping("delete/{id}")
    @ResponseBody
    public void delete(@PathVariable String id) throws MediaException {
        mediaTemplate.delete(id);
    }

    @GetMapping(value = MediaEndpoints.GET, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ResponseBody 
    public ResponseEntity<?> get(@PathVariable String filename) {
    	UrlResource resource;
		try {
			resource = new UrlResource(mediaTemplate.getURL(filename));
			if(!resource.exists())
				return ResponseEntity.notFound().build();
				
		} catch (MalformedURLException e) {
			return ResponseEntity.unprocessableEntity().build();
		}
    	return ResponseEntity.ok(resource);
    }

    @GetMapping("listall_metadata")
    @ResponseBody
    public List<?> listallMetadata() {
        return mediaTemplate.listAllMetadata();
    }

    @GetMapping(MediaEndpoints.LISTALL)
    @ResponseBody
    public List<String> listall() {
        return mediaTemplate.listAllFullPath();
    }
    
    @GetMapping(MediaEndpoints.LISTALL_IDS)
    @ResponseBody
    public List<String> listall_ids() {
        return mediaTemplate.listAllIDs();
    }

}
