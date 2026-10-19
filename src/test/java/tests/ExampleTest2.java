package tests;
import com.microsoft.playwright.*;
import com.microsoft.playwright.assertions.LocatorAssertions;
import com.microsoft.playwright.assertions.PlaywrightAssertions;
import com.microsoft.playwright.options.AriaRole;
import org.testng.annotations.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class ExampleTest2 {
    Page page;
    Browser browser;
    Playwright playwright;

//    @Test
    public void setUp() {
        playwright = Playwright.create();

        // Using different Browsers: Webkit, Chrome, Firefox
        // Browser browser = playwright.firefox().launch(new BrowserType.LaunchOptions().setHeadless(false));
        // Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));

        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
        page = browser.newPage();
        page.setDefaultTimeout(50000);
        page.navigate("https://www.saucedemo.com/");
        PlaywrightAssertions.setDefaultAssertionTimeout(7000);
    }

    @Test(priority = 1, description = "Access and Login to the Swag labs website")
    public void Login() {
        setUp();
        System.out.println(page.title());
        page.getByPlaceholder("Username").fill("standard_user");
        page.getByLabel("Password").fill("secret_sauce");
        page.locator("#login-button").click();
//        assertThat(page).hasURL("https://saucedemo.cominventory.html");

    }

    @Test(priority = 2, description = "Verify if able to access the Inventory Page")
    public void Inventory() {
        System.out.println(page.title());
        assertThat(page).hasURL("https://www.saucedemo.com/inventory.html");
    }

}
