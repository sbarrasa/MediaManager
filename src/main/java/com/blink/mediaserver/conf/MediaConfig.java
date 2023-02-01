package com.blink.mediaserver.conf;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.blink.mediamanager.MediaError;
import com.blink.mediamanager.MediaTemplate;
import com.blink.mediamanager.local.MediaLocal;
import com.blink.mediamanager.s3.MediaS3;

@Configuration
public class MediaConfig {
	@Autowired
    private ApplicationContext applicationContext;
	
	@SuppressWarnings("static-method")
	@Bean
	public MediaLocal localMedia() {
		return new MediaLocal();
	}
	
	@SuppressWarnings("static-method")
	@Bean
    public MediaS3 s3(@Value("${aws.access.key.id}") String accessKey,
    	    @Value("${aws.secret.access.key}") String secretKey,
    	    @Value("${aws.s3.region}") String region,
    	    @Value("${aws.s3.bucket.name}") String bucket,
    	    @Value("${com.blink.mediamanager.server.path}") String path) {

		return new MediaS3()
					.setAccessKey(accessKey)
					.setSecretKey(secretKey)
					.setBucket(bucket)
					.setRegion(region)
					.setPath(path);
		
		}
	
	
	@Bean 
	public MediaTemplate mediaTemplate(@Value("${com.blink.mediamanager.class}") String className) {
		try {
			
			return (MediaTemplate) applicationContext.getBean(Class.forName(className));
			
		} catch (Exception e) {
			throw new MediaError(String.format("Error when trying to instantiate MediaTemplate class %s", className));
		}
	}
	
	
	
	

}
