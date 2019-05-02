/**
 * 
 */
package com.ft.web.rest;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.ft.service.FileLocalServiceImpl;
import com.ft.config.Constants;

/**
 * @author giangdd
 *
 */
@Controller
@RequestMapping(Constants.MAPPING_REQUEST_GET)
public class FileLocalResource {
	private final Logger log = LoggerFactory.getLogger(FileLocalResource.class);
	@Autowired
	private FileLocalServiceImpl getFileService;

	@RequestMapping(value = Constants.MAPPING_REQUEST_IMAGE_JPG
			+ "/**", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
	public @ResponseBody byte[] getImageJpg(HttpServletRequest request, HttpServletResponse response) {
		return getFileService.getImage(request, response, Constants.MAPPING_REQUEST_IMAGE_JPG);
	}

	@RequestMapping(value = Constants.MAPPING_REQUEST_IMAGE_PNG
			+ "/**", method = RequestMethod.GET, produces = MediaType.IMAGE_PNG_VALUE)
	public @ResponseBody byte[] getImagePng(HttpServletRequest request, HttpServletResponse response) {
		return getFileService.getImage(request, response, Constants.MAPPING_REQUEST_IMAGE_PNG);
	}

	@RequestMapping(value = Constants.MAPPING_REQUEST_FILE + "/**", method = RequestMethod.GET)
	public void downloadFile(HttpServletResponse response, HttpServletRequest request) {
		getFileService.downloadFile(request, response);
	}

	@RequestMapping(value = Constants.MAPPING_REQUEST_UPLOAD, method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	public ResponseEntity<String> handleFileUpload(@RequestParam("name") String name,
			@RequestParam(value = "id", required = false) String id,
			@RequestParam(value = "key", required = false) String key, @RequestParam("file") MultipartFile file,
			HttpServletRequest request) throws IOException {

		String serverName = request.getServerName(); // hostname.com
		int serverPort = request.getServerPort();
		String scheme = request.getScheme();
		String urlFile = getFileService.handleFileUpload(name, file, scheme, serverName, serverPort, id, key);
		log.debug("REST request to save Image : {}", urlFile);
		return ResponseEntity.ok().body(id);
	}
}
