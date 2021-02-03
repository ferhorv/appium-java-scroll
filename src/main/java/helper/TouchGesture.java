package helper;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import org.openqa.selenium.By;
import org.openqa.selenium.Point;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.awt.geom.Point2D;
import java.time.Duration;
import java.util.Collections;
import java.util.Objects;

public class TouchGesture {
    public static Logger logger = LoggerFactory.getLogger(TouchGesture.class);

    public static double SWIPE_DURATION_MIN = 80;
    public static double SWIPE_DURATION_ON_DP = 0.30;
    private final AppiumDriver<MobileElement> driver;

    public TouchGesture(AppiumDriver<MobileElement> driver) {
        this.driver = driver;
    }

    /**
     * Swipe element to the given direction
     *
     * @param selector  selector of the element which should be swiped
     * @param direction 'up' or 'down'
     */
    public void swipeElementToDirection(By selector, String direction) {
        logger.debug(String.format("swiping element %s to the direction %s", selector, direction));
        Rectangle elementRect = driver.findElement(selector).getRect();
        swipeInRectToDirection(elementRect, direction);
    }

    void swipe(Duration duration, Point source, Point target) {
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence swipe = new Sequence(finger, 1);
        swipe.addAction(finger.createPointerMove(Duration.ofMillis(0),
                PointerInput.Origin.viewport(), source.x, source.y));
        swipe.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        swipe.addAction(finger.createPointerMove(duration,
                PointerInput.Origin.viewport(), target.x, target.y));
        swipe.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        driver.perform(Collections.singletonList(swipe));
    }

    void swipeInRectToDirection(Rectangle swipeArea, String direction) {
        Point source = getSwipeStartForDirection(direction, swipeArea);
        Point target = getSwipeEndForDirection(direction, source);
        Duration duration = getDurationForSwipe(source, target, false);
        swipe(duration, source, target);
        try {
            Thread.sleep(duration.toMillis() * 20);
        } catch (InterruptedException e) {
            logger.error("sleep after swipe gesture interrupted", e);
        }
    }

    private Point getSwipeEndForDirection(String direction, Point source) {
        switch (direction) {
            case "down":
                return new Point(source.getX(), driver.manage().window().getSize().getHeight());
            case "up":
                return new Point(source.getX(), 0);
            case "right":
                return new Point(driver.manage().window().getSize().getWidth(), source.getY());
            case "left":
                return new Point(0, source.getY());
            default:
                throw new IllegalArgumentException("unknown direction");
        }
    }

    private Point getSwipeStartForDirection(String direction, Rectangle swipeableElementRect) {
        switch (direction) {
            case "down":
                return new Point(getCenterOfRectangle(swipeableElementRect).getX(), swipeableElementRect.getY() + 1);
            case "up":
                return new Point(getCenterOfRectangle(swipeableElementRect).getX(), swipeableElementRect.getY() + swipeableElementRect.getHeight() - 1);
            case "right":
                return new Point(swipeableElementRect.getX() + 1, getCenterOfRectangle(swipeableElementRect).getY());
            case "left":
                return new Point(swipeableElementRect.getX() + swipeableElementRect.getWidth() - 1, getCenterOfRectangle(swipeableElementRect).getY());
            default:
                throw new IllegalArgumentException("unknown direction");
        }
    }

    private Point getCenterOfRectangle(Rectangle rect) {
        return new Point((rect.getX() + rect.getWidth() / 2), (rect.getY() + rect.getHeight() / 2));
    }

    private double getPlatformDependentDurationOnDp(boolean forAccurateScroll) {
        switch (Objects.requireNonNull(driver.getPlatformName())) {
            case "android":
                if (forAccurateScroll) {
                    return Scroll.SCROLL_DURATION_ON_DP_ANDROID;
                } else {
                    return SWIPE_DURATION_ON_DP;
                }
            case "ios":
                if (forAccurateScroll) {
                    return Scroll.SCROLL_DURATION_ON_DP_IOS;
                } else {
                    return SWIPE_DURATION_ON_DP;
                }
            default:
                throw new RuntimeException("unknown platform name");
        }
    }

    private double getPlatformDependentDistanceInDp(double distanceFromDriverCoordinates) {
        switch (Objects.requireNonNull(driver.getPlatformName())) {
            case "android":
                double pixelRatio;
                @Nullable Object value = driver.getSessionDetail("pixelRatio");
                if (value != null) {
                    pixelRatio = (double) value;
                } else {
                    throw new RuntimeException("pixelRatio of device unknown");
                }
                return distanceFromDriverCoordinates / pixelRatio;
            case "ios":
                return distanceFromDriverCoordinates;
            default:
                throw new RuntimeException("unknown platform name");
        }
    }

    Duration getDurationForSwipe(Point source, Point target, boolean forAccurateScroll) {
        double distanceFromDriverCoordinates = Point2D.distance(source.getX(), source.getY(), target.getX(), target.getY());
        double distanceInDp = getPlatformDependentDistanceInDp(distanceFromDriverCoordinates);
        double durationOnDp = getPlatformDependentDurationOnDp(forAccurateScroll);
        return Duration.ofMillis((long) Math.max(SWIPE_DURATION_MIN, Math.floor(distanceInDp * durationOnDp)));
    }
}
