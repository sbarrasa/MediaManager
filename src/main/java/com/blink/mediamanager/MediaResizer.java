package com.blink.mediamanager;

import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MediaResizer {
	public static final Integer thumbnailSize = 100;
	public static final String idResizedPattern = "%s_%d";
	public Media mediaSource;
	public Map<Integer, Media> resizedMap;
	public List<Integer> widths;
	
	public MediaResizer(Media mediaSource) {
		this(mediaSource, List.of(thumbnailSize));
	}
		
	public MediaResizer(Media mediaSource, List<Integer> widths) {
		this.mediaSource = mediaSource; 
		setWidths(widths);
	}

	public List<Integer> getWidths() {
		return widths;
	}

	public MediaResizer setWidths(List<Integer> widths) {
		this.widths = widths;
		this.resizedMap = null;
		return this;
	}

	public Media getThumbnail() {
		return getResizedMap().get(thumbnailSize);
	}
	
	public Collection<Media> getResized(){
		
		return getResizedMap().values();
	}

	public Map<Integer, Media> getResizedMap(){
		if(resizedMap == null) 
			build();
		
		return resizedMap;

	}

	public MediaResizer build(){
		resizedMap = new HashMap<>();
		
		widths.forEach(width -> {
			Media mediaResized = new Media();
			mediaResized.setId(String.format(idResizedPattern, mediaSource.getId(),width));
			mediaResized.setStream(resize(mediaSource.getStream(), width));

			resizedMap.put(width, mediaResized);
		});
		return this;
	}

	
	private InputStream resize(InputStream sourceStream, Integer width) {
		/* TODO hacer la transformación del insputStream en imagen
		 * cambiar el tamaño proporcional esgún el width enviado
		 */
		return null;
	}
}
