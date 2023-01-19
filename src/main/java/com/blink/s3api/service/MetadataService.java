package com.blink.s3api.service;

import com.blink.s3api.model.FileMeta;
import com.blink.s3api.repository.FileMetaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;


import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.zip.CRC32;

@Service
@Slf4j
public class MetadataService {

    @Autowired
    private AmazonS3Service amazonS3Service;

    @Autowired
    private FileMetaRepository fileMetaRepository;

    @Value("${aws.s3.bucket.name}")
    private String bucketName;

    public String upload(MultipartFile file) throws IOException {

        if (file.isEmpty())
            log.error("[Service] Cannot upload empty file");

        Map<String, String> metadata = new HashMap<>();
        metadata.put("Content-Type", file.getContentType());
        metadata.put("Content-Length", String.valueOf(file.getSize()));

        String fileName = String.format("%s", UUID.randomUUID() + StringUtils.getFilenameExtension(file.getOriginalFilename()));

        //calculate crc32
        CRC32 crc32 = new CRC32();
        crc32.update(file.getBytes());

        // Uploading file to s3
        try {
            log.info("[S3] Uploading file " + fileName + " with " + crc32.getValue() + " as CRC32");
            amazonS3Service.upload(
                    bucketName, fileName, Optional.of(metadata), file.getInputStream());
        } catch (Exception e){
            log.error("[S3] Error in s3 upload - check stacktrace!");
            e.printStackTrace();
            return "FAILED: " + e.getMessage();
        }


        // Saving metadata to db
        log.info("[DB] Saving file name and CRC32 into database..");
        fileMetaRepository.save(new FileMeta(fileName, crc32.getValue()));
        return "https://" + bucketName + ".s3.sa-east-1.amazonaws.com/" + fileName;
    }


    public String delete(String id) {
        FileMeta fileMeta;
        try {
            log.info("[S3] Trying to delete file " + id);
            fileMeta = fileMetaRepository.findById(id).orElseThrow(EntityNotFoundException::new);
            amazonS3Service.delete(bucketName, fileMeta.getFileName());

        } catch (EntityNotFoundException e){
            log.warn("[S3] File " + id + " not found! - maybe a typo?");
            return "NOT_FOUND";
        }
        log.info("[DB] Deleting file name and CRC32 from database..");
        fileMetaRepository.delete(fileMeta);
        return "OK";
    }


    public List<FileMeta> list() {
        List<FileMeta> metas = new ArrayList<>();
        fileMetaRepository.findAll().forEach(metas::add);
        return metas;
    }
}