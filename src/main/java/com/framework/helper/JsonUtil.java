package com.framework.helper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.framework.hooks.DriverFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class JsonUtil {

    public static void writeMapToJson(Map<String, String> data, String fileName) {
        String filePath = "src/main/resources/TestData/" + fileName;
        ObjectMapper mapper = new ObjectMapper();

        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(filePath), data);
            System.out.println("✅ Data written to " + filePath);
        } catch (IOException e) {
            System.out.println("❌ Failed to write JSON file: " + e.getMessage());
        }
    }

    public static void editAndSaveFields() {
        WebDriver driver = DriverFactory.getDriver();
        JavascriptExecutor js = (JavascriptExecutor) driver;
        Map<String, String> editedValues = new LinkedHashMap<>();

        for (ArticleFormData field : fields) {

            if ("Date back from SCE".equalsIgnoreCase(field.getFieldName())) {
                System.out.println("⚠️ Skipped editing field: " + field.getFieldName());
                continue;
            }

            String editable = field.getEditable();
            if (!"yes".equalsIgnoreCase(editable) && !"n/a".equalsIgnoreCase(editable)) {
                continue;
            }

            try {
                By locator = getLocator(field);
                WebElement element = driver.findElement(locator);
                Waiter.waitForElementToBeVisible(locator);

                if (!element.isEnabled() || element.getAttribute("readonly") != null) {
                    System.out.println("⚠️ Skipped (not editable at runtime): " + field.getFieldName());
                    continue;
                }

                String newValue = generateRandomValueByType(field.getFieldName());
                String fieldType = field.getType().toLowerCase(); // get type from JSON
                String fieldName = field.getFieldName();

                switch (fieldType) {
                    case "text":
                        element.clear();
                        element.sendKeys(newValue);
                        break;

                    case "number":
                        int randomNum = new Random().nextInt(999);
                        element.clear();
                        element.sendKeys(String.valueOf(randomNum));
                        newValue = String.valueOf(randomNum);
                        break;

                    case "date":
                        try {
                            js.executeScript("arguments[0].scrollIntoView(true);", element);
                            Waiter.waitForSeconds(1);
                            js.executeScript("arguments[0].click();", element);
                            Waiter.waitForSeconds(1);
                            element.clear();

                            LocalDate today = LocalDate.now();
                            String formattedDate = today.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));

                            try {
                                Select monthSelect = new Select(driver.findElement(By.cssSelector(".react-datepicker__month-container select:nth-child(1)")));
                                monthSelect.selectByValue(String.valueOf(today.getMonthValue() - 1)); // 0-based months

                                Select yearSelect = new Select(driver.findElement(By.cssSelector(".react-datepicker__month-container select:nth-child(2)")));
                                yearSelect.selectByVisibleText(String.valueOf(today.getYear()));
                            } catch (Exception ignored) {}

                            String dayXpath = String.format(
                                    "//div[contains(@class,'react-datepicker__day') " +
                                            "and not(contains(@class,'react-datepicker__day--outside-month')) " +
                                            "and text()='%d']",
                                    today.getDayOfMonth()
                            );
                            driver.findElement(By.xpath(dayXpath)).click();

                            newValue = formattedDate;
                            System.out.println("✏️ Edited: " + field.getFieldName() + " → " + newValue);

                        } catch (Exception e) {
                            System.out.println("❌ Error editing date field: " + field.getFieldName() + " → " + e.getMessage());
                        }
                        break;



                    case "radio":
                        try {
                            // Identify radio group name
                            String groupName = element.getAttribute("name");

                            // Find all radios with same name
                            List<WebElement> radioGroup = driver.findElements(By.name(groupName));

                            // Find the currently selected value
                            WebElement selectedRadio = radioGroup.stream()
                                    .filter(WebElement::isSelected)
                                    .findFirst()
                                    .orElse(null);

                            String currentValue = (selectedRadio != null)
                                    ? selectedRadio.getAttribute("value")
                                    : "false"; // assume default false if none selected

                            // Toggle the value (if true, click false; if false, click true)
                            String targetValue = currentValue.equalsIgnoreCase("true") ? "false" : "true";

                            // Click the opposite radio option
                            WebElement targetRadio = radioGroup.stream()
                                    .filter(r -> targetValue.equalsIgnoreCase(r.getAttribute("value")))
                                    .findFirst()
                                    .orElse(null);

                            if (targetRadio != null && !targetRadio.isSelected()) {
                                js.executeScript("arguments[0].click();", targetRadio);
                                Waiter.waitForSeconds(1);
                            }

                            // Verify that it’s now selected
                            Waiter.waitUntil(() -> targetRadio.isSelected(), 5);

                            // Store the newly selected value ("true" or "false")
                            newValue = targetValue;

                            System.out.println("✏️ Edited Radio: " + field.getFieldName() + " → " + newValue);

                        } catch (Exception e) {
                            System.out.println("❌ Error editing radio field: " + field.getFieldName() + " → " + e.getMessage());
                        }
                        break;

                    case "checkbox":
                        boolean selected = element.isSelected();
                        if (!selected) {
                            js.executeScript("arguments[0].click();", element);
                            Waiter.waitForSeconds(1);
                            selected = element.isSelected();
                        }
                        newValue = selected ? "Selected Radio" : "Not Selected";
                        break;


                    case "textarea":
                        element.clear();
                        element.sendKeys(newValue);
                        break;

                    case "dropdown":
                        switch (fieldName) {
                            case "productionWorkflow":
                                String valuePW = randomProductionWorkflowOption();
                                selectDropdownFields(valuePW, "Production workflow");
                                newValue = valuePW;
                                break;
                            case "Cover sub received":
                                String valueCSR = randomCoverSubReceivedOption();
                                selectDropdownFields(valueCSR,"Cover sub received");
                                newValue = valueCSR;
                                break;

                        }

                        break;

                    case "custom-dropdown":
                        newValue = handleustomDropdown();
                        break;

                    default:
                        try {
                            js.executeScript("arguments[0].scrollIntoView(true); arguments[0].click();", element);
                            System.out.println("⚠️ Custom element clicked: " + field.getFieldName());
                        } catch (Exception e) {
                            System.out.println("❌ Failed to handle: " + field.getFieldName());
                        }
                }

                editedValues.put(field.getFieldName(), newValue);
                System.out.println("✏️ Edited: " + field.getFieldName() + " → " + newValue);

            } catch (Exception e) {
                System.out.println("❌ Error editing: " + field.getFieldName() + " → " + e.getMessage());
            }
        }

}
