package com.blink.mediamanager;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import com.blink.async.AsyncProcessor;


@SpringBootTest(classes = { com.blink.mediaserver.conf.MediaConfig.class} )
public class MediaUpload {
	
	@Value("${com.blink.mediamanager.source.path}") 
	private String sourcePath;
	@Value("${com.blink.mediamanager.source.class}") 
	private String sourceClass;
	
	private MediaTemplate mediaSource;

	@Value("${com.blink.mediamanager.target.path}") 
	private String targetPath;
	@Value("${com.blink.mediamanager.target.class}") 
	private String targetClass;

	private MediaTemplate mediaTarget;

	
	@Autowired
	public void setMediaSource() {
		this.mediaSource = MediaTemplate.buildMediaTemplate(sourceClass);
		this.mediaSource.setPath(sourcePath);
	}	

	@Autowired
	public void setMediaTarget(){
		this.mediaTarget = MediaTemplate.buildMediaTemplate(targetClass);
		this.mediaTarget.setPath(targetPath);
	}

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final List<Integer> sizes = List.of(ImageResizer.sourceWidth, ImageResizer.thumbnailWidth, 400, 800);
	
	@Test
	public void upload() {
		logger.info("Preparing upload using {}", mediaTarget.getClass().getName());
		
		AsyncProcessor<Media> processor = new AsyncProcessor<>();
		processor.setCallback(this::callback);
		
		mediaSource.listIDs().forEach(id -> {
			logger.info("Getting {}", id);
			try {	
				Media media = mediaSource.get(id);
				try {
					processor.executeAsync(mediaTarget::upload, new ImageResizer(media, sizes).getResizes());
				} catch (MediaException e) {
					processor.executeAsync(mediaTarget::upload, media);
				}

			} catch (MediaException e) {
				logger.error(e.getMessage());
			}
			
		});

	
		processor.syncAll();
		logger.info("End upload {}",mediaTarget.getUploadResult());
		
	}

	private void callback(Media media) {
		switch (media.getStatus()) {
		case err:
			logger.info("{} {}", media.getId(), media.getStatus().getMsg());
			break;
		case added:
		case updated:
			logger.info("{} {} URL={}", media.getId(), media.getStatus(), media.getUrl());
			break;
		default:
			break;
			
		}

	}

	
}
