package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import test.java.Config;
import test.java.config.logger.Logger;
import test.java.config.webdriver.WebDriverManager;

import java.time.Duration;

public class PageObject {

    protected WebDriver driver;

    public PageObject() {
        this(WebDriverManager.getDriver());
    }

    public PageObject(WebDriver driver) {
        this.driver = driver;
    }

    public WebDriver getDriver() {
        return this.driver;
    }

    /**
     * Checks if any WebElement is found via the given By locator
     *
     * @param by Locator to check for elements
     */

    public boolean isElementPresent(By by) {
        return !driver.findElements(by).isEmpty();
    }

    /**
     * Checks if WebElement is present, visible and clickable via the given By
     * locator
     *
     * @param by Locator to check for elements
     */
    public boolean isElementClickable(By by) {
        var clickable = false;
        if (isElementPresent(by)) {
            WebElement e = driver.findElement(by);
            if (e.isDisplayed() && e.isEnabled()) {
                clickable = true;
            }
        }
        return clickable;
    }

    /**
     * Checks if given text is present in a given element
     *
     * @param e    WebElement to check
     * @param text Text to check for
     */
    public boolean isTextPresentInElement(WebElement e, String text) {
        text = text.replace("'", "\'");
        return e.getText().contains(text);
    }

    /**
     * Clicks on the given element
     *
     * @param by Locator for the element to click
     */
    public PageObject click(By by){
        wait(15).until(ExpectedConditions.elementToBeClickable(by));
        return click(driver.findElement(by));
    }

    /** Creates a new WebDriverWait instance with the given wait duration */
    protected WebDriverWait wait(int secondsToWait) {
        return new WebDriverWait(driver, Duration.ofSeconds(secondsToWait), Duration.ofMillis(250));
    }

    /**
     * Clicks on the given element
     *
     * @param e Element to click
     */
    public PageObject click(WebElement e) {
        e.click();
        return this;
    }
    /**
     * Loads a page using the given URL. Can be relative to the domain under test
     * defined in the {@link main.java.Config #TEST_URL} variable, or a full URL.
     *
     * Only use this method if the PageObject being returned matches the Page Object
     * it was called from. Otherwise, use {@link # loadPageFullURL(String, Class)} so
     * that the new page's class can be instantiated.
     *
     * @param url Full URL of the page to load
     */
    public PageObject loadPage(String url) {
        if (!url.contains("http") && !url.contains("www\\.") && !url.contains("\\.com") && !url.contains("\\.ca")
                && !url.contains("\\.org")) {
            url = Config.TEST_URL + "/" + url;
        }
        Logger.info("Loading URL: " + url);
        driver.get(url);
        return this;
    }

    /**
     * Selects the given checkbox element if not already selected
     *
     * @param by Locator for the checkbox
     */
    public PageObject selectCheckbox(By by) {
        return selectCheckbox(driver.findElement(by));
    }

    /**
     * Selects the given checkbox element if not already selected
     *
     * @param e Element to select
     */
    public PageObject selectCheckbox(WebElement e) {
        if (!e.isSelected()) {
            click(e);
        }
        return this;
    }


}
