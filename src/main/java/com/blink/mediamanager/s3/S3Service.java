package com.blink.mediamanager.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;

import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.blink.mediamanager.MediaTemplate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.CRC32;

@Service
public class S3Service implements MediaTemplate {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

//FIXIT: exteriorizar a MediaTemplate y generalizar como propertie com.blink.mediamanager.mediaserver.path     
    private static final String PATH = ".s3.sa-east-1.amazonaws.com/";


    @Value("${aws.s3.bucket.name}")
    private String BUCKET;

    @Autowired
    private AmazonS3 amazonS3;


    public List<String> listAllIDs() {
        return listAllMetadata()
                .stream().map(o -> ((S3ObjectSummary)o).getKey()).collect(Collectors.toList());
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
       metadata.addUserMetadata("CRC32", checksum);
       request.setMetadata(metadata);
       amazonS3.putObject(request);
       return true;
    }


    public void delete(String id) {
        amazonS3.deleteObject(new DeleteObjectRequest(BUCKET, id));
    }


    public boolean fileExistsInRemote(File file, String crc32) {
        try {
            return amazonS3.getObjectMetadata(BUCKET, file.getName()).getRawMetadata().get("CRC32").equals(crc32);
        } catch (Exception e){
            return false;
        }
    }

    public static File toFile(MultipartFile multipartFile) {
        File file = new File(multipartFile.getOriginalFilename());
        try {
        	file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            try {
            	fos.write(multipartFile.getBytes());
            }finally {
            	fos.close(); 
            }
        } catch (IOException e) {
            file = null;
        }

        return file;
    }


}
