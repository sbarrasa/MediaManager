package com.blink.mediamanager;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.zip.CRC32;

public interface MediaTemplate {
	
	@SuppressWarnings("unused")
	default public MediaTemplate setPath(String pathStr) {
		return this;
	}
	
	default public CompletableFuture<Media> upload(Media media, Consumer<Media> callback) {
		return CompletableFuture.supplyAsync(() -> {
			upload(media);
			callback.accept(media);
			return media;
		});
	}

	
	default public Media upload(Media media) {
		try {
			media.setUrl(getURL(media.getId()));
			if (inServer(media)) {
				media.setStatus(MediaStatus.unchanged);
			}else {	
				uploadImpl(media);
				media.setStatus(MediaStatus.uploaded);
			}
		}catch(Exception e) {
			media.setStatus(MediaStatus.err(e));
		}
		return media;

	}

	default public Collection<Media> upload(Collection<Media> medias) {
		medias.forEach(media -> upload(media));
		return medias;
	}

	default public CompletableFuture<Collection<Media>> upload(Collection<Media> medias, Consumer<Media> callback) {
		return CompletableFuture.supplyAsync(() ->  {
			
			medias.forEach(media -> {
				upload(media);
				
				callback.accept(media);
			});
			return medias;
		});
	}

	default public void delete(Media media) throws MediaException {
		delete(media.getId());
	}

	public void delete(String id) throws MediaException ;


	default public void delete(Collection<String> ids) {
		ids.forEach( id -> {
			try {
				delete(id);
			} catch (MediaException e) {
			}
		});
	}

	default public Collection<URL> listURLs() {
		return listIDs().stream().map(id -> {
			try {
				return getURL(id);
			} catch (MediaException e) {
				return null;
			}
		}).collect(Collectors.toList());

	}

	public Collection<String> listIDs();

	public Collection<?> listAllMetadata();

	public URL getURL(String id) throws MediaException;

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

	public String getServerChecksum(String id);

	default public boolean inServer(Media media) {

		String remoteChecksum = getServerChecksum(media.getId());

		String fileChecksum = getChecksum(media);
	
		return fileChecksum.equals(remoteChecksum);
	
	};

	public Media uploadImpl(Media media) throws MediaException;

	public Media get(String id) throws MediaException;

}
