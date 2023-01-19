package com.blink.s3demo.service;

import com.amazonaws.services.s3.model.PutObjectResult;
import com.blink.s3demo.Util;
import com.blink.s3demo.model.FileMeta;
import com.blink.s3demo.repository.FileMetaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.zip.CRC32;

@Service
@Slf4j
public class MetadataServiceImpl implements MetadataService {

    @Autowired
    private AmazonS3Service amazonS3Service;

    @Autowired
    private FileMetaRepository fileMetaRepository;

    @Value("${aws.s3.bucket.name}")
    private String bucketName;

    @Override
    public String upload(MultipartFile file) throws IOException {

        if (file.isEmpty())
            throw new IllegalStateException("Cannot upload empty file");

        Map<String, String> metadata = new HashMap<>();
        metadata.put("Content-Type", file.getContentType());
        metadata.put("Content-Length", String.valueOf(file.getSize()));

        String fileName = String.format("%s", UUID.randomUUID() + Util.getExtensionByStringHandling(file.getOriginalFilename()).get());

        // Uploading file to s3
        PutObjectResult putObjectResult = amazonS3Service.upload(
                bucketName, fileName, Optional.of(metadata), file.getInputStream());

        //calculate crc32
        CRC32 crc32 = new CRC32();
        crc32.update(file.getBytes());

        // Saving metadata to db
        fileMetaRepository.save(new FileMeta(fileName, crc32.getValue()));
        return "https://" + bucketName + ".s3.sa-east-1.amazonaws.com/" + fileName;
    }

    @Override
    public void delete(int id) {
        FileMeta fileMeta = fileMetaRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        amazonS3Service.delete(bucketName, fileMeta.getFileName());
        fileMetaRepository.delete(fileMeta);

    }

    @Override
    public List<FileMeta> list() {
        List<FileMeta> metas = new ArrayList<>();
        fileMetaRepository.findAll().forEach(metas::add);
        return metas;
    }
}
