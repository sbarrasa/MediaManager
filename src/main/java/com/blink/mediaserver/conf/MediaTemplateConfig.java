package com.blink.mediaserver.conf;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.blink.mediamanager.MediaError;
import com.blink.mediamanager.MediaTemplate;
import com.blink.mediamanager.s3.S3Service;

@Configuration
public class MediaTemplateConfig {

	
    @SuppressWarnings("static-method")
	@Bean
    public AmazonS3 s3(
    	    @Value("${aws.access.key.id}") String accessKey,
    	    @Value("${aws.secret.access.key}") String secretKey,
    	    @Value("${aws.s3.region}") String region) {
        AWSCredentials awsCredentials =
                new BasicAWSCredentials(accessKey, secretKey);
        
        
        return AmazonS3ClientBuilder
                .standard()
                .withRegion(region)
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .build();
    }
       
	
	@SuppressWarnings({ "static-access", "static-method" })
	@Bean 
	MediaTemplate mediaTemplate(@Value("${com.blink.mediamanager.class}") String className,
			@Value("${com.blink.mediamanager.mediaserver.path}") String path) {
		try {
		    System.setProperty("com.blink.mediamanager.mediaserver.path", path);
		     
			return (MediaTemplate) Class.forName(className).getDeclaredConstructor().newInstance();
		} catch (Exception e) {
			throw new MediaError("Error when trying to instantiate MediaTemplate class %s".format(className));
		}
	}

}
