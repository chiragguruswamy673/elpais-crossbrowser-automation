import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.Assert;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class BrowserStackParallelTest {
    public static final String USERNAME = "chiragguruswamy_kwVPNC";
    public static final String ACCESS_KEY = "gSGcgvyxzem8ap5eFRod";
    public static final String HUB_URL = "https://" + USERNAME + ":" + ACCESS_KEY + "@hub-cloud.browserstack.com/wd/hub";

    WebDriver driver;

    @Parameters({"browser", "browserVersion", "os", "osVersion", "deviceName"})
    @Test
    public void runTest(
            @Optional("Chrome") String browser,
            @Optional("latest") String browserVersion,
            @Optional("") String os,
            @Optional("") String osVersion,
            @Optional("") String deviceName
    ) throws Exception {
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability("browserName", browser);
        caps.setCapability("browserVersion", browserVersion);

        Map<String, Object> bstackOptions = new HashMap<>();
        if (!os.isEmpty()) bstackOptions.put("os", os);
        if (!osVersion.isEmpty()) bstackOptions.put("osVersion", osVersion);
        if (!deviceName.isEmpty()) bstackOptions.put("deviceName", deviceName);

        bstackOptions.put("buildName", "ElPais Translation Build");
        bstackOptions.put("sessionName", browser + " Test");

        caps.setCapability("bstack:options", bstackOptions);

        driver = new RemoteWebDriver(new URL(HUB_URL), caps);
        driver.get("https://elpais.com/opinion/");
        String title = driver.getTitle();

        System.out.println("[" + browser + "] Title: " + title);

        // ✅ Assertion to report pass/fail
        Assert.assertTrue(title.toLowerCase().contains("opinión"), "Page title does not contain 'Opinión'");

        driver.quit();
    }
}