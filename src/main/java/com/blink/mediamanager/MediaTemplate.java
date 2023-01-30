package com.blink.mediamanager;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.zip.CRC32;

public interface MediaTemplate {

	default public void upload(Media media, Consumer<Media> callback) {
		CompletableFuture.runAsync(() -> {
			URL url;
			try {
				upload(media);
				callback.accept(media);
			} catch (Exception e) {
				callback.accept(media);
			}
		});
	}

	default public void upload(Collection<Media> medias, Consumer<Media> callback) {
		CompletableFuture.runAsync(() -> {
			medias.forEach(media -> {
				upload(media);
				callback.accept(media); 
			});
		});
	}
	
	default public Media upload(Media media) {
		if (!mediaInRemote(media)) {
			try {
				uploadImpl(media);
				media.setUrl(getURL(media.getId()));
				media.setStatus(MediaStatus.ok);
			}catch(Exception e) {
				media.setStatus(MediaStatus.err(e));
			}
		
		}
		return media;

	}

	default public Collection<Media> upload(Collection<Media> medias) {
		medias.forEach(media -> upload(media));
		return medias;
	}

	default public void delete(Media media) {
		delete(media.getId());
	}

	public void delete(String id) ;

	default public void delete(List<Media> medias) {
		medias.forEach( media -> delete(media));
	}

	default public void deleteIDs(List<String> ids) {
		ids.forEach( id -> delete(id));
	}

	default public List<URL> listURLs() {
		return listIDs().stream().map(this::getURL).collect(Collectors.toList());

	}

	public List<String> listIDs();

	public List<?> listAllMetadata();

	public URL getURL(String id);

	public default String getChecksum(Media media) {
		CRC32 crc32 = new CRC32();
		crc32.update(media.getStream().readAllBytes());
		media.getStream().reset();
		return String.valueOf(crc32.getValue());
	}

	public String getRemoteChecksum(String id);

	default public boolean mediaInRemote(Media media) {

		String remoteChecksum = getRemoteChecksum(media.getId());

		String fileChecksum = getChecksum(media);

		return fileChecksum.equals(remoteChecksum);
	};

	public Media uploadImpl(Media media) throws MediaException;

	public Media get(String id) throws MediaException;

}
