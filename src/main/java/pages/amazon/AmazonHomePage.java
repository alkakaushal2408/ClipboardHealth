package main.java.pages.amazon;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import pages.PageObject;
import test.java.Config;
import test.java.config.logger.Logger;

public class AmazonHomePage extends PageObject {
    public static final String TITLE = "Online Shopping site in India: Shop Online for Mobiles, Books, Watches, Shoes and More - Amazon.in";
    private By hamburgerMenuLocator = By.id("nav-hamburger-menu");
    private By tvAppliancesElectronicsDepartmentLocator = By.xpath("//a[@class='hmenu-item']/child::div[text()='TV, Appliances, Electronics']");
    private By televisionsSubmenuLocator = By.xpath("//a[@class='hmenu-item' and text()='Televisions']");
    private By samsungCheckboxLocator = By.xpath("//span[text()='Samsung']");
    private By sortByHighToLowPricingDropdownValueLocator = By.xpath("//select[@id='s-result-sort-select']//child::option[@value='price-desc-rank']");

    public AmazonHomePage() {
        super();
        loadPage(Config.TEST_URL);
        waitForPageLoad();
    }

    public AmazonHomePage(WebDriver driver) {
        super(driver);
        Logger.pass("Loading page " + TITLE);
        waitForPageLoad();
    }

    private AmazonHomePage waitForPageLoad() {
        wait(15).until(ExpectedConditions.titleIs(TITLE));
        Logger.pass("Loaded page: " + TITLE);
        return this;
    }

    public AmazonHomePage performHamburgerMenuClick() {
        click(hamburgerMenuLocator);
        return this;
    }

    public AmazonHomePage performTVAppliancesElectronicsDepartmentClick() {
        click(tvAppliancesElectronicsDepartmentLocator);
        return this;

    }

    public AmazonHomePage performTelevisionsSubmenuClick() {
        click(televisionsSubmenuLocator);
        return this;
    }

    public AmazonHomePage performFilterBySamsung() {
        selectCheckbox(samsungCheckboxLocator);
        return this;
    }

    public AmazonHomePage performSortByHighToLowPrice() {
        click(sortByHighToLowPricingDropdownValueLocator);
        return this;
    }

}
