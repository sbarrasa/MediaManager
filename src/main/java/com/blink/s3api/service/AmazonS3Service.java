package com.blink.s3api.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;

import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.blink.media.MediaService;
import com.blink.s3api.repository.FileMetaRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AmazonS3Service implements MediaService {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private static final String PATH = ".s3.sa-east-1.amazonaws.com/";
			
	@Autowired
    private FileMetaRepository fileMetaRepository;
	
	@Value("${aws.s3.bucket.name}")
    private String BUCKET;

    @Autowired
    private AmazonS3 amazonS3;


    public String upload(
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
        amazonS3.putObject(path, fileName, inputStream, objectMetadata);
        return getFullPath(fileName);
    }

   
    public List<String> listAll(){
        return listAllMetadata()
        		.stream().map(s3 -> s3.getKey()).collect(Collectors.toList());
    }

    public List<S3ObjectSummary> listAllMetadata(){
        return amazonS3.listObjects(BUCKET).getObjectSummaries();
    }

	public String getFullPath(String id) {
		return  "https://%s%s%s".formatted(BUCKET, PATH, fileMetaRepository.findById(id).get().getFileName());
	}

	public String upload(File file) {
		//TODO Not Implemented yet
		return null;
	}

	public String upload(List<File> file) {
		//TODO Not Implemented yet
		return null;
	}

	public File download(String id) {
		//TODO Not Implemented yet
		return null;
	}

	public void delete(String id) {
	     amazonS3.deleteObject(new DeleteObjectRequest(BUCKET, id));
	}
}
