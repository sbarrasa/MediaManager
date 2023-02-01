package com.blink.mediamanager.local;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.blink.mediamanager.Media;
import com.blink.mediamanager.MediaException;
import com.blink.mediamanager.MediaTemplate;

public class MediaLocal implements MediaTemplate {
	private Path path;
	private String localProtocol = "file";
	private String localHost = "localhost";

	private Path getPath(String id) {
		return Path.of(path.toString(), id);
	}

	private Path getPath() {
		return path;
	}
	
	public MediaLocal() {
		
	}
	
	public MediaLocal(String pathStr) { 
		setPath(pathStr);
	}

	public MediaLocal setPath(Path path) {
		this.path = path;
		return this;
	}

	public MediaLocal setPath(String pathStr) {
		this.path = Path.of(pathStr);
		return this;
	}

	@Override
	public void delete(String id) throws MediaException {
		try {
			Files.delete(getPath(id) );
		} catch (IOException e) {
			throw new MediaException(e);
		}
	}

	@Override
	public Collection<String> listIDs() {
		try {
			Stream<Path> files = Files.list(getPath());
			return files.map(file -> file.getFileName().toString()).collect(Collectors.toList());
		} catch (IOException e) {
			return List.of();
		}
	}

	@Override
	public Collection<?> listAllMetadata() {
		try {
			return 	Files.list(getPath()).collect(Collectors.toList());
		} catch (IOException e) {
			return List.of();
		}
	}

	@Override
	public URL getURL(String id) throws MediaException {
		try {
			return new URL(localProtocol, localHost, getPath(id).toString());
		} catch (MalformedURLException e) {
			throw new MediaException(e);
		}
	}

	@Override
	public String getServerChecksum(String id) {
		try {
			return getChecksum(get(id));
		} catch (MediaException e) {
			return null;
		}
	}

	@Override
	public Media uploadImpl(Media media) throws MediaException {
		try {
			Files.copy(media.getStream(), getPath(media.getId()));
			return media;
		} catch (IOException e) {
			throw new MediaException(e);
		} 
	}

	@Override
	public Media get(String id) throws MediaException {
		return new Media(getPath(id));
	}

}
