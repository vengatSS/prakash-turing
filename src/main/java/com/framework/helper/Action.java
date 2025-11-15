package com.framework.helper;

import com.framework.hooks.DriverFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class Action {
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
}
