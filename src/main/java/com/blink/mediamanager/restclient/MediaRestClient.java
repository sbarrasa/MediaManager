package com.blink.mediamanager.restclient;

import java.io.File;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

import com.blink.mediamanager.MediaEndpoints;
import com.blink.mediamanager.MediaException;
import com.blink.mediamanager.MediaTemplate;

public class MediaRestClient implements MediaTemplate{
	private RestTemplate rest;
	
	@Value("${com.blink.mediamanager.rest.url}")
	private String URL;

	public MediaRestClient(){
		rest = new RestTemplate();
		//TODO: configurar el rest template 
	}

	public MediaRestClient(RestTemplate rest){
		this.rest = rest;
	}

	@Override
	public void delete(String id) throws MediaException {
		// TODO Auto-generated method stub

	}

	@Override
	public List<String> listAllIDs() {
		return List.of(rest.getForObject( MediaEndpoints.LISTALL_IDS,  String[].class));
	}

	@Override
	public List<?> listAllMetadata() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getURL(String id) {
		// TODO Auto-generated method stub
		return null;
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
		// TODO Auto-generated method stub
		return null;
	}
	
}
