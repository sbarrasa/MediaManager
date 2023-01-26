package com.blink.mediamanager;

import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImgResizer {
	public static final Integer thumbnailSize = 100;
	public static final String idResizedPattern = "%s_%d";
	public Media mediaSource;
	public Map<Integer, Media> resizedMap;
	public List<Integer> widths;
	private final Integer sourceSize = -1;
	private boolean includeSource = true;
	
	public ImgResizer(Media mediaSource) {
		this(mediaSource, List.of(thumbnailSize));
	}
		
	public ImgResizer(Media mediaSource, List<Integer> widths) {
		this.mediaSource = mediaSource; 
		setWidths(widths);
	}

	public List<Integer> getWidths() {
		return widths;
	}

	public ImgResizer setWidths(List<Integer> widths) {
		this.widths = widths;
		this.resizedMap = null;
		return this;
	}

	public Media getThumbnail() {
		return getMap().get(thumbnailSize);
	}

	
	public Collection<Media> getAll(){
		return getMap().values();
	}

	public Map<Integer, Media> getMap(){
		if(resizedMap == null) 
			build();
		
		return resizedMap;

	}

	public ImgResizer build(){
		resizedMap = new HashMap<>();
		
		if(includeSource )
			resizedMap.put(sourceSize , mediaSource);
		
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

	public boolean isIncludeSource() {
		return includeSource;
	}

	public ImgResizer setIncludeSource(boolean includeSource) {
		this.includeSource = includeSource;
		return this;
	}
}
