package com.blink.mediamanager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import com.blink.mediamanager.local.MediaLocal;

@SpringBootTest(classes = { com.blink.mediaserver.conf.MediaTemplateConfig.class} )
public class MediaUpload {
	
	@Value("${com.blink.mediamanager.source.path}")
	private String sourcePath;

	private MediaLocal mediaSource;

	@Autowired
	private MediaTemplate mediaTemplate;

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final List<Integer> sizes = List.of(ImageResizer.sourceWidth, ImageResizer.thumbnailWidth, 400, 800);

	@Test
	public void upload() throws IOException {
		logger.info("Preparing upload using {}", mediaTemplate.getClass().getName());

		mediaSource =  new MediaLocal(sourcePath);

	
		Collection<Media> medias = new ArrayList<>();
		
		mediaSource.listIDs().forEach(id -> {
			logger.info("Getting {}", id);
			try {	
				Media media = mediaSource.get(id);
				
				try {
					medias.addAll(new ImageResizer(media, sizes).getResizes());
				} catch (MediaException e) {
					medias.add(media);
				}

			} catch (MediaException e) {
				logger.error(e.getMessage());
			}
			

		});
		logger.info("Uploading");

		CompletableFuture<Collection<Media>> future = mediaTemplate.upload(medias, this::callback);


		future.join();

		logger.info("End upload");
	}

	private void callback(Media media) {
		switch (media.getStatus()) {
		case err:
			logger.info("{} {}", media.getId(), media.getStatus().getMsg());
			break;
		case uploaded:
			logger.info("{} {} URL={}", media.getId(), media.getStatus(), media.getUrl());
			break;
		default:
			break;
		}

	}
}
