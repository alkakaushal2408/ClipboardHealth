package test.java.utils;

import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import test.java.Config;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;

/**
 * This class is for downloading files from the app under test, and interacting
 * with the downloaded files.
 */
public class FileUtil {

	private FileUtil() {
		throw new IllegalStateException("Utility class");
	}

	private static Logger logger = LogManager.getLogger();

	/** Returns the newest file for a specific extension */
	public static String getTheNewestDownloadedFileName(String file) {
		var theNewestFile = new File("");
		var dir = new File(Config.DOWNLOAD_DIR);
		FileFilter fileFilter = new WildcardFileFilter("*" + file + "*");
		File[] files = dir.listFiles(fileFilter);

		if (files.length > 0) {
			Arrays.sort(files, LastModifiedFileComparator.LASTMODIFIED_REVERSE);
			theNewestFile = files[0];
		}

		return theNewestFile.getName();
	}

	/**
	 * Given a partial filename and extension, returns the newest matching file name
	 * as a String
	 */
	public static String getTheNewestDownloadedFileName(String fileName, String fileExtension) {
		var theNewestFile = new File("");
		var dir = new File(Config.DOWNLOAD_DIR);
		FileFilter fileFilter = new WildcardFileFilter(fileName + "*" + fileExtension);
		File[] files = dir.listFiles(fileFilter);

		if (files.length > 0) {
			Arrays.sort(files, LastModifiedFileComparator.LASTMODIFIED_REVERSE);
			theNewestFile = files[0];
		}
		return theNewestFile.getName();
	}

	/**
	 * Creates all of the directories on the given path if they don't already exist
	 */
	public static void createDirectories(String filePath) {
		var dir = new File(filePath);
		if (dir.mkdirs()) {
			logger.log(Level.INFO, "Created directories that didnt already exist on path: {}", filePath);
		}
	}

	/**
	 * Deletes all files at the given file path
	 */
	public static void cleanDirectory(String filePath) {
		var dir = new File(filePath);
		for (File file : dir.listFiles()) {
			if (!file.isDirectory() && !file.getName().contains("PreserveFolder")
					&& !file.getName().contains(".gitignore")) {
				try {
					Files.delete(file.toPath());
				} catch (IOException e) {
					logger.warn(e);
				}
			}
		}
	}

	/**
	 * Deletes all files and sub-directories at the given file path, except for
	 * .gitignore files and folders named "PreserveFolder"
	 */
	public static void cleanDirectoryIncludingFolders(String filePath) {
		var dir = new File(filePath);
		for (File file : dir.listFiles()) {
			if (file.isDirectory()) {
				cleanDirectoryIncludingFolders(file.getAbsolutePath());
			}
			if (!file.getName().contains("PreserveFolder") && !file.getName().contains(".gitignore")) {
				try {
					Files.delete(file.toPath());
				} catch (IOException e) {
					logger.warn(e);
				}
			}
		}
	}

	/** Deletes all files at the given file path with the given file extension */
	public static void cleanDirectoryOfFilesWithExtension(String filePath, String extension) {
		var dir = new File(filePath);
		for (File file : dir.listFiles()) {
			if (file.getName().contains("." + extension)) {
				try {
					Files.delete(file.toPath());
				} catch (IOException e) {
					logger.warn(e);
				}
			}
		}
	}

	/**
	 * Waits until a file that contains the given name is completely downloaded.
	 */
	public static void waitForFileToDownload(String file) {
		var dir = new File(Config.DOWNLOAD_DIR);

		// wait for chrome download to finish
		FileFilter download = new WildcardFileFilter("*" + file + "*crdownload*");

		waitForFileToBePresent(5, dir, download);

		File[] downloadFiles = dir.listFiles(download);

		long startTime = System.currentTimeMillis();
		while (downloadFiles.length > 0 && (System.currentTimeMillis() - startTime) < 60000) {
			downloadFiles = dir.listFiles(download);
		}

		FileFilter fileFilter = new WildcardFileFilter("*" + file + "*");
		waitForFileToBePresent(dir, fileFilter);
		File[] files = dir.listFiles(fileFilter);

		waitForFileSizeToStopChanging(files[files.length - 1]);
	}

	/**
	 * Waits up to 30 seconds until a file in the given Directory matching the given
	 * filter is present. Note that this could be a file still being downloaded.
	 */
	private static void waitForFileToBePresent(File dir, FileFilter fileFilter) {
		waitForFileToBePresent(30, dir, fileFilter);
	}

	/**
	 * Waits up to the given number of seconds for a file in the given Directory
	 * matching the given filter is present. Note that this could be a file still
	 * being downloaded.
	 */
	private static void waitForFileToBePresent(int secondsToWait, File dir, FileFilter fileFilter) {
		File[] files = dir.listFiles(fileFilter);
		// waits for file to be present
		for (var i = 0; i < secondsToWait; i++) {
			if (files.length > 0) {
				break;
			}
			files = dir.listFiles(fileFilter);
			TestUtil.sleep(1);
		}
	}

	/**
	 * Waits until the given file's size stops changing; this is to ensure that a
	 * file is completely downloaded before returning it.
	 */
	private static void waitForFileSizeToStopChanging(File file) {
		var oldSize = 0L;
		var newSize = 1L;
		var fileIsOpen = true;

		while ((newSize > oldSize) || fileIsOpen) {
			oldSize = file.length();
			TestUtil.sleep(1);
			newSize = file.length();

			try {
				var fileInput = new FileInputStream(file);
				fileIsOpen = false;
				fileInput.close();
			} catch (IOException e) {
				logger.warn(e);
			}

		}
	}
}