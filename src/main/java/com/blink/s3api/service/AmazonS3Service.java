package com.blink.s3api.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectResult;

import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.blink.s3api.repository.FileMetaRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class AmazonS3Service {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private static final String PATH = ".s3.sa-east-1.amazonaws.com/";
			
	@Autowired
    private FileMetaRepository fileMetaRepository;
	
	@Value("${aws.s3.bucket.name}")
    private static String BUCKET;

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

    public List<S3ObjectSummary> listAll(){
        return amazonS3.listObjects(BUCKET).getObjectSummaries();
    }

	public String getFullPath(String id) {
		return  "https://%s%s%s".formatted(BUCKET, PATH, fileMetaRepository.findById(id).get().getFileName());
	}
}
