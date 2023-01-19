package com.blink.s3api.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Map;
import java.util.Optional;

@Service
public class AmazonS3Service {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private AmazonS3 amazonS3;


    public PutObjectResult upload(
            String path,
            String fileName,
            Optional<Map<String, String>> optionalMetaData,
            InputStream inputStream) {
        ObjectMetadata objectMetadata = new ObjectMetadata();

        optionalMetaData.ifPresent(map -> {
            if (!map.isEmpty()) {
                map.forEach(objectMetadata::addUserMetadata);
            }
        });
        logger.debug("Path: {}, FileName: {} ", path , fileName);
        return amazonS3.putObject(path, fileName, inputStream, objectMetadata);
    }

    public void delete(String bucket, String fileName) {
        amazonS3.deleteObject(new DeleteObjectRequest(bucket, fileName));
    }
}
