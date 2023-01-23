package com.blink.mediamanager.s3;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;

import com.blink.mediamanager.MediaException;
import com.blink.mediamanager.MediaTemplate;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class S3Service implements MediaTemplate {

	
    private String PATH; 

    private String BUCKET;

    private AmazonS3 amazonS3;


    public S3Service(String accessKey, 
		    		String secretKey, 
		    		String region, 
		    		String bucket, 
		    		String path) {

    	this.BUCKET = bucket;
    	this.PATH = path;
    	
    	AWSCredentials awsCredentials =
                new BasicAWSCredentials(accessKey, secretKey);
        
    	amazonS3 = AmazonS3ClientBuilder
                .standard()
                .withRegion(region)
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .build();

	}

	@Override
    public List<String> listAllIDs() {
        return listAllMetadata()
                .stream().map(o -> ((S3ObjectSummary) o).getKey()).collect(Collectors.toList());
    }

    @Override
    public List<?> listAllMetadata() {
        return amazonS3.listObjects(BUCKET).getObjectSummaries();
    }

    @Override
    public String getURL(String id) {
        return String.format("https://%s.%s/%s", BUCKET, PATH, id);
    }

    @Override
    public Boolean upload(File file, String checksum) {
        PutObjectRequest request = new PutObjectRequest(BUCKET, file.getName(), file);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.addUserMetadata("crc32", checksum);
        request.setMetadata(metadata);
        amazonS3.putObject(request);
        return true;
    }

    @Override
    public File getFile(String id) throws MediaException {
        File file = new File(id);
        try {
            S3Object s3Object = amazonS3.getObject(BUCKET, id);
            
            FileUtils.copyInputStreamToFile(s3Object.getObjectContent(), file);
        } catch (SdkClientException | IOException e) {
            throw new MediaException(e);
        }
        return file;

    }

    @Override
    public void delete(String id) throws MediaException {
        try {
            amazonS3.deleteObject(new DeleteObjectRequest(BUCKET, id));
        } catch (AmazonServiceException e) {
            throw new MediaException(e.getMessage());
        }

    }


    @Override
    public String getRemoteChecksum(String id) {
        try {
            return amazonS3.getObject(BUCKET, id).getObjectMetadata().getUserMetadata().get("crc32");
        } catch (Exception e) {
            return null;
        }
    }


		
	
}
