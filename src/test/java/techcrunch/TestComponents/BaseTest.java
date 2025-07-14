package techcrunch.TestComponents;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class BaseTest {

    protected WebDriver driver;
    protected ConfigReader configReader;

    @BeforeSuite
    public void setup() {
        configReader = new ConfigReader();

        String browserName = configReader.getBrowser().toLowerCase();
        String baseUrl = configReader.getBaseUrl();
        boolean isHeadless = configReader.getHeadless();

        if (browserName.equals("chrome")) {
            WebDriverManager.chromedriver().setup();
            ChromeOptions options = new ChromeOptions();
            if (isHeadless) {
                options.addArguments("--headless=new");
                options.addArguments("--disable-gpu");
                options.addArguments("--window-size=1920,1080");
            }
            driver = new ChromeDriver(options);
        } else if (browserName.equals("firefox")) {
            WebDriverManager.firefoxdriver().setup();
            FirefoxOptions options = new FirefoxOptions();
            if (isHeadless) {
                options.addArguments("-headless");
                options.addArguments("--window-size=1920,1080");
            }
            driver = new FirefoxDriver(options);
        } else {
            throw new IllegalArgumentException("Unsupported browser: " + browserName + ". Please specify 'chrome' or 'firefox' in config.properties.");
        }

        if (!isHeadless) {
            driver.manage().window().maximize();
        }

        driver.get(baseUrl);
        saveAllureEnvironmentInfo(isHeadless);
    }

    @AfterSuite
    public void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }

    private void saveAllureEnvironmentInfo(boolean isHeadless) {
        try {
            Properties props = new Properties();

            if (driver instanceof RemoteWebDriver) {
                RemoteWebDriver remoteDriver = (RemoteWebDriver) driver;
                props.setProperty("Browser", remoteDriver.getCapabilities().getBrowserName());
                props.setProperty("Browser Version", remoteDriver.getCapabilities().getBrowserVersion());
            } else {
                props.setProperty("Browser", "Unknown - Could not retrieve capabilities");
                props.setProperty("Browser Version", "Unknown");
                System.err.println("Warning: WebDriver is not an instance of RemoteWebDriver, browser capabilities could not be retrieved.");
            }

            props.setProperty("Headless Mode", String.valueOf(isHeadless));

            props.setProperty("Operating System", System.getProperty("os.name") + " " + System.getProperty("os.version"));
            props.setProperty("Base URL", configReader.getBaseUrl());
            props.setProperty("Java Version", System.getProperty("java.version"));

            String allureResultsDirPath = System.getProperty("user.dir") + "/target/allure-results/";
            File allureResultsDir = new File(allureResultsDirPath);

            if (!allureResultsDir.exists()) {
                boolean created = allureResultsDir.mkdirs();
                if (created) {
                    System.out.println("Allure results directory created: " + allureResultsDirPath);
                } else {
                    System.err.println("Failed to create Allure results directory: " + allureResultsDirPath);
                    return;
                }
            }

            String allureEnvFilePath = allureResultsDirPath + "environment.properties";
            FileOutputStream fos = new FileOutputStream(allureEnvFilePath);
            props.store(fos, "Allure Environment Information");
            fos.close();
            System.out.println("Allure environment.properties file created: " + allureEnvFilePath);

        } catch (IOException e) {
            System.err.println("Error creating Allure environment.properties file: " + e.getMessage());
        }
    }
}