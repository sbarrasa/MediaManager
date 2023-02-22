package com.blink.mediaservice;

import com.blink.mediamanager.MediaException;
import com.blink.mediamanager.MediaStatus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.blink.mediamanager.MediaTemplate;
import com.blink.async.AsyncProcessor;
import com.blink.async.ProcessResult;
import com.blink.mediamanager.rest.MediaEndpoints;
import com.blink.mediamanager.Media;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Controller
public class MediaController implements MediaTemplate {
	private AsyncProcessor<Media, MediaStatus> asyncProcessor = new AsyncProcessor<>();

	@Autowired
	private MediaConfig mediaConfig;

	@Autowired
	private MediaTemplate mediaTemplate;

	@ResponseBody
	@RequestMapping(path = MediaEndpoints.UPLOAD, method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public URL upload(@RequestPart() MultipartFile multipartFile) throws IOException {
		
		Media media = new Media()
								.setId(multipartFile.getOriginalFilename())
								.setStream(multipartFile.getInputStream());

		MediaUpdater mediaUpdater = new MediaUpdater().setTarget(mediaTemplate);
		
		asyncProcessor.executeAsync("upload", mediaUpdater::upload, media); 
				
		return media.getUrl();
	}

	@DeleteMapping(MediaEndpoints.DELETE + "/{id}")
	@ResponseBody
	@Override
	public Media delete(@PathVariable String id) {
		Media media = new Media(id);
		deleteImpl(media);
		return media;
	}

	public void deleteImpl(Media media) {
		mediaTemplate.delete(media);
	}

	@GetMapping(MediaEndpoints.LISTALL_METADATA)
	@ResponseBody
	@Override
	public Collection<?> listAllMetadata() {
		return mediaTemplate.listAllMetadata();
	}

	@GetMapping(MediaEndpoints.LIST_URLs)
	@ResponseBody
	public Collection<URL> listURLs() {
		return mediaTemplate.listURLs();
	}

	@GetMapping(MediaEndpoints.LIST_IDS)
	@ResponseBody
	@Override
	public Collection<String> listIDs() {
		return mediaTemplate.listIDs();
	}

	@GetMapping(MediaEndpoints.URL + "/{id}")
	@ResponseBody
	@Override
	public URL getURL(@PathVariable String id) {
		return mediaTemplate.getURL(id);
	}

	@Override
	public Media uploadImpl(Media media) throws MediaException {
		return mediaTemplate.uploadImpl(media);
	}

	@GetMapping("/get/{id}")
	@ResponseBody
	@Override
	public Media get(@PathVariable String id) throws MediaException {
		return mediaTemplate.get(id);
	}

	@GetMapping(value = (MediaEndpoints.GET + "{id}"), produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	@ResponseBody
	public ResponseEntity<?> getEntity(@PathVariable String id) throws MediaException {
		UrlResource resource;
		resource = new UrlResource(mediaTemplate.getURL(id));
		if (!resource.exists())
			return ResponseEntity.notFound().build();

		return ResponseEntity.ok(resource);
	}

	@GetMapping(MediaEndpoints.CHECKSUM + "{id}")
	@ResponseBody
	@Override
	public String getServerChecksum(String id) {
		return mediaTemplate.getServerChecksum(id);
	}

	@DeleteMapping("delete_all")
	@ResponseBody
	public CompletableFuture<ProcessResult<MediaStatus>> deleteAll() {
		MediaUpdater mediaUpdater = new MediaUpdater().setTarget(mediaTemplate);
		
		return asyncProcessor.executeAsync("delete", mediaUpdater::deleteAll);
	}

	@ResponseBody
	@PostMapping("/upload_all/")
	public CompletableFuture<ProcessResult<MediaStatus>> uploadAll(
			@Value("${com.blink.mediamanager.source.class}") String sourceClass,
			@Value("${com.blink.mediamanager.source.path}") String sourcePath,
			@Value("${com.blink.mediamanager.imageresizer.widths}") Set<Integer> widths) {

		MediaUpdater mediaUpdater = new MediaUpdater().setSource(mediaConfig.newMediaTemplate(sourceClass, sourcePath))
				.setTarget(mediaTemplate).setImageResizes(widths);

		return asyncProcessor.executeAsync("upload", mediaUpdater::uploadAll);

	}

	@Override
	public MediaTemplate setPath(String pathStr) {
		return null;
	}

	@Override
	public String getPath() {
		return null;
	}

	@ResponseBody
	@GetMapping("/process/result/")
	public Map<String, ProcessResult<MediaStatus>> showProcessResult() {
		return asyncProcessor.getAllProcessResult();
	}


	@ResponseBody
	@PostMapping("/process/cleanCompleted")
	public Map<String, ProcessResult<MediaStatus>> cleanComplete() {
		return asyncProcessor.cleanComleted();
	}

	@Override
	public boolean validateURL(URL url) throws MediaException {
		return mediaTemplate.validateURL(url);
	}

}
