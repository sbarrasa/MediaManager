package com.blink.s3api.controller;

import com.amazonaws.services.s3.model.S3ObjectSummary;import com.blink.s3api.service.AmazonS3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
public class S3Controller {

    @Autowired
    private AmazonS3Service amazonS3Service;


    @ResponseBody
    @RequestMapping(path = "/upload", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String upload(@RequestPart() MultipartFile file) throws IOException {
        return amazonS3Service.upload(amazonS3Service.convert(file));
    }

    @GetMapping("delete/{id}")
    @ResponseBody
    public void delete(@PathVariable String id) {
        amazonS3Service.delete(id);
    }


    @GetMapping(value = "download/{filename}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ResponseBody
    public FileSystemResource download(@PathVariable String id) {

        return new FileSystemResource(amazonS3Service.getFullPath(id));
        //	return amazonS3Service.getMediaURL(id);
    }

    @GetMapping("listall_metadata")
    @ResponseBody
    public List<S3ObjectSummary> listallMetadata() {
        return amazonS3Service.listAllMetadata();
    }

    @GetMapping("listall")
    @ResponseBody
    public List<String> listall() {
        return amazonS3Service.listAll();
    }

}
