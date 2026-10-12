# Playwright-Testng-Project-Java

## Playwright Java + TestNG Test Automation

End-to-end UI test automation framework built with [Playwright for Java](https://playwright.dev/java/) and [TestNG](https://testng.org/), managed with Maven.

## Table of Contents

- [Features](#features)
- [Prerequisites](#prerequisites)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
- [Running Tests](#running-tests)
- [Configuration](#configuration)
- [Debugging](#debugging)
- [Troubleshooting](#troubleshooting)
- [Contributing](#contributing)
- [License](#license)

## Features

- Cross-browser testing on Chromium, Firefox, and WebKit
- Thread-safe setup using `ThreadLocal`, ready for parallel execution
- Isolated browser context per test (no shared cookies or state)
- Auto-waiting and auto-retrying assertions to reduce flaky tests
- TestNG suite configuration through `testng.xml`
- Optional tracing and screenshots for failure analysis

## Prerequisites

| Tool  | Version        |
|-------|----------------|
| JDK   | 11 or higher   |
| Maven | 3.8 or higher  |
| Git   | Any recent version |

Verify your installation:

```bash
java -version
mvn -version
```

## Project Structure

```
playwright-testng-project/
├── pom.xml
├── testng.xml
├── README.md
└── src/
    └── test/
        └── java/
            └── tests/
                ├── BaseTest.java
                └── ExampleTest.java
```

| File               | Purpose                                                   |
|--------------------|-----------------------------------------------------------|
| `pom.xml`          | Maven dependencies and Surefire plugin configuration      |
| `testng.xml`       | TestNG suite definition (classes, parallelism)            |
| `BaseTest.java`    | Browser lifecycle setup and teardown shared by all tests  |
| `ExampleTest.java` | Sample tests                                              |

## Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/<your-username>/<your-repo>.git
cd <your-repo>
```

### 2. Add dependencies

If you are starting from scratch, add the following to your `pom.xml`. Check [Maven Central](https://central.sonatype.com/artifact/com.microsoft.playwright/playwright) for the latest Playwright version.

```xml
<dependencies>
    <dependency>
        <groupId>com.microsoft.playwright</groupId>
        <artifactId>playwright</artifactId>
        <version>1.49.0</version>
    </dependency>
    <dependency>
        <groupId>org.testng</groupId>
        <artifactId>testng</artifactId>
        <version>7.10.2</version>
        <scope>test</scope>
    </dependency>
</dependencies>

<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-surefire-plugin</artifactId>
            <version>3.2.5</version>
            <configuration>
                <suiteXmlFiles>
                    <suiteXmlFile>testng.xml</suiteXmlFile>
                </suiteXmlFiles>
            </configuration>
        </plugin>
    </plugins>
</build>
```

### 3. Install browsers

Playwright downloads browsers automatically on first run. To install them ahead of time (recommended for CI):

```bash
mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install --with-deps"
```

## Writing Tests

### Base test class

`BaseTest` launches one browser per class and creates a fresh context and page for each test. `ThreadLocal` keeps parallel runs safe, since Playwright objects are not thread-safe.

```java
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
            new BrowserType.LaunchOptions().setHeadless(true)));
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
```

### Sample test

```java
package tests;

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
```

> **Tip:** Prefer Playwright's `assertThat` over TestNG's `assertEquals` for page and locator checks. It automatically retries until the condition is met or the timeout expires.

### Suite configuration

Create `testng.xml` in the project root:

```xml
<!DOCTYPE suite SYSTEM "https://testng.org/testng-1.0.dtd">
<suite name="Playwright Suite" parallel="classes" thread-count="3">
    <test name="UI Tests">
        <classes>
            <class name="tests.ExampleTest"/>
        </classes>
    </test>
</suite>
```

Remove the `parallel` and `thread-count` attributes to run tests sequentially.

## Running Tests

Run the whole suite:

```bash
mvn test
```

Run a single test class:

```bash
mvn test -Dtest=ExampleTest
```

Run a single test method:

```bash
mvn test -Dtest=ExampleTest#homePageHasTitle
```

You can also run tests from your IDE (IntelliJ IDEA or Eclipse) using the TestNG runner.

## Configuration

| Setting          | Where to change it                                            |
|------------------|---------------------------------------------------------------|
| Browser          | `BaseTest.launchBrowser()`: use `chromium()`, `firefox()`, or `webkit()` |
| Headless mode    | `.setHeadless(true/false)` in launch options                  |
| Parallelism      | `parallel` and `thread-count` in `testng.xml`                 |
| Viewport size    | `new Browser.NewContextOptions().setViewportSize(1920, 1080)` |
| Default timeout  | `page.setDefaultTimeout(10000)`                               |

### Running on a different browser

```java
browser.set(playwright.get().firefox().launch(
    new BrowserType.LaunchOptions().setHeadless(true)));
```

## Debugging

**Run with a visible browser and slow motion:**

```java
playwright.get().chromium().launch(
    new BrowserType.LaunchOptions().setHeadless(false).setSlowMo(500));
```

**Playwright Inspector:** set the `PWDEBUG` environment variable to step through a test.

```bash
# macOS / Linux
PWDEBUG=1 mvn test

# Windows (PowerShell)
$env:PWDEBUG=1; mvn test
```

**Record a trace and open it later:**

```java
// In @BeforeMethod, after creating the context
context.get().tracing().start(new Tracing.StartOptions()
    .setScreenshots(true).setSnapshots(true).setSources(true));

// In @AfterMethod, before closing the context
context.get().tracing().stop(new Tracing.StopOptions()
    .setPath(java.nio.file.Paths.get("traces/trace.zip")));
```

```bash
mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="show-trace traces/trace.zip"
```

**Generate test code by recording your actions:**

```bash
mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="codegen https://example.com"
```

## Troubleshooting

| Problem | Solution |
|---------|----------|
| `Executable doesn't exist` error | Browsers are not installed. Run the install command in [Install browsers](#3-install-browsers). |
| Tests hang or fail in CI | Make sure browsers are installed with `--with-deps`, and run in headless mode. |
| Flaky tests when running in parallel | Confirm every test uses its own context and that `ThreadLocal` is used for Playwright objects. |
| `NoClassDefFoundError` or version conflicts | Run `mvn clean install -U` to refresh dependencies. |
| Surefire runs no tests | Check that `testng.xml` is in the project root and the class names match your packages. |

## Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/my-test`
3. Commit your changes: `git commit -m "Add my test"`
4. Push to the branch: `git push origin feature/my-test`
5. Open a pull request

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.
