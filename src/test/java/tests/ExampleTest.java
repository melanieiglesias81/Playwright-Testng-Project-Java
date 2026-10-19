package tests;

import com.microsoft.playwright.*;
import org.testng.*;
import com.microsoft.playwright.Page.GetByRoleOptions;
import com.microsoft.playwright.options.AriaRole;
import org.testng.annotations.Test;

import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class ExampleTest extends BaseTest {

    @Test
    public void homePageHasTitle() {
        page().navigate("https://playwright.dev");
        assertThat(page()).hasTitle(Pattern.compile("Playwright"));
    }

    @Test
    public void canClickGetStarted() {
        page().navigate("https://playwright.dev");
        page().getByRole(AriaRole.LINK,
                new GetByRoleOptions().setName("Get started")).click();
        assertThat(page()).hasURL(Pattern.compile(".*intro"));
    }
}