package test.java;

import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvEntry;
import io.netty.util.internal.StringUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import test.java.utils.DateUtil;

import java.nio.file.Paths;
import java.util.TimeZone;

/**
 * Sets and handles all environment variables and system properties. For any
 * variable, if a system property is present, will use it. If not, will use an
 * environment variable. This allows overriding the variable when running maven
 * from the command-line.
 */
public final class Config {

	private Config() {

	}

	static Logger logger = LogManager.getLogger();

	public static final String BUILD_NAME;

	public static final String TEST_URL;
	public static final String TEST_BROWSER;
	public static final String TEST_BROWSER_VERSION;
	public static final Boolean HEADLESS_ENABLED;
	public static final String TEST_OS;

	public static final String TMP_DIR;
	public static final String DOWNLOAD_DIR;
	public static final String SCREENSHOT_DIR;
	public static final String VIDEO_DIR;
	public static final String GENERATED_FILES_DIR;

	public static final Boolean GRID_ENABLED;

	public static final String GRID_URL;
	public static final String GRID_USERNAME;
	public static final String GRID_ACCESS_KEY;
	public static final String GRID_RESULT_KEY;

	static {
		loadDotenv();

		String tmpDirProperty = getEnvironmentVariableOrSystemProperty("TMP_DIR");
		TMP_DIR = StringUtil.isNullOrEmpty(tmpDirProperty) ? Paths.get(System.getProperty("user.dir"), "tmp").toString()
				: tmpDirProperty;
		DOWNLOAD_DIR = Paths.get(TMP_DIR, "downloads").toString();
		VIDEO_DIR = Paths.get(TMP_DIR, "videos").toString();
		GENERATED_FILES_DIR = Paths.get(TMP_DIR, "generatedfiles").toString();

		TEST_URL = getEnvironmentVariableOrSystemProperty("TEST_URL");
		TEST_BROWSER = getEnvironmentVariableOrSystemProperty("TEST_BROWSER");
		TEST_BROWSER_VERSION = getEnvironmentVariableOrSystemProperty("TEST_BROWSER_VERSION");
		HEADLESS_ENABLED = Boolean.parseBoolean(getEnvironmentVariableOrSystemProperty("HEADLESS_ENABLED"));
		GRID_ENABLED = Boolean.parseBoolean(getEnvironmentVariableOrSystemProperty("GRID_ENABLED"));

		/*
		 * Sets the TEST_OS constant. If GRID_ENABLED is true, pulls it from the .env
		 * file or passed in property, otherwise if the tests are running locally, sets
		 * it as the current environment's OS.
		 */
		if (Boolean.TRUE.equals(GRID_ENABLED)) {
			TEST_OS = getEnvironmentVariableOrSystemProperty("TEST_OS");
		} else {
			TEST_OS = System.getProperty("os.name");
		}

		/**
		 * The BROWSERSTACK_BUILD_NAME env var is set by the browserstack extension in
		 * Azure Pipelines, otherwise it doesn't apply. If not running in Azure
		 * Pipelines, generate an alternate build name instead
		 */
		if (StringUtil.isNullOrEmpty(getEnvironmentVariableOrSystemProperty("BROWSERSTACK_BUILD_NAME"))) {
			BUILD_NAME = "(" + Config.TEST_OS + ") " + Config.TEST_BROWSER + " "
					+ DateUtil.getCurrentDate(TimeZone.getTimeZone("America/Chicago"), "MM-dd-yyyy hhmm") + "CST";
		} else {
			BUILD_NAME = getEnvironmentVariableOrSystemProperty("BROWSERSTACK_BUILD_NAME");
		}

		SCREENSHOT_DIR = Paths.get(System.getProperty("user.dir"), "test-output", "extent", "screenshots").toString();


		GRID_URL = getEnvironmentVariableOrSystemProperty("GRID_URL");
		GRID_USERNAME = getEnvironmentVariableOrSystemProperty("GRID_USERNAME");
		GRID_ACCESS_KEY = getEnvironmentVariableOrSystemProperty("GRID_ACCESS_KEY");
		GRID_RESULT_KEY = getEnvironmentVariableOrSystemProperty("GRID_RESULT_KEY");
	}

	/**
	 * If System Property matching the key is set, returns it. Otherwise returns
	 * Environment Variable for the key.
	 */
	public static String getEnvironmentVariableOrSystemProperty(String key) {
		String prop = System.getProperty(key);
		return StringUtil.isNullOrEmpty(prop) ? System.getenv(key) : prop;
	}

	/**
	 * Loads the .env file using java-dotenv, then loads the variables into system
	 * properties. If the system property already exists, does not overwrite it, so
	 * that system properties set via maven cli at runtime are not overwritten
	 */
	private static void loadDotenv() {
		var dotenv = Dotenv.configure().ignoreIfMissing().load();
		for (DotenvEntry e : dotenv.entries()) {
			if (StringUtil.isNullOrEmpty(System.getProperty(e.getKey()))) {
				System.setProperty(e.getKey(), e.getValue());
			}
		}
	}
}
