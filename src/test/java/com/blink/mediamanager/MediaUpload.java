package com.blink.mediamanager;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
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
	private final List<Integer> sizes = List.of(ImgResizer.thumbnailSize, 400, 800);
	
	public MediaUpload() {
		System.setProperty("logging.level.org.springframework",  "WARN");
		System.setProperty("logging.level.com.mkyong", "DEBUG");
		System.setProperty("logging.pattern.file", "%d %p %c{1.} [%t] %m%n");
		System.setProperty("logging.pattern.console", "%d{yyyy/MM/dd HH:mm:ss.SSS} [%t] %logger{5} %level: %msg%n");

	}
	
	@SuppressWarnings("static-method")
	@Test
	public void upload() throws IOException {
		
		Stream<Path> files = Files.list(Path.of(localPath));
		files.forEach(path -> {
			
			logger.info("Processing {}",path.getFileName());
			InputStream stream;
			try {
				stream = new FileInputStream(path.toFile());
				ImgWithResize media = new ImgWithResize(path.getFileName().toString(), stream, sizes);
				getMediaTemplate().upload(media, this::callback);
				getMediaTemplate().upload(media.getResizes(), this::callback);
				
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			
		
		});
		
	}
	
	private MediaTemplate getMediaTemplate() {
		if(mediaTemplate== null)
			mediaTemplate = new MediaS3(accessKey, secretKey, region, bucket, s3Path );
		
		return mediaTemplate;
	}
	
	private void callback(Media media, MediaStatus status) {
		switch(status) {
			case ok: logger.info("Finished upload {} URL {}", media.getId(), status.getUrl());
			case err: logger.info("Finished upload {} URL {}", media.getId(), status.getException().getMessage());
		}
	}
}
