package com.blink.mediamanager;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.zip.CRC32;

public interface MediaTemplate {

	default public CompletableFuture<Media> upload(Media media, Consumer<Media> callback) {
		return CompletableFuture.supplyAsync(() -> {
			upload(media);
			callback.accept(media);
			return media;
		});
	}

	default public CompletableFuture<Collection<Media>> upload(Collection<Media> medias, Consumer<Media> callback) {
		return CompletableFuture.supplyAsync(() ->  {
			
			medias.forEach(media -> {
				if(media.getStatus() == MediaStatus.ok)
					upload(media);
				
				callback.accept(media);
			});
			return medias;
		});
	}
	
	default public Media upload(Media media) {
		media.setUrl(getURL(media.getId()));

		if (!mediaInRemote(media)) {
		
			try {
				if(media.getStatus() == MediaStatus.ok) 
					uploadImpl(media);
				
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


	default public void delete(Collection<String> ids) {
		ids.forEach( id -> delete(id));
	}

	default public Collection<URL> listURLs() {
		return listIDs().stream().map(this::getURL).collect(Collectors.toList());

	}

	public Collection<String> listIDs();

	public Collection<?> listAllMetadata();

	public URL getURL(String id);

	public default String getChecksum(Media media) {
		CRC32 crc32 = new CRC32();
		try {
			crc32.update(media.getStream().readAllBytes());
			media.getStream().reset();
			
		} catch (IOException e) {
			media.setStatus(MediaStatus.err(e));
		}
		return String.valueOf(crc32.getValue());
	}

	public String getRemoteChecksum(String id);

	default public boolean mediaInRemote(Media media) {

		String remoteChecksum = getRemoteChecksum(media.getId());

		String fileChecksum = getChecksum(media);

		return fileChecksum.equals(remoteChecksum);
	};

	public Media uploadImpl(Media media) ;

	public Media get(String id) throws MediaException;

}
