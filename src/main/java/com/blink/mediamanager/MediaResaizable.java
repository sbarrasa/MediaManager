package com.blink.mediamanager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class MediaResaizable extends Media {
	private List<Integer> widths;
	Map<Integer, Media> mediasMap ;
	
	public static final Integer thumbnail = 100;
	
	public MediaResaizable() {
		this.widths = new ArrayList<>();
		this.widths.add(thumbnail);
	}

	public MediaResaizable(List<Integer> widths) {
		this.widths = widths;
	}

	public List<Integer> getWidths() {
		return widths;
	}

	public Media setWidths(List<Integer> sizes) {
		this.widths = sizes;
		this.mediasMap = null;
		return this;
	}

	public Map<Integer, Media> getMediasMap() {
		if(mediasMap == null)
			mediasMap = new MediaResizer(this).build();
		
		return mediasMap;
	}
	
	public Collection<Media> getMediasResized(){
		return getMediasMap().values();
		
	}

}
