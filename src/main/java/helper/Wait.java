package helper;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

public class Wait {
    public static Logger logger = LoggerFactory.getLogger(Wait.class);

    private final AppiumDriver<MobileElement> driver;

    public Wait(AppiumDriver<MobileElement> driver) {
        this.driver = driver;
    }

    public boolean isPresentAfter(By locator, Duration duration) {
        try {
            return new WebDriverWait(driver, duration.toSeconds()).until(ExpectedConditions.presenceOfElementLocated(locator)) != null;
        } catch (TimeoutException e) {
            logger.debug("element {} was not present", locator, e);
            return false;
        }
    }

    public boolean isVisibleAfter(By locator, Duration duration) {
        try {
            return new WebDriverWait(driver, duration.toSeconds()).until(ExpectedConditions.visibilityOfElementLocated(locator)) != null;
        } catch (TimeoutException e) {
            logger.debug("element {} was not visible", locator, e);
            return false;
        }
    }

    public WebElement waitFor(By locator, Duration duration) {
        return new WebDriverWait(driver, duration.toMillis()).until(ExpectedConditions.presenceOfElementLocated(locator));
    }
}
