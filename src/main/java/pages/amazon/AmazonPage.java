package main.java.pages.amazon;

import main.java.pages.PageObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import test.java.Config;
import test.java.config.logger.Logger;

public class AmazonPage extends PageObject {
    private static final String TITLE = "Online Shopping site in India: Shop Online for Mobiles, Books, Watches, Shoes and More - Amazon.in";
    private static final String TELEVISIONPAGETITLE = "Buy the latest LED TVs, 4K TVs and Android TVs online at Best Prices in India-Amazon.in | Shop by size, price, features and more";
    private By hamburgerMenuLocator = By.id("nav-hamburger-menu");
    private By tvAppliancesElectronicsDepartmentLocator = By.xpath("//a[@class='hmenu-item']/child::div[text()='TV, Appliances, Electronics']");
    private By televisionsSubmenuLocator = By.xpath("//a[@class='hmenu-item' and text()='Televisions']");
    private By samsungCheckboxLocator = By.xpath("//span[text()='Samsung']//preceding-sibling::div");
    private By sortByHighToLowPricingDropdownValueLocator = By.xpath("//select[@id='s-result-sort-select']//child::option[@value='price-desc-rank']");
    private By secondProductLocator = By.xpath("//div[@data-index='2']");
    public By aboutThisItemSectionLocator = By.xpath("//h1[text()=' About this item ']//following-sibling::ul");

    public AmazonPage() {
        super();
        loadPage(Config.TEST_URL);
        waitForPageLoad();
    }

    public AmazonPage(WebDriver driver) {
        super(driver);
        Logger.pass("Loading page " + TITLE);
        waitForPageLoad();
    }

    private AmazonPage waitForPageLoad() {
        wait(15).until(ExpectedConditions.titleIs(TITLE));
        Logger.pass("Loaded page: " + TITLE);
        return this;
    }

    public AmazonPage performHamburgerMenuClick() {
        click(hamburgerMenuLocator);
        return this;
    }

    public AmazonPage performTVAppliancesElectronicsDepartmentClick() {
        click(tvAppliancesElectronicsDepartmentLocator);
        return this;

    }

    public AmazonPage performTelevisionsSubmenuClick() {
        click(televisionsSubmenuLocator);
        wait(10).until(ExpectedConditions.titleIs(TELEVISIONPAGETITLE));
        return this;
    }

    public AmazonPage performFilterBySamsung() {
        if (isElementPresent(By.xpath("//span[text()='Brands']"))) {
            WebElement element = driver.findElement(By.xpath("//span[text()='Brands']"));
            WebElement checkboxElement = driver.findElement(samsungCheckboxLocator);
            Actions actions = new Actions(driver);
            actions.moveToElement(element);
            actions.perform();
            actions.click(checkboxElement);
            actions.perform();
        }
        wait(30).until(ExpectedConditions.presenceOfElementLocated(By.xpath("//span[@data-component-type='s-search-results']")));
        return this;

    }

    public AmazonPage performSortByHighToLowPrice() {
        click(sortByHighToLowPricingDropdownValueLocator);
        return this;
    }

    public AmazonPage openSecondHighestPricesItem() {
        click(secondProductLocator);
        return this;
    }

    public AmazonPage switchToProductWindow() {
        switchToPopUp();
        return this;
    }

}


