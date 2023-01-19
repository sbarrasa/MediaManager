package com.blink.s3api.controller;

import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.blink.s3api.model.FileMeta;
import com.blink.s3api.repository.FileMetaRepository;
import com.blink.s3api.service.AmazonS3Service;
import com.blink.s3api.service.MetadataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;
import java.util.List;

@Controller
public class S3Controller {

    @Autowired
    private MetadataService metadataService;

    @Autowired
    private AmazonS3Service amazonS3Service;

   
    @ResponseBody
    @RequestMapping(path = "/upload", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String upload(@RequestPart() MultipartFile file) throws IOException {
        return metadataService.upload(file);
    }

    @GetMapping("delete/{id}")
    @ResponseBody
    public FileMeta delete(@PathVariable String id) {
        return metadataService.delete(id);
    }


    @GetMapping("download/{filename}")
    @ResponseBody
    public void download(@PathVariable String id, HttpServletResponse response) throws
            IOException {

        //LINK: https://test-blink.s3.sa-east-1.amazonaws.com/8efd1b3a-8eab-4bb7-92a7-5930ead3f11f/HD-wallpaper-among-us-ghost-among-us-among-us-game-among-us-among-us-amongus-ghost-among-us-amongus.jpg

        response.sendRedirect(amazonS3Service.getFullPath(id));
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
