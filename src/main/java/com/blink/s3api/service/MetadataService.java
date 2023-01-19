package com.blink.s3api.service;

import com.blink.s3api.model.FileMeta;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface MetadataService {
    public String upload(MultipartFile file) throws IOException;
    public String delete(String id);
    public List<FileMeta> list();
}