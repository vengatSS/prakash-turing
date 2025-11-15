package com.framework.helper;

import com.framework.hooks.DriverFactory;
import org.openqa.selenium.*;

public class Actions {

    public static void click(By locator) {
        WebDriver driver = DriverFactory.getDriver();

        int attempts = 0;
        while (attempts < 3) {
            try {
                Waiter.waitForElementToBeClickable(locator);
                WebElement element = driver.findElement(locator);
                click(element);
                return;
            } catch (StaleElementReferenceException e) {
                attempts++;
                if (attempts == 3) {
                    throw e;
                }
            }
        }
    }

    public static void click(WebElement element){
        Waiter.waitForElementToBeClickable((By) element);
        element.click();
    }

    public static void forceClick(By locator) {
        WebElement element = DriverFactory.getDriver().findElement(locator);
        ((JavascriptExecutor) DriverFactory.getDriver()).executeScript("arguments[0].click();", element);
    }

    public static void launchUrl(String url) {
        DriverFactory.getDriver().get(url);
    }

    public static void type(By locator, String text) {
        Waiter.waitForPresenceOfElement(locator);
        WebDriver driver = DriverFactory.getDriver();
        driver.findElement(locator).clear();
        driver.findElement(locator).sendKeys(text);
    }
}
