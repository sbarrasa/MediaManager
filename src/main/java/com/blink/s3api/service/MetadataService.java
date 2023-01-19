package com.blink.s3api.service;

import com.blink.s3api.model.FileMeta;
import com.blink.s3api.repository.FileMetaRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class MetadataService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
    private AmazonS3Service amazonS3Service;

    @Autowired
    private FileMetaRepository fileMetaRepository;

    @Value("${aws.s3.bucket.name}")
    private String bucketName;

    public String upload(MultipartFile file) throws IOException {

        if (file.isEmpty())
            logger.error("[Service] Cannot upload empty file");

        Map<String, String> metadata = new HashMap<>();
        metadata.put("Content-Type", file.getContentType());
        metadata.put("Content-Length", String.valueOf(file.getSize()));

        //String fileName = String.format("%s", UUID.randomUUID() + "." +StringUtils.getFilenameExtension(file.getOriginalFilename()));
        String fileName = String.format("%s", file.getOriginalFilename());

        //calculate crc32
        CRC32 crc32 = new CRC32();
        crc32.update(file.getBytes());

        // Uploading file to s3
        try {
            logger.info("[S3] Uploading file " + fileName + " with " + crc32.getValue() + " as CRC32");
            amazonS3Service.upload(
                    bucketName, fileName, Optional.of(metadata), file.getInputStream());
        } catch (Exception e){
            logger.error("[S3] Error in s3 upload - check stacktrace!");
            e.printStackTrace();
            return "FAILED: " + e.getMessage();
        }


        // Saving metadata to db
        logger.info("[DB] Saving file name and CRC32 into database..");
        fileMetaRepository.save(new FileMeta(fileName, crc32.getValue()));
        return "https://" + bucketName + ".s3.sa-east-1.amazonaws.com/" + fileName;
    }


    public String delete(String id) {
        FileMeta fileMeta;
        try {
            logger.info("[S3] Trying to delete file " + id);
            fileMeta = fileMetaRepository.findById(id).orElseThrow(EntityNotFoundException::new);
            amazonS3Service.delete(bucketName, fileMeta.getFileName());

        } catch (EntityNotFoundException e){
            logger.warn("[S3] File {} not found! - maybe a typo?", id );
            return "NOT_FOUND";
        }
        logger.info("[DB] Deleting file name and CRC32 from database..");
        fileMetaRepository.delete(fileMeta);
        return "OK";
    }


    public List<FileMeta> list() {
        List<FileMeta> metas = new ArrayList<>();
        fileMetaRepository.findAll().forEach(metas::add);
        return metas;
    }
}