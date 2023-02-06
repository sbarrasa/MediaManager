package com.blink.mediaservice;


import java.util.Collection;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.blink.mediamanager.ImageResizer;
import com.blink.mediamanager.Media;
import com.blink.mediamanager.MediaException;
import com.blink.mediamanager.MediaStatus;
import com.blink.mediamanager.MediaTemplate;
import com.blink.mediamanager.ProcessResult;

public class MediaUpdater {
	private MediaTemplate mediaTarget;
	private ProcessResult<MediaStatus> uploadResult;
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Value("${com.blink.mediamanager.imageresizer.widths}")
	private Set<Integer> imageResizes;
	
	public MediaUpdater() {
		
	}
		
	
		
	public ProcessResult<MediaStatus> uploadFrom(MediaTemplate mediaSource, ProcessResult<MediaStatus> uploadResult){
		this.uploadResult = uploadResult;
		logger.info("Preparing upload using {}", mediaTarget.getClass().getName());
		
		
		Collection<String> mediaIds = mediaSource.listIDs();
		
		uploadResult.setTotal(mediaIds.size());
		
		mediaIds.forEach(id -> {
			logger.info("Getting {}", id);
			try {	
				Media media = mediaSource.get(id);
				try {
					new ImageResizer(media, imageResizes).getResizes()
							.forEach(mediaR -> {
								uploadResult.incTotal();
								upload(mediaR);
							});
				} catch (MediaException e) {
					upload(media);
				}
				uploadResult.incProcessed(media.getStatus());
				
			} catch (MediaException e) {
				logger.error(e.getMessage());
			}
			
		});

	
		logger.info("End upload {}",uploadResult);
		return uploadResult;
		
	}

	private Media upload(Media media) {
		mediaTarget.upload(media);
		showStatus(media);
		uploadResult.incProcessed(media.getStatus());
	
		return media;
	}
	
	
	private void showStatus(Media media) {
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

	public Set<Integer> getImageResizes() {
		return imageResizes;
	}

	public MediaUpdater setImageResizes(Set<Integer> resizes) {
		this.imageResizes = resizes;
		return this;
	}


	public MediaUpdater setTarget(MediaTemplate mediaTarget) {
		this.mediaTarget = mediaTarget;
		return this;
	}

}
