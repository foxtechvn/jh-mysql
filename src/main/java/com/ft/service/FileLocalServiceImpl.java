package com.ft.service;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import com.ft.config.Constants;
import com.ft.service.util.FileUtil;

/**
 * @author giangdd
 *
 */
@Service
public class FileLocalServiceImpl {
	private final Logger logger = LoggerFactory.getLogger(FileLocalServiceImpl.class);

	// TODO add to application.properties
	// @Value("${patchFolder:}")
	private String patchFolder = Constants.UPLOAD_PATH;

	public String handleFileUpload(String name, MultipartFile file, String scheme, String serverName, int serverPort,
			String id, String key) throws IOException {

		if (file.isEmpty()) {
			return null;
		}

		byte[] bytes = file.getBytes();
		if (StringUtils.isBlank(name)) {
			name = file.getOriginalFilename();
		}
		File fileS = FileUtil.createFile(name, patchFolder);
		BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(fileS));
		stream.write(bytes);
		stream.close();

		String urlFile = generateFileUrl(fileS, scheme, serverName, serverPort);
		return urlFile;

	}

	private String getFilePathFromRequest(HttpServletRequest request, String mapingRequest) {
		String patch = request.getRequestURI().substring(request.getContextPath().length());
		String patchMapingRequest = Constants.MAPPING_REQUEST_GET + mapingRequest;
		if (patch.length() < patchMapingRequest.length()) {
			return "";
		}
		patch = patch.substring(patchMapingRequest.length());
		try {
			patch = URLDecoder.decode(patch, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			logger.error("Download exception", e);
		}
		return patch;
	}

	public void downloadFile(HttpServletRequest request, HttpServletResponse response) {
		try {
			String patch = getFilePathFromRequest(request, Constants.MAPPING_REQUEST_FILE);

			File file = FileUtil.getFile(patch, patchFolder);

			if (!file.exists()) {
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
				return;
			}

			String mimeType = URLConnection.guessContentTypeFromName(file.getName());
			if (mimeType == null) {
//        System.out.println("mimetype is not detectable, will take default");
				mimeType = "application/octet-stream";
			}

			response.setContentType(mimeType);

			/*
			 * "Content-Disposition : inline" will show viewable types [like
			 * images/text/pdf/anything viewable by browser] right on browser while
			 * others(zip e.g) will be directly downloaded [may provide save as popup, based
			 * on your browser setting.]
			 */
			response.setHeader("Content-Disposition", String.format("inline; filename=\"" + file.getName() + "\""));

			/*
			 * "Content-Disposition : attachment" will be directly download, may provide
			 * save as popup, based on your browser setting
			 */
			// response.setHeader("Content-Disposition", String.format("attachment;
			// filename=\"%s\"",
			// file.getName()));

			response.setContentLength((int) file.length());

			InputStream inputStream = new BufferedInputStream(new FileInputStream(file));

			// Copy bytes from source to destination(outputstream in this example), closes
			// both streams.
			FileCopyUtils.copy(inputStream, response.getOutputStream());
		} catch (Exception e) {
			logger.error("Download exception", e);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	public byte[] getImage(HttpServletRequest request, HttpServletResponse response, String mappingRequestFuntion) {
		try {
			String patch = getFilePathFromRequest(request, mappingRequestFuntion);
			// Retrieve image from the classpath.
			File file = FileUtil.getFile(patch, patchFolder);
			if (!file.exists()) {
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
				return null;
			}
			InputStream is = new FileInputStream(file);

			// Prepare buffered image.
			BufferedImage img = ImageIO.read(is);

			// Create a byte array output stream.
			ByteArrayOutputStream bao = new ByteArrayOutputStream();

			// Write to output stream
			switch (mappingRequestFuntion) {
			case Constants.MAPPING_REQUEST_IMAGE_JPG:
				ImageIO.write(img, "jpg", bao);
				break;
			case Constants.MAPPING_REQUEST_IMAGE_PNG:
				ImageIO.write(img, "png", bao);
				break;
			default:
				ImageIO.write(img, "jpg", bao);
				break;
			}
			return bao.toByteArray();
		} catch (Exception e) {
			logger.error("Get image exception", e);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
		return null;
	}

	private String generateFileUrl(File file, String scheme, String serverName, int serverPort) {
		StringBuilder url = new StringBuilder();
		url.append(scheme).append("://").append(serverName);
		if (serverPort != 80) {
			url.append(":").append(serverPort);
		}
		url.append(Constants.MAPPING_REQUEST_GET);
		String ext = FilenameUtils.getExtension(file.getPath()).toLowerCase();
		switch (ext) {
		case "jpg":
			url.append(Constants.MAPPING_REQUEST_IMAGE_JPG);
			break;
		case "png":
			url.append(Constants.MAPPING_REQUEST_IMAGE_PNG);
			break;
		default:
			url.append(Constants.MAPPING_REQUEST_FILE);
			break;
		}
		url.append("/");
		String patchFile = FileUtil.getRelativePath(file.getPath());
		String split[] = patchFile.split("/");
		try {
			StringBuilder sTeam = new StringBuilder();
			for (int i = 0; i < split.length; i++) {
				String item = URLEncoder.encode(split[i], "UTF-8");
				if (i != 0) {
					sTeam.append("/");
				}
				sTeam.append(item);
			}

			patchFile = sTeam.toString();
		} catch (UnsupportedEncodingException e) {
			logger.error("Download exception", e);
		}
		url.append(patchFile);
		return url.toString();
	}
}
