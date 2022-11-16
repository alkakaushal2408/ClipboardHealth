package test.java.utils;

import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * This class is for downloading files from the app under test, and interacting
 * with the downloaded files.
 */
public class FileUtil {

	private FileUtil() {
		throw new IllegalStateException("Utility class");
	}

	private static Logger logger = LogManager.getLogger();

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
}