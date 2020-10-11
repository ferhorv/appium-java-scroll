import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.MalformedURLException;
import java.net.URL;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.remote.AndroidMobileCapabilityType;
import io.appium.java_client.remote.MobileCapabilityType;

public class AndroidSampleTest {
    protected AndroidDriver<WebElement> driver;
    protected WebDriverWait wait;

    @BeforeEach
    public void setUp() throws MalformedURLException {
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability(CapabilityType.PLATFORM_NAME, Platform.ANDROID);
        caps.setCapability(AndroidMobileCapabilityType.APP_PACKAGE, "com.google.android.calculator");
        caps.setCapability(AndroidMobileCapabilityType.APP_ACTIVITY, "com.android.calculator2.Calculator");
        caps.setCapability(MobileCapabilityType.FULL_RESET, false);
        caps.setCapability(MobileCapabilityType.NO_RESET, true);
        driver = new AndroidDriver<>(new URL("http://localhost:4723/wd/hub"), caps);
        wait = new WebDriverWait(driver, 10);
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void sampleAndroid() {
        LogEntries serverLog = driver.manage().logs().get(LogType.SERVER);
        Assertions.assertThat(serverLog.getAll()).as("check serverLog size").isNotEmpty();
    }
}
