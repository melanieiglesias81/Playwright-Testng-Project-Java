package tests;

import com.microsoft.playwright.*;
import org.testng.annotations.*;

public class BaseTest {
    private static final ThreadLocal<Playwright> playwright = new ThreadLocal<>();
    private static final ThreadLocal<Browser> browser = new ThreadLocal<>();
    private static final ThreadLocal<BrowserContext> context = new ThreadLocal<>();
    private static final ThreadLocal<Page> page = new ThreadLocal<>();

    protected Page page() {
        return page.get();
    }

    @BeforeClass
    public void launchBrowser() {
        playwright.set(Playwright.create());
        browser.set(playwright.get().chromium().launch(
                new BrowserType.LaunchOptions().setHeadless(false)));
    }

    @BeforeMethod
    public void createContext() {
        context.set(browser.get().newContext());
        page.set(context.get().newPage());
    }

    @AfterMethod(alwaysRun = true)
    public void closeContext() {
        context.get().close();
    }

    @AfterClass(alwaysRun = true)
    public void closeBrowser() {
        browser.get().close();
        playwright.get().close();
    }
}