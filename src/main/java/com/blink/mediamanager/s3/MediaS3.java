package com.blink.mediamanager.s3;

import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import com.blink.mediamanager.Media;
import com.blink.mediamanager.MediaError;
import com.blink.mediamanager.MediaException;
import com.blink.mediamanager.MediaStatus;
import com.blink.mediamanager.MediaTemplate;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

public class MediaS3 implements MediaTemplate {

	
    private String PATH; 

    private String BUCKET;

    private AmazonS3 amazonS3;

    
    public MediaS3() {
    	this( System.getProperty("aws.access.key.id"),
    		  System.getProperty("aws.secret.access.key"),
    		  System.getProperty("aws.s3.region"),
    		  System.getProperty("aws.s3.bucket.name"),
    		  System.getProperty("com.blink.mediamanager.mediaserver.path"));
    }

    public MediaS3(String accessKey, 
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
    public List<String> listIDs() {
        return listAllMetadata()
                .stream().map(o -> ((S3ObjectSummary) o).getKey()).collect(Collectors.toList());
    }

    @Override
    public List<?> listAllMetadata() {
        return amazonS3.listObjects(BUCKET).getObjectSummaries();
    }

    @Override
    public URL getURL(String id) {
        try {
			return new URL(String.format("https://%s.%s/%s", BUCKET, PATH, id));
		} catch (MalformedURLException e) {
			throw new MediaError(e);
		}
    }

    @Override
    public Media uploadImpl(Media media) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.addUserMetadata("crc32", getChecksum(media));
        metadata.setContentLength(media.lenght());
        PutObjectRequest request = new PutObjectRequest( BUCKET, media.getId(), media.getStream() , metadata);
        amazonS3.putObject(request);
        return media;
    }

    @Override
    public Media get(String id) throws MediaException {
    	S3Object s3Object ;
    	try {
            s3Object = amazonS3.getObject(BUCKET, id);
            
        } catch (SdkClientException e) {
            throw new MediaException(e);
        }
        return new Media(id, s3Object.getObjectContent());

    }

    @Override
    public void delete(String id) {
       amazonS3.deleteObject(new DeleteObjectRequest(BUCKET, id));
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
