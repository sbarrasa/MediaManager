package com.blink.mediaserver.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.blink.mediamanager.MediaTemplate;
import com.blink.mediamanager.s3.S3Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@Controller
public class MediaController {

    @Autowired
    private MediaTemplate mediaTemplate;


    @ResponseBody
    @RequestMapping(path = "/upload", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String upload(@RequestPart() MultipartFile file) throws IOException {
        return mediaTemplate.upload(toFile(file));
    }

    @GetMapping("delete/{id}")
    @ResponseBody
    public void delete(@PathVariable String id) {
        mediaTemplate.delete(id);
    }


    @GetMapping(value = "get/{filename}")
    public ResponseEntity<Object> get(@PathVariable String id) throws URISyntaxException {
        URI red = new URI(mediaTemplate.getFullPath(id));
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(red);
        return new ResponseEntity<>(httpHeaders, HttpStatus.SEE_OTHER);
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


    public static File toFile(MultipartFile multipartFile) {
        File file = new File(multipartFile.getOriginalFilename());
        try {
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            try {
                fos.write(multipartFile.getBytes());
            }finally {
                fos.close();
            }
        } catch (IOException e) {
            file = null;
        }

        return file;
    }

}
