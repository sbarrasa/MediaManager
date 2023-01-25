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
import com.blink.mediamanager.Media;
import com.blink.mediamanager.MediaEndpoints;


import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

@Controller
public class MediaController implements MediaTemplate {

    @Autowired
    private MediaTemplate mediaTemplate;

    @ResponseBody
    @RequestMapping(path = MediaEndpoints.UPLOAD, method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String upload(@RequestPart() MultipartFile multipartFile) throws IOException {

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


    @GetMapping(MediaEndpoints.REMOTE_URL + "/{id}")
    @ResponseBody
    @Override
    public String getURL(@PathVariable String id) {
        return mediaTemplate.getURL(id);
    }

    @Override
    public String getRemoteChecksum(String id) {
        return mediaTemplate.getRemoteChecksum(id);
    }

    @Override
    public Boolean uploadImpl(Media media) {
        return mediaTemplate.uploadImpl(media);
    }

    @GetMapping(MediaEndpoints.GET + "{id}")
    @ResponseBody
    @Override
    public Media get(@PathVariable String id) throws MediaException {
        return mediaTemplate.get(id);
    }

    @GetMapping(MediaEndpoints.GET + "get/{id}")
    @ResponseBody
    public void get(@PathVariable String id, HttpServletResponse r) throws MediaException {

        r.setHeader("Content-Disposition", "attachment; filename=" + id);
        try {
            IOUtils.copy(mediaTemplate.get(id).getStream(), r.getOutputStream());
            r.getOutputStream().flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }



}
