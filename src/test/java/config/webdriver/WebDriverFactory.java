package test.java.config.webdriver;

import io.netty.util.internal.StringUtil;
import test.java.Config;
import org.apache.commons.validator.routines.UrlValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxDriverLogLevel;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Class for building WebDriver instances according to the current profile,
 * which specifies browser, headless/not, target environment, and whether it is
 * running via grid or locally
 */
public class WebDriverFactory {

	static final Logger logger = LogManager.getLogger();
	private ChromeOptions chromeOptions = new ChromeOptions();
	private boolean gridEnabled = Config.GRID_ENABLED;
	private boolean headlessEnabled = Config.HEADLESS_ENABLED;

	WebDriver createInstance(String browser) {
		return createInstance(browser, null);
	}

	public WebDriver createInstance(String browser, String testName) {
		if (gridEnabled) {
			return createInstanceForGrid(testName);
		} else {
			return createLocalInstance(browser.trim());
		}
	}

	/**
	 * Builds a HashMap of all chromeprefs that are shared regardless of if the
	 * current test run is in a grid or local
	 */
	private HashMap<String, Object> buildChromePrefs() {
		HashMap<String, Object> chromePrefs = new HashMap<>();
		chromePrefs.put("profile.default_content_settings.popups", 0);

		// disable the save password popup
		chromePrefs.put("credentials_enable_service", Boolean.valueOf(false));

		// disable password manager
		chromePrefs.put("profile.password_manager_enabled", Boolean.valueOf(false));

		// disable chrome built in pdf reader (so that pdfs download instead)
		chromePrefs.put("pdfjs.disabled", true);

		// disable download prompt
		chromePrefs.put("download.prompt_for_download", false);

		// needed to set download directory
		chromePrefs.put("download.directory_upgrade", true);

		// prevent chrome from opening pdfs (so that pdfs download instead)
		chromePrefs.put("plugins.always_open_pdf_externally", true);

		// disables safebrowsing
		chromePrefs.put("safebrowsing.enabled", false);

		// disable chrome's PDF viewer as its not needed
		List<String> pluginsToDisable = new ArrayList<>();
		pluginsToDisable.add("Chrome PDF Viewer");
		chromePrefs.put("plugins.plugins_disabled", pluginsToDisable);

		return chromePrefs;
	}

	/**
	 * Builds a HashMap of ChromeOptions that are shared regardless of if the
	 * current test run is in a grid or local
	 */
	private void setChromeOptions(HashMap<String, Object> chromePrefs) {
		chromeOptions.setExperimentalOption("prefs", chromePrefs);
		chromeOptions.addArguments("--test-type");
		chromeOptions.addArguments("--window-size=1920,1080");
		chromeOptions.addArguments("--disable-dev-shm-usage");

		// this is to disable the "this browser is being controlled by
		// automation" popup
		chromeOptions.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation"));

		// accepts SSL certificates
		chromeOptions.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);

		// if config headless is true, adds headless argument
		if (headlessEnabled) {
			chromeOptions.addArguments("--headless");

			// necessary for headless to work in grid
			chromeOptions.addArguments("--no-sandbox");

			// these are necessary to improve headless
			// performance
			chromeOptions.addArguments("--no-proxy-server");
			chromeOptions.addArguments("--proxy-server='direct://'");
			chromeOptions.addArguments("--proxy-bypass-list=*");
		}
	}

	/**
	 * Creates a WebDriver instance in the Grid
	 * 
	 * @param browser  Specifies the browser driver to instantiate
	 * @param testName Sets the test name in the grid
	 */
	private WebDriver createInstanceForGrid(String testName) {

		var caps = new DesiredCapabilities();
		// Configure your capabilities here
		caps.setCapability("platform", Config.TEST_OS);
		caps.setCapability("os", Config.TEST_OS.split(" ")[0]);
		caps.setCapability("osVersion", Config.TEST_OS.split(" ")[1]);
		caps.setCapability("browserName", Config.TEST_BROWSER);
		if (!Config.GRID_URL.contains("localhost")) {
			caps.setCapability("browserVersion", Config.TEST_BROWSER_VERSION);
		}
		caps.setCapability("build", Config.BUILD_NAME);
		caps.setCapability("name", testName);
		caps.setCapability("plugin", "git-testng");

		var driver = new RemoteWebDriver(configureGridURL(), caps);
		driver.manage().window().maximize();
		return driver;
	}

	/**
	 * Uses the environment variables and/or system properties to formulate the Grid
	 * URL, including detecting whether http or https protocol should be used if
	 * it's not already provided.
	 */
	private URL configureGridURL() {
		URL url = null;
		var schema = new String[] { "https" };
		var urlValidator = new UrlValidator(schema);
		String gridURL = null;
		if (StringUtil.isNullOrEmpty(Config.GRID_USERNAME)) {
			gridURL = Config.GRID_URL;
		} else {
			gridURL = Config.GRID_USERNAME + ":" + Config.GRID_ACCESS_KEY + "@" + Config.GRID_URL.replace("@", "");
		}
		if (!gridURL.contains("http")) {
			if (urlValidator.isValid("https://" + gridURL)) {
				gridURL = "https://" + gridURL;
			} else {
				gridURL = "http://" + gridURL;
			}
		}
		try {
			url = new URL(gridURL);
		} catch (MalformedURLException e) {
			logger.error(e);
		}
		return url;
	}

	/** Creates a local WebDriver instance for running on the local machine */
	private WebDriver createLocalInstance(String browser) {
		WebDriver driver = null;
		setDriverPath(browser);
		if (browser.equalsIgnoreCase("Firefox")) {
			var profile = new FirefoxProfile();
			profile.setPreference("browser.download.folderList", 2);
			profile.setPreference("browser.download.manager.showWhenStarting", false);
			profile.setPreference("browser.download.dir", Config.DOWNLOAD_DIR);
			profile.setPreference("browser.helperApps.neverAsk.saveToDisk", "text/csv");
			profile.setPreference("browser.helperApps.alwaysAsk.force", false);

			var options = new FirefoxOptions();
			options.setProfile(profile);
			options.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
			options.setLogLevel(FirefoxDriverLogLevel.FATAL);

			driver = new FirefoxDriver(options);
			driver.manage().window().maximize();

		} else if (browser.equalsIgnoreCase("Chrome")) {

			HashMap<String, Object> chromePrefs = buildChromePrefs();
			// set default download directory
			chromePrefs.put("download.default_directory", Config.DOWNLOAD_DIR);
			setChromeOptions(chromePrefs);

			// starts driver
			driver = new ChromeDriver(chromeOptions);
		} else {
			test.java.config.logger.Logger.fatal("No driver configuration for browser: " + browser);
		}
		return driver;
	}

	/**
	 * Sets the system property for the driver path in the local repo according to
	 * the system's OS
	 */
	private void setDriverPath(String browser) {
		if (browser.equalsIgnoreCase("firefox")) {
			browser = "gecko";

		}
		var browserProperty = "webdriver." + browser + ".driver";
		var webdriverFolder = Paths.get("src", "test", "resources", "webdrivers").toString();
		if (System.getProperty("os.name").contains("Mac")) {
			System.setProperty(browserProperty, Paths.get(webdriverFolder, "mac", browser + "driver").toString());
		} else if (System.getProperty("os.name").contains("Linux")) {
			System.setProperty(browserProperty, Paths.get(webdriverFolder, "linux", browser + "driver").toString());
		} else {
			System.setProperty(browserProperty, Paths.get(webdriverFolder, browser + "driver.exe").toString());
		}
	}
}
