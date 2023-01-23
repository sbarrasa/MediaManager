package com.blink.mediamanager.s3;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;

import com.blink.mediamanager.MediaException;
import com.blink.mediamanager.MediaTemplate;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class S3Service implements MediaTemplate {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    //TODO: exteriorizar a MediaTemplate y generalizar como propertie com.blink.mediamanager.mediaserver.path
    private static final String PATH = ".s3.sa-east-1.amazonaws.com/";


    @Value("${aws.s3.bucket.name}")
    private String BUCKET;

    @Autowired
    private AmazonS3 amazonS3;


    public List<String> listAllIDs() {
        return listAllMetadata()
                .stream().map(o -> ((S3ObjectSummary) o).getKey()).collect(Collectors.toList());
    }

    public List<?> listAllMetadata() {
        return amazonS3.listObjects(BUCKET).getObjectSummaries();
    }

    public String getFullPath(String id) {
        return String.format("https://%s%s%s", BUCKET, PATH, id);
    }

    public Boolean upload(File file, String checksum) {
        PutObjectRequest request = new PutObjectRequest(BUCKET, file.getName(), file);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.addUserMetadata("crc32", checksum);
        request.setMetadata(metadata);
        amazonS3.putObject(request);
        return true;
    }

    @Override
    public File getFile(String id) {
        File file = null;
        try {
            S3Object s3Object = amazonS3.getObject(BUCKET, id);
            file = new File(id);
            FileUtils.copyInputStreamToFile(s3Object.getObjectContent(), file);
        } catch (IOException e) {
            return null;
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
