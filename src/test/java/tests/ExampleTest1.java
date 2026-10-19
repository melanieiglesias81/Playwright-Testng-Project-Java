package tests;

import com.microsoft.playwright.*;
import com.microsoft.playwright.assertions.LocatorAssertions;
import com.microsoft.playwright.assertions.PlaywrightAssertions;
import com.microsoft.playwright.options.AriaRole;
import org.testng.annotations.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class ExampleTest1
{
    Page page;
    Browser browser;
    Playwright playwright;

    @Test
    public void setUp() {
        playwright = Playwright.create();

        // Using different Browsers: Webkit, Chrome, Firefox
        // Browser browser = playwright.firefox().launch(new BrowserType.LaunchOptions().setHeadless(false));
        // Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));


        browser = playwright.webkit().launch(new BrowserType.LaunchOptions().setHeadless(false));
        page = browser.newPage();
//        page.setDefaultTimeout(50000);
        page.navigate("https://eventhub.rahulshettyacademy.com/login");
        PlaywrightAssertions.setDefaultAssertionTimeout(7000);
    }

    @Test(description = "Create Event Book that event and verify if its booked")
    public void DemoTest() {
        setUp();
        System.out.println(page.title());
        assertThat(page).hasTitle("EventHub — Discover & Book Events");
        System.out.println("Hello World");

        // Input Text and Click
        page.getByPlaceholder("you@email.com").fill("rahulshetty1@yahoo.com");
        page.getByLabel("Password").fill("Magiclife1!");
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Sign In")).click();

        assertThat(page.getByRole(AriaRole.LINK,
                new Page.GetByRoleOptions().setName("Browse Events →"))).isVisible();

        // Navigate and Fill Event Form
        page.navigate("https://eventhub.rahulshettyacademy.com/admin/events");
        page.locator("#event-title-input").fill("QA Summit Rahul Shetty 1", new Locator.FillOptions().setTimeout(10000));
        page.locator("#admin-event-form textarea").fill("Rahul Shetty QA Meetups");
        page.getByLabel("Category").selectOption("Concert");
        page.getByLabel("City").fill("Test City");
        page.getByLabel("Venue").fill("Test Venue");
        page.getByLabel("Event Date & Time").fill("2026-12-18T07:25");
        page.getByLabel("Price ($)").fill("100");
        page.getByLabel("Total Seats").fill("50");
        page.locator("#add-event-btn").click(new Locator.ClickOptions().setTimeout(12000));

        // Verify Event Creation
        assertThat(page.getByText("Event created")).isVisible(); //5 seconds



        // Step 2 - Find newly created event in the events page
        page.locator("#nav-events").click();
        Locator eventCards = page.getByTestId("event-card");
        System.out.println(eventCards.count());

        // Visibility of the card which we have added
        Locator targetCard = eventCards.filter(new Locator.FilterOptions().setHasText("QA Summit Rahul Shetty 1"));
        assertThat(targetCard).isVisible(new LocatorAssertions.IsVisibleOptions().setTimeout(10000));
        String seatsText = targetCard.getByText("seats").innerText();
        System.out.println(seatsText);
        targetCard.getByTestId("book-now-btn").click();

        page.getByLabel("Full Namme").fill("Test Student");
        page.locator("#customer-email").fill("test.student@example.com");
        page.getByPlaceholder("+91 98765 43210").fill("9876543210");
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Book Now")).click();
        // #confirm-booking
        assertThat(page.getByText("Your tickets are reserved.")).isVisible();
        String bookingRef = page.locator(".booking-ref").innerText();
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("View My Bookings")).click();

        // Verify in Booking History
        Locator bookingCards = page.locator("#Booking-card");
        Locator targetBookingCard = bookingCards.filter(new Locator.FilterOptions().setHasText(bookingRef));
        assertThat(targetBookingCard).isVisible();
        // seat count reduction check
        page.locator("nav-events").click();

        Locator eventCardsAfterBooking = page.getByTestId("event-card");
        // Visibility of the card which we have added
        Locator targetCardAfterBooking = targetBookingCard.filter(new Locator.FilterOptions().setHasText("QA Summit Rahul Shetty 1"));
        String seatsTextAfterBooking = targetCardAfterBooking.getByText("seats").innerText();
        System.out.println(seatsTextAfterBooking);
        // AfterBooking < BeforeBookings


        // Close browser session   : #27
        browser.close();
        playwright.close();
    }
}
