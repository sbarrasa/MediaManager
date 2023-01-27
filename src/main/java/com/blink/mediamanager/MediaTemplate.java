package com.blink.mediamanager;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.zip.CRC32;

public interface MediaTemplate {

	default public void upload(Media media, BiConsumer<Media, MediaStatus> callback) {
		CompletableFuture.runAsync(() -> {
			URL url;
			try {
				url = upload(media);
				callback.accept(media, MediaStatus.ok(url));
			} catch (Exception e) {
				callback.accept(media, MediaStatus.err(e));
			}
		});
	}

	default public void upload(Collection<Media> medias, BiConsumer<Media, MediaStatus> callback) {
		CompletableFuture.runAsync(() -> {
			medias.forEach(media -> {
				URL url;
				
				try {
					url = upload(media);
					callback.accept(media, MediaStatus.ok(url));
				} catch (Exception e) {
					callback.accept(media, MediaStatus.err(e));
				}
			});
		});
	}
	
	default public URL upload(Media media) {
		if (!fileExistsInRemote(media)) {

			Boolean uploaded = uploadImpl(media);
			
			if (!uploaded)
				throw new MediaError(String.format("Can't upload %s", media.getId()));
		
		}
		return getURL(media.getId());

	}

	default public List<URL> upload(Collection<Media> medias) {
		List<URL> res = new ArrayList<>();
		medias.forEach(media -> {
			res.add(upload(media));
		});
		return res;
	}

	public void delete(String id) ;

	default public void delete(List<String> ids) {
		ids.forEach(id -> {
			delete(id);
		});
	}

	default public List<URL> listURLs() {
		return listIDs().stream().map(this::getURL).collect(Collectors.toList());

	}

	public List<String> listIDs();

	public List<?> listAllMetadata();

	public URL getURL(String id);

	public default String getChecksum(Media media) {
		CRC32 crc32 = new CRC32();
		try {
			crc32.update(media.getStream().readAllBytes());
		} catch (IOException e) {
			throw new MediaError(e);
		}
		return String.valueOf(crc32.getValue());
	}

	public String getRemoteChecksum(String id);

	default public boolean fileExistsInRemote(Media media) {

		String remoteChecksum = getRemoteChecksum(media.getId());

		String fileChecksum = getChecksum(media);

		return fileChecksum.equals(remoteChecksum);
	};

	public Boolean uploadImpl(Media media);

	public Media get(String id) throws MediaException;

}
