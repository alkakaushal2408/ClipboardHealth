package test.java.config.webdriver;

import org.openqa.selenium.WebDriver;

public class WebDriverManager {

    /**
     * Handles parallelization of webdrivers, keeping them stored in their local
     * threads so that different TestNG threads cannot use the same webdriver.
     */

    private WebDriverManager() {
        throw new IllegalStateException("Utility class");
    }

    private static ThreadLocal<WebDriver> webDriver = new ThreadLocal<>();
    private static ThreadLocal<String> mwh = new ThreadLocal<>();

    public static WebDriver getDriver() {
        return webDriver.get();
    }

    public static void setWebDriver(WebDriver driver) {
        webDriver.set(driver);
    }

    public static void unload() {
        mwh.remove();
        webDriver.remove();
    }

    public static String getMainWindowHandle() {
        return mwh.get();
    }

    public static void setMainWindowHandle(String mainWindowHandle) {
        mwh.set(mainWindowHandle);
    }
}

