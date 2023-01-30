package com.blink.mediamanager;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.blink.mediamanager.s3.MediaS3;

public class MediaUpload {
	private static final String localPath = "/home/zaiper/Blink/img";
	private static final String accessKey = "AKIAXR53AKFBEO3LNYAC";
	private static final String secretKey = "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";
	private static final String region = "sa-east-1";
	private static final String bucket = "test-blink";
	private static final String s3Path = "s3.sa-east-1.amazonaws.com";
	private static MediaTemplate mediaTemplate;
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final List<Integer> sizes = List.of(ImageResizer.sourceSize, ImageResizer.thumbnailSize, 400, 800);

	public MediaUpload() throws IOException {
		((ch.qos.logback.classic.Logger) LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME))
				.setLevel(ch.qos.logback.classic.Level.INFO);

		mediaTemplate = new MediaS3(accessKey, secretKey, region, bucket, s3Path);

	}

	@Test
	public void upload() throws IOException {
		logger.info("Preparing upload");
		
		try (Stream<Path> files = Files.list(Path.of(localPath))) {
			Collection<Media> medias = new ArrayList<>();
			
			files.forEach(path -> {
				logger.info("Getting {}", path.getFileName());
				InputStream stream;
				try {
					stream = new FileInputStream(path.toFile());
					Media media = new Media(path.getFileName().toString(), stream);
					
					try {
						medias.addAll(new ImageResizer(media, sizes).getResizes());
					} catch (MediaException e) {
						medias.add(media);
					}
	
				} catch (FileNotFoundException e) {
					logger.error(e.getMessage());
				}

			});
			
			CompletableFuture<Collection<Media>> future = mediaTemplate.upload(medias, this::callback);
			logger.info("End prepare upload");
			
			future.join();
			
		}
		logger.info("End upload");
	}

	private void callback(Media media) {
		switch(media.getStatus()) {
		case err:
			logger.info("{} {}", media.getId(), media.getStatus().getMsg());
			break;
		case remoteUploaded:
			logger.info("{} {} URL={}", media.getId(), media.getStatus(), media.getUrl());
			break;
		}
	
	}
}
