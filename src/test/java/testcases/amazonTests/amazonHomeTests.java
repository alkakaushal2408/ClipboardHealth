package test.java.testcases.amazonTests;

import main.java.pages.amazon.AmazonHomePage;
import org.testng.annotations.Test;
import test.java.testcases.base.TestBase;

public class amazonHomeTests  extends TestBase {

    @Test(groups = {
            "Assignment" }, description = "Verify that the section 'About this item' is present")
    public void verifyCreateNewShowsAccountOption() {
        var amazonHomePage = new AmazonHomePage();
        amazonHomePage.performHamburgerMenuClick();
       amazonHomePage.performTVAppliancesElectronicsDepartmentClick();
       amazonHomePage.performTelevisionsSubmenuClick();
       amazonHomePage.performFilterBySamsung();
       amazonHomePage.performSortByHighToLowPrice();
      /* amazonHomePage.openSecondHighestPricesItem();
       amazonHomePage.swictchToProductWindow();*/

    }
}
