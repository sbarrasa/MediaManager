package com.blink.mediaserver;


import java.util.EnumMap;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.blink.mediamanager.ImageResizer;
import com.blink.mediamanager.Media;
import com.blink.mediamanager.MediaException;
import com.blink.mediamanager.MediaStatus;
import com.blink.mediamanager.MediaTemplate;

public class MediaUploader {
	private MediaTemplate mediaSource;
	private MediaTemplate mediaTarget;
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Value("${com.blink.mediamanager.imageresizer.widths}")
	private Set<Integer> imageResizes;


	
	public MediaUploader() {
		
	}
		
	public MediaUploader(MediaTemplate mediaSource, MediaTemplate mediaTarget) {
		this.mediaSource = mediaSource;
		this.mediaTarget = mediaTarget;
	}
	
	public EnumMap<MediaStatus, Integer> uploadAll(){
		logger.info("Preparing upload using {}", mediaTarget.getClass().getName());
		
		
		mediaSource.listIDs().forEach(id -> {
			logger.info("Getting {}", id);
			try {	
				Media media = mediaSource.get(id);
				try {
					mediaTarget.upload(new ImageResizer(media, imageResizes).getResizes());
				} catch (MediaException e) {
					mediaTarget.upload(media);
				}
				showUploadStatus(media);
			} catch (MediaException e) {
				logger.error(e.getMessage());
			}
			
		});

	
		logger.info("End upload {}",mediaTarget.getUploadResult());
		return mediaTarget.getUploadResult();
		
	}

	private void showUploadStatus(Media media) {
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

	public MediaUploader setImageResizes(Set<Integer> resizes) {
		this.imageResizes = resizes;
		return this;
	}


	public MediaUploader setSource(MediaTemplate mediaSource) {
		this.mediaSource = mediaSource;
		return this;
	}
	
	public MediaUploader setTarget(MediaTemplate mediaTarget) {
		this.mediaTarget = mediaTarget;
		return this;
	}

}
