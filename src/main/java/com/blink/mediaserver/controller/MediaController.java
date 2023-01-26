package com.blink.mediaserver.controller;

import com.blink.mediamanager.MediaException;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.blink.mediamanager.MediaTemplate;
import com.blink.mediamanager.rest.MediaEndpoints;
import com.blink.mediamanager.Media;
import com.blink.mediamanager.MediaError;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

@Controller
public class MediaController implements MediaTemplate {

    @Autowired
    private MediaTemplate mediaTemplate;

    @ResponseBody
    @RequestMapping(path = MediaEndpoints.UPLOAD, method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public URL upload(@RequestPart() MultipartFile multipartFile) throws IOException {

        return mediaTemplate.upload(new Media().setId(multipartFile.getOriginalFilename()).setStream(multipartFile.getInputStream()));
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
    public List<?> listAllMetadata() {
        return mediaTemplate.listAllMetadata();
    }

    @GetMapping(MediaEndpoints.LIST_URLs)
    @ResponseBody
    public List<URL> listURLs() {
        return mediaTemplate.listURLs();
    }

    @GetMapping(MediaEndpoints.LIST_IDS)
    @ResponseBody
    @Override
    public List<String> listIDs() {
        return mediaTemplate.listIDs();
    }


    @GetMapping(MediaEndpoints.REMOTE_URL + "/{id}")
    @ResponseBody
    @Override
    public URL getURL(@PathVariable String id) {
        return mediaTemplate.getURL(id);
    }

    @Override
    public Boolean uploadImpl(Media media) {
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
    public ResponseEntity<?> getEntity(@PathVariable String id) {
        UrlResource resource;
        resource = new UrlResource(mediaTemplate.getURL(id));
        if(!resource.exists())
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok(resource);
    }

    @GetMapping(MediaEndpoints.REMOTE_CHECKSUM + "{id}")
    @ResponseBody
    @Override
    public String getRemoteChecksum(String id) {
        return mediaTemplate.getRemoteChecksum(id);
    }

    


}
