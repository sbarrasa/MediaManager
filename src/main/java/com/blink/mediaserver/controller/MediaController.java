package com.blink.mediaserver.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.blink.mediamanager.MediaTemplate;
import com.blink.mediamanager.s3.S3Service;

import java.io.IOException;
import java.util.List;

@Controller
public class MediaController {

    @Autowired
    private MediaTemplate mediaTemplate;


    @ResponseBody
    @RequestMapping(path = "/upload", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String upload(@RequestPart() MultipartFile file) throws IOException {
        return mediaTemplate.upload(S3Service.toFile(file)); //FIXIT: hacer que no necesite convetir con algo propio de spring
    }

    @GetMapping("delete/{id}")
    @ResponseBody
    public void delete(@PathVariable String id) {
        mediaTemplate.delete(id);
    }


    @GetMapping(value = "get/{filename}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ResponseBody
    public FileSystemResource get(@PathVariable String id) {

        return new FileSystemResource(mediaTemplate.getFullPath(id));
    }

    @GetMapping("listall_metadata")
    @ResponseBody
    public List<?> listallMetadata() {
        return mediaTemplate.listAllMetadata(); 
    }

    @GetMapping("listall")
    @ResponseBody
    public List<String> listall() {
        return mediaTemplate.listAllFullPath();
    }

}
