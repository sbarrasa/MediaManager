package com.blink.mediamanager.restclient;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import com.blink.mediamanager.MediaEndpoints;
import com.blink.mediamanager.MediaException;
import com.blink.mediamanager.MediaTemplate;

public class MediaRestClient implements MediaTemplate {
	private RestTemplate rest;

	@Value("${com.blink.mediamanager.rest.url}")
	private String URI;

	public MediaRestClient() {
		rest = new RestTemplate();
		rest.setUriTemplateHandler(new DefaultUriBuilderFactory(URI));
	}

	public MediaRestClient(RestTemplate rest) {
		this.rest = rest;
	}

	@Override
	public void delete(String id) throws MediaException {
		rest.delete(MediaEndpoints.DELETE, id);
	}

	@Override
	public List<String> listAllIDs() {
		return List.of(rest.getForObject(MediaEndpoints.LISTALL_IDS, String[].class));
	}

	@Override
	public List<?> listAllMetadata() {

		return List.of(rest.getForObject(MediaEndpoints.LISTALL_METADATA, String[].class));
	}

	@Override
	public String getURL(String id) {
		return String.format(MediaEndpoints.GET, id);
	}

	@Override
	public String getRemoteChecksum(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean uploadImpl(File file) {
		return rest.postForObject(MediaEndpoints.UPLOAD, file, Boolean.class);
	}

	@Override
	public File getFile(String id) throws MediaException {
		InputStream inputStream = rest.getForObject(MediaEndpoints.GET,InputStream.class, id);
		File file = new File(id);
		try {
			if (inputStream != null) {
				FileUtils.copyInputStreamToFile(inputStream, file);
				return file;
			}
		} catch (IOException e) {
			throw new MediaException(e.getMessage());
		}

		return null;
	}

}
