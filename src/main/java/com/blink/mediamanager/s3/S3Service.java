package com.blink.mediamanager.s3;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;

import com.blink.mediamanager.MediaException;
import com.blink.mediamanager.MediaTemplate;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class S3Service implements MediaTemplate {

	public static final String PROPERTY_BUCKET = "aws.s3.bucket.name";
	
    private String PATH; 

    @Value("${aws.s3.bucket.name}")
    private String BUCKET;

    @Autowired
    private AmazonS3 amazonS3;


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

	public String getPATH() {
		return PATH;
	}

	public S3Service setPATH(String PATH) {
		this.PATH = PATH;
		return this;
	}

	public String getBUCKET() {
		return BUCKET;
	}

	public S3Service setBUCKET(String BUCKET) {
		this.BUCKET = BUCKET;
		return this;

	}

		
	public S3Service() {
		this.BUCKET = System.getProperty(PROPERTY_BUCKET);
			
		this.PATH = System.getProperty(PROPERTY_PATH);
	}
	
}
