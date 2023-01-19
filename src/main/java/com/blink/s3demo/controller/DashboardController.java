package com.blink.s3demo.controller;

import com.amazonaws.services.s3.model.S3Object;
import com.blink.s3demo.repository.FileMetaRepository;
import com.blink.s3demo.service.MetadataService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
public class DashboardController {


    @Value("${aws.s3.bucket.name}")
    private String bucket;

    @Autowired
    private MetadataService metadataService;

    @Autowired
    private FileMetaRepository fileMetaRepository;

    @GetMapping("dashboard")
    public String dashboard(Model model) {

        var files = metadataService.list();
        model.addAttribute("files", files);
        return "dashboard";
    }

    @PostMapping("upload")
    @ResponseBody
    public String upload(
            @RequestParam("file") MultipartFile file) throws IOException {
        return metadataService.upload(file);
    }

    @GetMapping("delete/{id}")
    public String delete(@PathVariable int id, HttpServletResponse response) {
        metadataService.delete(id);
        return "redirect:/dashboard";
    }

    @GetMapping("download/{id}")
    @ResponseBody
    public void download(Model model, @PathVariable int id, HttpServletResponse response) throws
            IOException {

        //LINK: https://test-blink.s3.sa-east-1.amazonaws.com/8efd1b3a-8eab-4bb7-92a7-5930ead3f11f/HD-wallpaper-among-us-ghost-among-us-among-us-game-among-us-among-us-amongus-ghost-among-us-amongus.jpg

        response.sendRedirect("https://" + bucket + ".s3.sa-east-1.amazonaws.com/" + fileMetaRepository.findById(id).get().getFileName());


    }
}
