package com.blink.s3api.service;

import com.amazonaws.services.s3.model.PutObjectResult;

import java.io.InputStream;
import java.util.Map;
import java.util.Optional;

public interface AmazonS3Service {
    public PutObjectResult upload(
            String path,
            String fileName,
            Optional<Map<String, String>> optionalMetaData,
            InputStream inputStream);

    public void delete(String bucket, String keyName);

}
