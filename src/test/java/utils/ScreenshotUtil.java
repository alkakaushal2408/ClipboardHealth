package test.java.utils;

import test.java.Config;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.shooting.ShootingStrategies;
import test.java.config.webdriver.WebDriverManager;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ScreenshotUtil {

	private ScreenshotUtil() {
		throw new IllegalStateException("Utility class");
	}

	private static Logger logger = LogManager.getLogger();

	/**
	 * Takes a screenshot of the current page and stores it in the tmp screenshots
	 * directory
	 */
	public static String takeScreenshot(String filename) {
		if (WebDriverManager.getDriver() != null) {
			try {
				var screenshot = (new AShot()
						.shootingStrategy(ShootingStrategies.viewportPasting(ShootingStrategies.scaling(2.0f), 100))
						.takeScreenshot(WebDriverManager.getDriver()));
				ImageIO.write(screenshot.getImage(), "PNG",
						new File(Paths.get(Config.SCREENSHOT_DIR, filename + ".png").toString()));
				return Path.of(Config.SCREENSHOT_DIR, filename + ".png").toString();
			} catch (NoSuchSessionException se) {
				logger.error("Browser was never opened");
			} catch (NoSuchWindowException e) {
				logger.error("Browser was closed unexpectedly!");
			} catch (NullPointerException np) {
				logger.error("No screenshot taken, as driver was not set.");
			} catch (IOException e) {
				logger.error(e);
			}
		} else {
			logger.warn("Skipped taking screenshot because no WebDriver instance existed.");
		}
		return null;
	}

	/**
	 * Takes a screenshot of the given element and stores it in the tmp screenshots
	 * directory
	 */
	public static String takeScreenshot(By by, String filename) {
		try {
			return takeScreenshot(WebDriverManager.getDriver().findElement(by), filename);
		} catch (NoSuchElementException ne) {
			logger.error(
					"Element located {} was not found for screenshot with filename {}. Screenshoting entire page instead.",
					by, filename);
			return takeScreenshot(filename);
		}
	}

	/**
	 * Takes a screenshot of the given element and stores it in the tmp screenshots
	 * directory
	 */
	public static String takeScreenshot(WebElement e, String filename) {
		try {
			var scrFile = e.getScreenshotAs(OutputType.FILE);
			FileUtils.copyFile(scrFile, new File(Paths.get(Config.SCREENSHOT_DIR, filename + ".png").toString()));
			return Path.of(Config.SCREENSHOT_DIR, filename + ".png").toString();
		} catch (IOException ioe) {
			logger.fatal(ioe);
			return null;
		}
	}
}
