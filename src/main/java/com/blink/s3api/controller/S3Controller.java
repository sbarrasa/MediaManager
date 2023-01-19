package com.blink.s3api.controller;

import com.blink.s3api.repository.FileMetaRepository;
import com.blink.s3api.service.AmazonS3Service;
import com.blink.s3api.service.MetadataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class S3Controller {

    @Value("${aws.s3.bucket.name}")
    private String bucket;

    @Autowired
    private MetadataService metadataService;

    @Autowired
    private AmazonS3Service amazonS3Service;

    @Autowired
    private FileMetaRepository fileMetaRepository;


    @ResponseBody
    @RequestMapping(path = "/upload", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String upload(@RequestPart() MultipartFile file) throws IOException {
        return metadataService.upload(file);
    }

    @GetMapping("delete/{id}")
    @ResponseBody
    public String delete(@PathVariable String id) {
        return metadataService.delete(id);
    }


    @GetMapping("download/{filename}")
    @ResponseBody
    public void download(Model model, @PathVariable String id, HttpServletResponse response) throws
            IOException {

        //LINK: https://test-blink.s3.sa-east-1.amazonaws.com/8efd1b3a-8eab-4bb7-92a7-5930ead3f11f/HD-wallpaper-among-us-ghost-among-us-among-us-game-among-us-among-us-amongus-ghost-among-us-amongus.jpg

        response.sendRedirect("https://" + bucket + ".s3.sa-east-1.amazonaws.com/" + fileMetaRepository.findById(id).get().getFileName());

    }

    @GetMapping("listall")
    @ResponseBody
    public String listall(HttpServletResponse response) {
        List<String> a = new ArrayList<>();
        amazonS3Service.listAll().forEach(s3ObjectSummary -> a.add(s3ObjectSummary.getKey().toString() + " " + s3ObjectSummary.getSize() + " " + s3ObjectSummary.getLastModified() + "<br>"));
        return a.toString();
    }


}
