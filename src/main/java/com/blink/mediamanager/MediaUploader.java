package com.blink.mediamanager;

import java.net.URL;
import java.util.Collection;
import java.util.EnumMap;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blink.async.AsyncProcessor;

public class MediaUploader {
	private MediaTemplate mediaSource;
	private MediaTemplate mediaTarget;
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private Set<Integer> imageResizes =  ImageResizer.defaultWidths;
	
	public MediaUploader() {
		
	}
		
	public MediaUploader(MediaTemplate mediaSource, MediaTemplate mediaTarget) {
		this.mediaSource = mediaSource;
		this.mediaTarget = mediaTarget;
	}
	
	public EnumMap<MediaStatus, Integer> uploadAll(){
		logger.info("Preparing upload using {}", mediaTarget.getClass().getName());
		
		AsyncProcessor<Media> processor = new AsyncProcessor<>();
		processor.setCallback(this::callback);
		
		mediaSource.listIDs().forEach(id -> {
			logger.info("Getting {}", id);
			try {	
				Media media = mediaSource.get(id);
				try {
					processor.executeAsync(mediaTarget::upload, new ImageResizer(media, imageResizes).getResizes());
				} catch (MediaException e) {
					processor.executeAsync(mediaTarget::upload, media);
				}

			} catch (MediaException e) {
				logger.error(e.getMessage());
			}
			
		});

	
		processor.syncAll();
		logger.info("End upload {}",mediaTarget.getUploadResult());
		return mediaTarget.getUploadResult();
		
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
