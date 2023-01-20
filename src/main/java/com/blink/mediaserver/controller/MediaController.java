package com.blink.mediaserver.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.blink.mediamanager.MediaTemplate;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

@Controller
public class MediaController {

    @Autowired
    private MediaTemplate mediaTemplate;

    @ResponseBody
    @RequestMapping(path = "/upload", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String upload(@RequestPart() MultipartFile multipartFile) throws IOException {

        return mediaTemplate.upload(toFile(multipartFile));
    }

    private File toFile(MultipartFile multipartFile) {
        File file = new File(multipartFile.getOriginalFilename());
        try {
            file.createNewFile();
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


    @GetMapping("delete/{id}")
    @ResponseBody
    public void delete(@PathVariable String id) {
        mediaTemplate.delete(id);
    }

    @GetMapping(value = "get/{filename}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ResponseBody
    public String get(@PathVariable String filename) {
        return mediaTemplate.getFullPath(filename);
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
