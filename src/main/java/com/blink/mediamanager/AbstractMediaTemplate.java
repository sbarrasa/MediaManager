package com.blink.mediamanager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public abstract class AbstractMediaTemplate implements MediaTemplate{
	private String pathStr;
	private EnumMap<MediaStatus, Integer> uploadResult = new EnumMap<>(MediaStatus.class);
	private List<CompletableFuture<?>> futures = new ArrayList<>();
	
	@Override
	public MediaTemplate setPath(String pathStr) {
		this.pathStr = pathStr;
		return this;
	}

	@Override
	public String getPath() {
		return pathStr;
	}

	@Override
	public EnumMap<MediaStatus, Integer> getUploadResult(){
		return uploadResult;
	}
	
	@Override
	public CompletableFuture<?> upload(Media media, Consumer<Media> callback) {
		CompletableFuture<?> future = MediaTemplate.super.upload(media, callback);
		futures.add(future);
		return future;
	}

	public CompletableFuture<?> upload(Collection<Media> medias, Consumer<Media> callback) {
		CompletableFuture<?> future = MediaTemplate.super.upload(medias, callback);
		futures.add(future);
		return future;
	}
	
	public void syncUpdates() {
		futures.forEach(future -> future.join());
		futures.clear();
	};
	
}
