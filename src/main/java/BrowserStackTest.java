import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class BrowserStackTest {
    public static final String USERNAME = "chiragguruswamy_kwVPNC";
    public static final String ACCESS_KEY = "gSGcgvyxzem8ap5eFRod";
    public static final String URL = "https://" + USERNAME + ":" + ACCESS_KEY + "@hub-cloud.browserstack.com/wd/hub";

    public static void main(String[] args) throws Exception {
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability("browserName", "Chrome");
        caps.setCapability("browserVersion", "latest");

        // BrowserStack-specific options must go inside bstack:options
        Map<String, Object> bstackOptions = new HashMap<>();
        bstackOptions.put("os", "Windows");
        bstackOptions.put("osVersion", "11");
        bstackOptions.put("buildName", "ElPais Translation Build");
        bstackOptions.put("sessionName", "Opinion Section Test");

        caps.setCapability("bstack:options", bstackOptions);

        WebDriver driver = new RemoteWebDriver(new URL(URL), caps);

        driver.get("https://elpais.com/opinion/");
        System.out.println("Title: " + driver.getTitle());

        driver.quit();
    }
}