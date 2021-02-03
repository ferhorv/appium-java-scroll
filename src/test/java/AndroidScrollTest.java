import helper.Scroll;
import helper.ScrollParameter;
import helper.TouchGesture;
import helper.Wait;
import io.appium.java_client.MobileBy;
import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.remote.AndroidMobileCapabilityType;
import io.appium.java_client.remote.MobileCapabilityType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Platform;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

public class AndroidScrollTest {
    private AndroidDriver<MobileElement> driver;
    private Wait wait;
    private TouchGesture touchGesture;
    private Scroll scroll;
    private final By listDemoListItem = MobileBy.AccessibilityId("List Demo");
    private final By stratocumulusListItem = MobileBy.AccessibilityId("Stratocumulus");
    private final By cirrusListItem = MobileBy.AccessibilityId("Cirrus");
    private final By scrollView = MobileBy.className("android.widget.ScrollView");

    @BeforeEach
    public void setUp() throws MalformedURLException {
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability(CapabilityType.PLATFORM_NAME, Platform.ANDROID);
        caps.setCapability(MobileCapabilityType.APP, "https://github.com/cloudgrey-io/the-app/releases/download/v1.10.0/TheApp-v1.10.0.apk");
        caps.setCapability(AndroidMobileCapabilityType.APP_PACKAGE, "io.cloudgrey.the_app");
        caps.setCapability(AndroidMobileCapabilityType.APP_ACTIVITY, "io.cloudgrey.the_app.MainActivity");
        caps.setCapability(MobileCapabilityType.FULL_RESET, false);
        caps.setCapability(MobileCapabilityType.NO_RESET, true);
        driver = new AndroidDriver<>(new URL("http://localhost:4723/wd/hub"), caps);
        wait = new Wait(driver);
        touchGesture = new TouchGesture(driver);
        scroll = new Scroll(driver, wait, touchGesture);
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void scrollToElementBasicTest() {
        wait.waitFor(listDemoListItem, Duration.ofSeconds(10)).click();
        assert !wait.isPresentAfter(stratocumulusListItem, Duration.ofSeconds(5));
        assert scroll.toElement(new ScrollParameter.Builder().scrollView(scrollView).element(stratocumulusListItem).build());
    }

    @Test
    public void scrollToElementOnSideTest() {
        wait.waitFor(listDemoListItem, Duration.ofSeconds(10)).click();
        assert !wait.isPresentAfter(stratocumulusListItem, Duration.ofSeconds(5));
        assert scroll.toElement(new ScrollParameter.Builder().xPercent(0.05f).scrollView(scrollView).element(stratocumulusListItem).build());
    }

    @Test
    public void scrollToEndOfScrollViewTest() {
        wait.waitFor(listDemoListItem, Duration.ofSeconds(10)).click();
        wait.waitFor(scrollView, Duration.ofSeconds(10));
        scroll.toEndInScrollView(new ScrollParameter.Builder().scrollView(scrollView).build());
    }
    @Test
    public void swipeElementTest() {
        wait.waitFor(listDemoListItem, Duration.ofSeconds(10)).click();
        wait.waitFor(scrollView, Duration.ofSeconds(10));
        touchGesture.swipeElementToDirection(cirrusListItem, "left");
    }
}
