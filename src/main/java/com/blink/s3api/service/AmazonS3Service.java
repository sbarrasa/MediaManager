package com.blink.s3api.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;

import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.blink.media.MediaService;

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
public class AmazonS3Service implements MediaService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String PATH = ".s3.sa-east-1.amazonaws.com/";


    @Value("${aws.s3.bucket.name}")
    private String BUCKET;

    @Autowired
    private AmazonS3 amazonS3;


    public List<String> listAll() {
        return listAllMetadata()
                .stream().map(S3ObjectSummary::getKey).collect(Collectors.toList());
    }

    public List<S3ObjectSummary> listAllMetadata() {
        return amazonS3.listObjects(BUCKET).getObjectSummaries();
    }

    public String getFullPath(String id) {
        return String.format("https://%s%s%s", BUCKET, PATH, id);
    }

    public String upload(File file) {
        //TODO Not Implemented yet
        String checksum = getCrc32(file);

        if (!fileExistsInRemote(file, checksum)) { //If file is not the same or not exists
            PutObjectRequest request = new PutObjectRequest(BUCKET, file.getName(), file);
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.addUserMetadata("CRC32", checksum);
            request.setMetadata(metadata);
            amazonS3.putObject(request);
        }
        return getFullPath(file.getName());
    }

    public List<String> upload(List<File> file) {
        //TODO Not Implemented yet
        List<String> res = new ArrayList<>();
        file.forEach(file1 -> {
            res.add(upload(file1));
        });
        return res;
    }

    public File download(String id) {
        //TODO Not Implemented yet
        return null;
    }

    public void delete(String id) {
        amazonS3.deleteObject(new DeleteObjectRequest(BUCKET, id));
    }


    private boolean fileExistsInRemote(File file, String crc32) {
        return amazonS3.getObjectMetadata(BUCKET, file.getName()).getRawMetadata().get("CRC32").equals(crc32);
    }

    public File convert(MultipartFile file) {
        File convFile = new File(file.getOriginalFilename());
        try {
            convFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(convFile);
            fos.write(file.getBytes());
            fos.close(); //IOUtils.closeQuietly(fos);
        } catch (IOException e) {
            convFile = null;
        }

        return convFile;
    }

    private String getCrc32(File file) {
        CRC32 crc32 = new CRC32();
        try {
            crc32.update(Files.readAllBytes(file.toPath()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return String.valueOf(crc32.getValue());
    }

}
