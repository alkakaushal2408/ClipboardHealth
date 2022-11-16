package test.java.testcases.base;

import main.java.pages.PageObject;
import org.openqa.selenium.By;
import org.testng.Assert;
import test.java.config.logger.Logger;
import test.java.utils.DateUtil;
import test.java.utils.ErrorUtil;
import test.java.utils.ScreenshotUtil;

public class Verify {

    /**
     * The Verify class contains soft assertions so that when an assertion fails, a
     * screenshot is taken, the failure is added to the error logger, and then the
     * test can continue running.
     */

    /*
     * Each time a failure occurs, the current errNum is used for the screenshot
     * filename, and then incremented for the next failure
     */
    static int errNum = 1;
    PageObject pO;
    String testName = "";

    /**
     * Gets a new Page Object class (which will automatically use the current Test's
     * Threadsafe WebDriver), as well as the current Test's name to use as the
     * filename in any screenshot tests
     */
    public Verify() {
        pO = new PageObject();
        var stackTraceStep = 2;
        long startTime = System.currentTimeMillis();
        while ((!testName.contains("Test") && !testName.contains("setUp"))
                && (System.currentTimeMillis() - startTime) < 10000) {
            testName = Thread.currentThread().getStackTrace()[stackTraceStep].getMethodName();
            stackTraceStep++;
        }
    }

    /**
     * Verifies that the given By locator finds at least one matching Element on the
     * page
     *
     * @param by Element locator to check
     */
    public void elementPresent(By by) {
        elementPresent(by, "Failed to find element located '" + by + "'.");
    }

    /**
     * Verifies that the given By locator finds at least one matching Element on the
     * page
     *
     * @param by     Element locator to check
     * @param errMsg Custom error message
     */
    public void elementPresent(By by, String errMsg) {
        try {
            Assert.assertTrue(pO.isElementPresent(by), errMsg);
            Logger.pass("Verified that an element located by '" + by + "' was present.");
        } catch (AssertionError e) {
            String screenshot = ScreenshotUtil.takeScreenshot(by,
                    testName + "-VerifyElementPresent_" + DateUtil.getCurrentUTCDateTimeStamp());
            ErrorUtil.addVerificationFailure(e, screenshot);
        }
    }

}
