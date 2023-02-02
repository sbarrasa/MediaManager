package com.blink.mediaserver.controller;

import com.blink.mediamanager.MediaException;
import com.blink.mediamanager.MediaStatus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.blink.mediamanager.MediaTemplate;
import com.blink.mediamanager.MediaUploader;
import com.blink.mediamanager.rest.MediaEndpoints;
import com.blink.mediamanager.AbstractMediaTemplate;
import com.blink.mediamanager.Media;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Controller
public class MediaController extends AbstractMediaTemplate {

    @Autowired
    private MediaTemplate mediaTemplate;

    @ResponseBody
    @RequestMapping(path = MediaEndpoints.UPLOAD, method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public URL upload(@RequestPart() MultipartFile multipartFile) throws IOException {
    	Media media = mediaTemplate.upload(new Media().setId(multipartFile.getOriginalFilename()).setStream(multipartFile.getInputStream()));
    	return media.getUrl();
    }

    @DeleteMapping("delete_all")
    @ResponseBody
    public void deleteAll() {
        mediaTemplate.delete(mediaTemplate.listIDs());
    }

    @DeleteMapping(MediaEndpoints.DELETE + "/{id}")
    @ResponseBody
    @Override
    public void delete(@PathVariable String id) throws MediaException {
        mediaTemplate.delete(id);
    }


    @GetMapping(MediaEndpoints.LISTALL_METADATA)
    @ResponseBody
    @Override
    public Collection<?> listAllMetadata() {
        return mediaTemplate.listAllMetadata();
    }

    @GetMapping(MediaEndpoints.LIST_URLs)
    @ResponseBody
    public Collection<URL> listURLs() {
        return mediaTemplate.listURLs();
    }

    @GetMapping(MediaEndpoints.LIST_IDS)
    @ResponseBody
    @Override
    public Collection<String> listIDs() {
        return mediaTemplate.listIDs();
    }


    @GetMapping(MediaEndpoints.REMOTE_URL + "/{id}")
    @ResponseBody
    @Override
    public URL getURL(@PathVariable String id) throws MediaException {
        return mediaTemplate.getURL(id);
    }

    @Override
    public Media uploadImpl(Media media) throws MediaException {
        return mediaTemplate.uploadImpl(media);
    }

    @GetMapping("/get/{id}")
    @ResponseBody
    @Override
    public Media get(@PathVariable String id) throws MediaException {
        return mediaTemplate.get(id);
    }

    @GetMapping(value=(MediaEndpoints.GET + "{id}"), produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ResponseBody
    public ResponseEntity<?> getEntity(@PathVariable String id) throws MediaException {
        UrlResource resource;
        resource = new UrlResource(mediaTemplate.getURL(id));
        if(!resource.exists())
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok(resource);
    }

    @GetMapping(MediaEndpoints.REMOTE_CHECKSUM + "{id}")
    @ResponseBody
    @Override
    public String getServerChecksum(String id) {
        return mediaTemplate.getServerChecksum(id);
    }

    @ResponseBody
    @PostMapping("/upload_all/")
    public Map<MediaStatus, Integer> uploadAll(@Value("${com.blink.mediamanager.source.class}") String sourceClass,
    								@Value("${com.blink.mediamanager.source.path}") String sourcePath,
    								@Value("${com.blink.mediamanager.image.resizes}") Set<Integer> imageResizes){
    								
    		
    	MediaTemplate mediaSource = MediaTemplate.buildMediaTemplate(sourceClass)
				.setPath(sourcePath);
    	
    	return new MediaUploader()
    		.setSource(mediaSource)
    		.setTarget(mediaTemplate)
    		.setImageResizes(imageResizes)
    		.uploadAll();
    	
    }	


}
