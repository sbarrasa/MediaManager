package com.blink.mediamanager;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class MediaResizer {
	public MediaResaizable mediaSource;
	
	public MediaResizer(MediaResaizable mediaSource) {
		this.mediaSource = mediaSource; 
	}
	
	public Map<Integer, Media> build(){
		mediaSource.mediasMap = resizeAll();
		return mediaSource.mediasMap ;
	}

	private Map<Integer, Media> resizeAll() {
		mediaSource.mediasMap = new HashMap<>();
		
		mediaSource.getWidths().forEach(w -> {
			Media mediaResized = new Media();
			mediaResized.setId(mediaSource.getId()+w);
			mediaResized.setStream(resize(mediaSource.getStream(), w));

			mediaSource.mediasMap.put(w, mediaResized);
		});
		return mediaSource.mediasMap ;
	}

	private InputStream resize(InputStream sourceStream, Integer width) {
		/* TODO hacer la transformación del insputStream en imagen
		 * cambiar el tamaño proporcional esgún el width enviado
		 */
		return null;
	}
}
