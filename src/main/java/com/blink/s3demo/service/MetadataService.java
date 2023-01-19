package com.blink.s3demo.service;

import com.blink.s3demo.model.FileMeta;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface MetadataService {
    public String upload(MultipartFile file) throws IOException;
    public void delete(int id);
    public List<FileMeta> list();
}
