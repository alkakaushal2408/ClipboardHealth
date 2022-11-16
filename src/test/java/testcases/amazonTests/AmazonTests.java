package test.java.testcases.amazonTests;

import main.java.pages.amazon.AmazonPage;
import org.apache.logging.log4j.Level;
import org.testng.annotations.Test;
import test.java.config.logger.Logger;
import test.java.testcases.base.TestBase;

public class AmazonTests extends TestBase {

    @Test(groups = {
            "Assignment"}, description = "Verify that the section 'About this item' is present")
    public void verifyCreateNewShowsAccountOption() {
        var amazonPage = new AmazonPage();
        amazonPage.performHamburgerMenuClick();
        amazonPage.performTVAppliancesElectronicsDepartmentClick();
        amazonPage.performTelevisionsSubmenuClick();
        amazonPage.performFilterBySamsung();
        amazonPage.performSortByHighToLowPrice();
        amazonPage.openSecondHighestPricesItem();
        amazonPage.switchToProductWindow();
        Logger.pass("About this item section located " + amazonPage.aboutThisItemSectionLocator);
        verify().elementPresent(amazonPage.aboutThisItemSectionLocator);
    }
}
