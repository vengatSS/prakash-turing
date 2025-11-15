package com.framework.hooks;
import com.framework.Logger.Log;
import io.cucumber.java.*;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import java.io.File;

public class Hooks {
    private static final Logger logger = Log.getLogger(Hooks.class);

    @Before
    public void setUp() {
        new DriverFactory().initializeDriver();
        cleanupDynamicFiles();

    }

    @AfterStep
    public void afterEachStep(Scenario scenario) {
        if (scenario.isFailed()) {
//            AttachScreenshot("Failed Screenshot", scenario);
            attachScreenshotToCucumberReport(scenario);
        }
    }

    @After
    public void tearDown(Scenario scenario) {
//            AttachScreenshot("Final Screenshot", scenario);
        attachScreenshotToCucumberReport( scenario);
        DriverFactory.quitDriver();
    }

    private void attachScreenshotToCucumberReport(Scenario scenario) {
        try {
            byte[] screenshot = ((TakesScreenshot) DriverFactory.getDriver())
                    .getScreenshotAs(OutputType.BYTES);
            scenario.attach(screenshot, "image/png", "Screenshot");
            logger.info("Screenshot attached to Cucumber report");
        } catch (Exception e) {
            logger.error("Error attaching screenshot", e);
        }
    }

    private void cleanupDynamicFiles() {
        File folder = new File("src/main/resources/TestData/");

        File[] files = folder.listFiles((dir, name) -> (name.startsWith("User_") || name.startsWith("Journal_")) && name.endsWith(".json"));

        if (files != null) {
            for (File file : files) {
                if (file.exists()) {
                    boolean deleted = file.delete();
                    if (deleted) {
                        logger.info("Deleted dynamic file: " + file.getName());
                    } else {
                        logger.error("Failed to delete dynamic file: " + file.getName());
                    }
                }
            }
        }
    }


}


