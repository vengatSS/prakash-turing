package com.framework.hooks;

import com.framework.Logger.Log;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.edge.EdgeOptions;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;


public class DriverFactory {
    private static final Logger logger = Log.getLogger(DriverFactory.class);
    private static final ThreadLocal<WebDriver> driver = new ThreadLocal<>();

    public void initializeDriver() {
        String browserName = System.getProperty("browser", "chrome").toLowerCase();
        String headless = System.getProperty("headless", "false").toLowerCase();

        WebDriver driverInstance = switch (browserName) {
            case "chrome" -> {
                ChromeOptions options = new ChromeOptions();
                options.setAcceptInsecureCerts(true);
                if (Boolean.parseBoolean(headless)) {
                    options.addArguments("--headless");


                }
                options.addArguments("--window-size=1920,1080");
                options.addArguments("--incognito");
                options.addArguments("--disable-gpu");
                options.addArguments("--no-sandbox");
                options.addArguments("--disable-dev-shm-usage");


                yield new ChromeDriver(options);
            }

            case "firefox" -> {
                FirefoxOptions options = new FirefoxOptions();
                options.setAcceptInsecureCerts(true);
                if ("true".equals(headless)) {
                    options.addArguments("--headless");
                }
                yield new FirefoxDriver(options);
            }

            case "edge" -> {
                EdgeOptions options = new EdgeOptions();
                options.setAcceptInsecureCerts(true);

                try {
                    Path userDataDirPath = Files.createTempDirectory("edge-profile-");
                    userDataDirPath.toFile().deleteOnExit();
                    options.addArguments("--user-data-dir=" + userDataDirPath.toAbsolutePath());
                } catch (IOException e) {
                    logger.error("Failed to create temporary Edge user data dir", e);
                    throw new RuntimeException(e);
                }

                if ("true".equals(headless)) {
                    options.addArguments("--headless");
                }

                yield new EdgeDriver(options);
            }

            case "safari" -> new SafariDriver(); // Safari does not support headless mode yet

            default -> {
                logger.info("Unsupported browser: {}", browserName);
                throw new RuntimeException("Unsupported browser: " + browserName);
            }
        };

        driver.set(driverInstance);
//        driverInstance.manage().window().maximize();
        getDriver();
    }

    public static WebDriver getDriver() {
        return driver.get();
    }

    public static void quitDriver() {
        if (driver.get() != null) {
            driver.get().quit();
            driver.remove();
        }
    }
}

