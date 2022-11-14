package test.java.testcases.base;

import com.aventstack.extentreports.utils.StringUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.*;
import test.java.Config;
import test.java.config.webdriver.WebDriverFactory;
import test.java.config.webdriver.WebDriverManager;
import test.java.utils.FileUtil;

import java.lang.reflect.Method;
import java.util.Arrays;

public class TestBase {

    protected Logger logger = LogManager.getLogger();

    @BeforeSuite(alwaysRun = true, groups = { "parameters" })
    public void setupSuite(ITestContext ctx) {
        outputParameters();
    }

    /** Logs configured parameters for the test run */
    private void outputParameters() {
        logger.info("Test URL: {}", Config.TEST_URL);
        logger.info("Browser: {}", Config.TEST_BROWSER);
        logger.info("OS: {}", Config.TEST_OS);
        logger.info("Grid Enabled: {}", Config.GRID_ENABLED);
    }

    /**
     * Cleans up tmp files created by previous script runs prior to starting a new
     * run. If the tmp directories don't already exist, creates them.
     */
    @BeforeSuite(groups = { "cleanup" }, dependsOnGroups = { "parameters" })
    public void createAndCleanUpTmpFolders() {
        FileUtil.createDirectories(Config.DOWNLOAD_DIR);
        FileUtil.createDirectories(Config.SCREENSHOT_DIR);
        FileUtil.createDirectories(Config.VIDEO_DIR);
        FileUtil.createDirectories(Config.GENERATED_FILES_DIR);

        FileUtil.cleanDirectory(Config.DOWNLOAD_DIR);
        FileUtil.cleanDirectory(Config.SCREENSHOT_DIR);
        FileUtil.cleanDirectoryOfFilesWithExtension(Config.VIDEO_DIR, "mp4");
        FileUtil.cleanDirectoryIncludingFolders(Config.GENERATED_FILES_DIR);
    }

    /**
     * Creates a WebDriver using the WebDriverFactory prior to beginning a Test,
     * provided the test isn't in the "nodriver" group
     */
    @BeforeMethod(alwaysRun = true)
    public void setupWebDriver(Method method) {
        var testClass = method.getAnnotation(Test.class);
        if (Arrays.stream(testClass.groups()).noneMatch("nodriver"::equals)) {
            var factory = new WebDriverFactory();
            WebDriver driver = factory.createInstance(Config.TEST_BROWSER, method.getName());
            WebDriverManager.setWebDriver(driver);
        }
    }

    /** Sets up the test class and method names for the logger */
    @BeforeMethod(alwaysRun = true)
    public void setupLogger(ITestContext ctx, Method method) {
        ThreadContext.put("testClassName", ctx.getAllTestMethods()[0].getInstance().getClass().getSimpleName());
        ThreadContext.put("testName", method.getName());
    }

    /** Takes a screenshot if the test failed, then tears down the WebDriver */
    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        var driver = WebDriverManager.getDriver();
        if (driver != null && Boolean.TRUE.equals(Config.GRID_ENABLED)
                && StringUtil.isNotNullOrEmpty(Config.GRID_RESULT_KEY)) {
            String status = result.isSuccess() ? "passed" : "failed";
            JavascriptExecutor js = (JavascriptExecutor) driver;
            setBrowserstackTestName(result, js);
            js.executeScript(Config.GRID_RESULT_KEY.replace("test-result", status));
        }
        terminate();
    }

    /**
     * Sets the browserstack test name to the TestNG Description tag, to make for
     * more descriptive browserstack reports on the dashboard
     */
    private void setBrowserstackTestName(ITestResult result, JavascriptExecutor js) {
        if (Config.GRID_URL.toLowerCase().contains("browserstack")
                && StringUtil.isNotNullOrEmpty(result.getMethod().getDescription())) {
            js.executeScript("browserstack_executor: {\"action\": \"setSessionName\", \"arguments\": {\"name\":\""
                    + result.getMethod().getDescription() + "\" }}");
        }
    }

    /**
     * Closes and quits the WebDriver, then unloads it from the WebDriverManager
     * ThreadLocal variable
     */
    public void terminate() {
        var driver = WebDriverManager.getDriver();
        if (driver != null) {
            driver.quit();
        }
        WebDriverManager.unload();
    }

    public Verify verify() {
        return new Verify();
    }



}
