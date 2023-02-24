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
import com.blink.async.ProcessResult;

public class MediaUpdater {
	private MediaTemplate mediaTarget;
	private MediaTemplate mediaSource;
	
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Value("${com.blink.mediamanager.imageresizer.widths}")
	private Set<Integer> imageResizes;
	
	public MediaUpdater() {
	}
		
	
		
	public ProcessResult<MediaStatus> uploadAll(ProcessResult<MediaStatus> processResult){
		logger.info("Preparing upload using {}", mediaTarget.getClass().getName());
		
		
		Collection<String> mediaIds = mediaSource.listIDs();
		
		processResult.setTotal(mediaIds.size());
		
		mediaIds.forEach(id -> {
			logger.info("Getting {}", id);
			try {	
				Media media = mediaSource.get(id);
				try {
					Collection<Media> medias = new ImageResizer(media, imageResizes).getResizes();
					processResult.setTotal(processResult.getTotal()+medias.size()-1);
					medias.forEach(mediaR -> upload(mediaR, processResult));
				} catch (MediaException e) {
					upload(media, processResult);
				}
				
			} catch (MediaException e) {
				logger.error(e.getMessage());
			}
			
		});

		processResult.setCompleted(true);
	
		logger.info("End upload {}",processResult);
		return processResult;
		
	}

	public ProcessResult<MediaStatus> upload(Media media, ProcessResult<MediaStatus> processResult) {
		if(processResult.getTotal() == null)
			processResult.setTotal(1);
		
		mediaTarget.upload(media);
		showStatus(media);
		processResult.incProcessed(media.getStatus());
	
		return processResult;
	}
	
	
	private void showStatus(Media media) {
		switch (media.getStatus()) {
		case err:
			logger.info("{} {}", media.getId(), media.getStatus());
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
	
	public ProcessResult<MediaStatus> deleteAll(ProcessResult<MediaStatus> processResult){
		Collection<String> mediaIds = mediaTarget.listIDs();
		processResult.setTotal(mediaIds.size());
		mediaIds.forEach(id -> {
			Media media = mediaTarget.delete(id);
			processResult.incProcessed(media.getStatus());
		});
		processResult.setCompleted(true);
		return processResult;
	}
	
	public MediaUpdater setSource(MediaTemplate mediaSource) {
		this.mediaSource = mediaSource;
		return this;
	}

}
