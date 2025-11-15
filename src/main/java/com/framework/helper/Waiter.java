package com.framework.helper;

import com.framework.hooks.DriverFactory;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.time.Duration;
import java.util.function.BooleanSupplier;

public class Waiter {

    private static final Duration TIMEOUT = Duration.ofSeconds(30);

    public static void waitForSeconds(int seconds) {
        try {
            Thread.sleep(seconds * 1000L); // Convert seconds to milliseconds
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Good practice to reset the interruption status
            throw new RuntimeException("Thread was interrupted while waiting", e);
        }
    }

    public static void waitForElementToBeVisible(By locator) {
        WebDriverWait wait = new WebDriverWait(DriverFactory.getDriver(), TIMEOUT);
        wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public static void waitForElementToBeInvisible(By locator) {
        WebDriverWait wait = new WebDriverWait(DriverFactory.getDriver(), TIMEOUT);
        wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    public static void waitForElementToBeClickable(By locator) {
        WebDriverWait wait = new WebDriverWait(DriverFactory.getDriver(), TIMEOUT);
        wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    public static void waitForElementToBeNotClickable(By locator) {
        WebDriver driver = DriverFactory.getDriver();
        WebDriverWait wait = new WebDriverWait(driver, TIMEOUT);
        wait.until(driver1 -> {
            try {
                WebElement element = driver1.findElement(locator);
                return !element.isEnabled() || !element.isDisplayed();
            } catch (NoSuchElementException | StaleElementReferenceException e) {
                return true;
            }
        });
    }

    public static void waitUntil(BooleanSupplier condition, int timeoutSeconds) {
        WebDriver driver = DriverFactory.getDriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        wait.until(d -> condition.getAsBoolean());
    }

    public static void waitForFileToExist(String filePath, long timeout) {
        File file = new File(filePath);
        long startTime = System.currentTimeMillis();

        while (!file.exists() && (System.currentTimeMillis() - startTime) < timeout) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        if (!file.exists()) {
            throw new RuntimeException("Timeout waiting for file: " + filePath);
        }
    }


}
