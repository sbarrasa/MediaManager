package com.blink.mediamanager;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.blink.mediamanager.s3.MediaS3;


public class MediaUpload {
	private static final String localPath = "/home/zaiper/Blink/img";
	private static final String accessKey="AKIAXR53AKFBEO3LNYAC";
	private static final String secretKey = "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";
	private static final String region = "sa-east-1";
	private static final String bucket = "test-blink";
	private static final String s3Path = "s3.sa-east-1.amazonaws.com";
	private static MediaTemplate mediaTemplate;
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final List<Integer> sizes =  List.of(ImageResizer.sourceSize, ImageResizer.thumbnailSize, 400, 800);
	
	
	public MediaUpload() throws IOException {
		((ch.qos.logback.classic.Logger)LoggerFactory
			.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME))
			.setLevel(ch.qos.logback.classic.Level.INFO);

		mediaTemplate = new MediaS3(accessKey, secretKey, region, bucket, s3Path );
		
	}
	
	@SuppressWarnings("static-method")
	@Test
	public void upload() throws IOException {
		
		try (Stream<Path> files = Files.list(Path.of(localPath))) {
			files.forEach(path -> {
				
				logger.info("Processing {}",path.getFileName());
				InputStream stream;
				try {
					stream = new FileInputStream(path.toFile());
					Media media = new Media(path.getFileName().toString(), stream);
					Collection<Media> medias = new ImageResizer(media, sizes).getAll();
					medias.forEach(mediaL -> {
						try {

						mediaTemplate.upload(mediaL);
							
					} catch (MediaException e) {
							logger.error("Error {}",mediaL.getId());
										}
					});
//					mediaTemplate.upload(medias, this::callback);
					
				} catch (Exception e) {
					logger.error("Error {}",path);
				}
				
			
			});
		}
		logger.info("End upload");
	}
	
	
	private void callback(Media media, MediaStatus status) {
		switch(status) {
			case ok: 
				logger.info("Finished upload {} Ok, URL={}", media.getId(), status.getUrl());
				break;
			case err: 
				logger.info("Finished upload {} with error: {}", media.getId(), status.getException().getMessage());
				break;

		}
	}
}
