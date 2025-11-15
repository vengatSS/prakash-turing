package runner;


import io.cucumber.testng.*;
import org.testng.annotations.*;
import com.framework.Logger.Log;
import org.apache.logging.log4j.Logger;
import com.aventstack.extentreports.service.ExtentService;
import com.framework.ResultAttachment.Zipper;

@CucumberOptions(
        features = "src/test/resources/features",
        glue = {"com.framework.steps", "com.framework.hooks"},
        plugin = {
                "pretty",
                "json:test-output/Default/CucumberReport.json",
                "html:test-output/Default/CucumberHtmlReport.html",
                "com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:"
        }
        ,
        tags ="@EndToEnd-28"
)

public class TestRunner extends AbstractTestNGCucumberTests {
    private static final Logger logger = Log.getLogger(TestRunner.class);

    @Override
    @DataProvider(parallel = false)
    public Object[][] scenarios() {
        return super.scenarios();
    }



    @BeforeSuite
    public void systeminfo()
    {
        ExtentService.getInstance().setSystemInfo("OS", System.getProperty("os.name"));
        ExtentService.getInstance().setSystemInfo("Browser", System.getProperty("browser", "chrome").toUpperCase());
        ExtentService.getInstance().setSystemInfo("Java Version", System.getProperty("java.version"));
    }
    @AfterSuite
    public void runZipper() {

        try {
            logger.info("Running Zipper...");
            Zipper.main(new String[]{});  // Run the Zipper class to zip the test-output folder
        } catch (Exception e) {
            logger.info("Error running Zipper:{} ",e.getMessage());
        }
    }
}
