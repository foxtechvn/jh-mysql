/**
 * 
 */
package com.ft.service.util;

import java.io.File;

import org.apache.commons.lang3.StringUtils;

/**
 * @author giangdd
 *
 */
public class FileUtil {

	private static String validatePatch(String patch) {
		if (StringUtils.isBlank(patch)) {
			return "";
		}
		if ("/".equals(File.separator)) {
			patch = patch.replace("\\", File.separator);
		}
		if ("\\".equals(File.separator)) {
			patch = patch.replace("/", File.separator);
		}
		return patch;
	}

	public static String createFolder(String patch, String patchFolder) {
		String url = getPatchFile(patch, patchFolder);
		File file = new File(url);
		if (!file.exists()) {
			file.mkdir();
		}
		return file.getPath();
	}

	private static String getPatchFile(String patch, String patchFolder) {
		String spatch = validatePatch(patch);

		if (spatch.startsWith(File.separator)) {
			spatch = spatch.substring(1, spatch.length());
		}

		patchFolder = validatePatch(patchFolder);
		if (patchFolder.endsWith(File.separator)) {
			patchFolder = patchFolder.substring(0, patchFolder.length() - 1);
		}

		String url = patchFolder + File.separator + spatch;
		return url;
	}

	public static String getFileName(String patchFile) {
		patchFile = validatePatch(patchFile);
		if (StringUtils.isBlank(patchFile)) {
			return "";
		}
		int index = patchFile.lastIndexOf(File.separator);
		if (index >= 0) {
			return patchFile.substring(index + 1, patchFile.length());
		}
		return patchFile;
	}

	public static String getPatchFolder(String patchFile) {
		patchFile = validatePatch(patchFile);
		if (StringUtils.isBlank(patchFile)) {
			return "";
		}
		int index = patchFile.lastIndexOf(File.separator);
		if (index >= 0) {
			return patchFile.substring(0, index);
		}
		return "";
	}

	public static File createFile(String patch, String patchFolder) {
		String fileName = getFileName(patch);

		patchFolder = createFolder(patch, patchFolder);
		patch = patchFolder + File.separator + fileName;
		return new File(patch);
	}

	public static File getFile(String patch, String patchFolder) {
		patch = getPatchFile(patch, patchFolder);
		return new File(patch);
	}

	public static String getRelativePath(String patch) {
		patch = validatePatch(patch);
		String relativePatch = "";
		if (StringUtils.isBlank(patch)) {
			return relativePatch;
		}
		// TODO
		String url = validatePatch(patch);
		String absoluteUrl = url.endsWith(File.separator) ? url : url + File.separator;
		if (patch.startsWith(absoluteUrl)) {
			relativePatch = patch.substring(absoluteUrl.length(), patch.length());
		} else {
			relativePatch = "";
		}

		relativePatch = relativePatch.replace(File.separator, "/");
		return relativePatch;
	}

	public static boolean renameOrRemoverFile(File oldfile, File newfile) {
		if (oldfile == null || newfile == null || !oldfile.exists()) {
			return false;
		}
		String patchFolder = getPatchFolder(newfile.getPath());
		File folder = new File(patchFolder);
		if (!folder.exists()) {
			if (!folder.mkdir()) {
				return false;
			}
		}

		return oldfile.renameTo(newfile);
	}

	public static boolean delete(File file) {

		if (file.isDirectory()) {

			// directory is empty, then delete it
			if (file.list().length == 0) {
				return file.delete();
			} else {

				// list all the directory contents
				String files[] = file.list();

				for (String temp : files) {
					// construct the file structure
					File fileDelete = new File(file, temp);

					// recursive delete
					if (!delete(fileDelete)) {
						return false;
					}
				}

				// check the directory again, if empty then delete it
				if (file.list().length == 0) {
					return file.delete();
				} else {
					return false;
				}
			}

		} else {
			// if file, then delete it
			return file.delete();

		}
	}
}
