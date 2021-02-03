package helper;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileBy;
import io.appium.java_client.MobileElement;
import io.appium.java_client.Setting;
import org.openqa.selenium.By;
import org.openqa.selenium.Point;
import org.openqa.selenium.Rectangle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Objects;

public class Scroll {
    public static Logger logger = LoggerFactory.getLogger(Scroll.class);

    public static double SCROLL_DURATION_ON_DP_ANDROID = 3;
    public static double SCROLL_DURATION_ON_DP_IOS = 3;

    public AppiumDriver<MobileElement> driver;
    protected Wait wait;
    protected TouchGesture touchGesture;

    public Scroll(AppiumDriver<MobileElement> driver, Wait wait, TouchGesture touchGesture) {
        this.driver = driver;
        this.wait = wait;
        this.touchGesture = touchGesture;
    }

    /**
     * helper.Scroll vertically in a scrollView
     *
     * @param scrollParameter object with builder pattern and default values for the scrolling - element is mandatory!
     * @return The returned value is a boolean one and equals to true if the element was found and fully scrolled into view and false if it was not found
     */
    public boolean toElement(ScrollParameter scrollParameter) {
        logger.debug("scrolling to element with parameter: " + scrollParameter);
        if (scrollParameter.getElement() == null) {
            throw new IllegalArgumentException("element parameter not set");
        }
        boolean canScrollMore = true;
        Rectangle scrollArea = getScrollArea(scrollParameter.getScrollView());
        for (int i = 0; i <= scrollParameter.getMaxRounds(); i++) {
            if (wait.isPresentAfter(scrollParameter.getElement(), Duration.ofSeconds(10))) {
                logger.debug("element is present in the DOM");
                Rectangle elementRect = driver.findElement(scrollParameter.getElement()).getRect();
                if (!isFullLengthScrollNeeded(scrollArea, elementRect, scrollParameter.getStrategy())) {
                    logger.debug("element was found and is at least partially displayed in the scroll view");
                    if (!isElementFullyScrolledIntoAreaByDirection(scrollArea, elementRect, scrollParameter.getStrategy())) {
                        logger.debug("performing adjustment scroll");
                        adjustScrollUntilElementIsFullyScrolledIntoArea(scrollArea, elementRect, scrollParameter.getStrategy());
                    }
                    logger.debug("element is fully displayed in the scroll view");
                    return true;
                } else {
                    logger.debug("[iOS] element is present in the DOM but is out of the scroll view");
                    if (!canScrollMore) {
                        logger.debug("we reached the end of the scroll view and the element was not found");
                        return false;
                    }
                    logger.debug("performing whole length scroll");
                    canScrollMore = scrollVerticallyInView(scrollParameter.getScrollView(), scrollArea, scrollParameter.getStrategy(), scrollParameter.getxPercent(), true);
                }
            } else {
                if (!canScrollMore) {
                    logger.debug("we reached the end of the scroll view and the element was not found");
                    return false;
                }
                canScrollMore = scrollVerticallyInView(scrollParameter.getScrollView(), scrollArea, scrollParameter.getStrategy(), scrollParameter.getxPercent(), true);
            }
        }
        return false;
    }

    /**
     * helper.Scroll vertically to the end of a scrollView
     *
     * @param scrollParameter object with builder pattern and default values for the scrolling
     */
    public void toEndInScrollView(ScrollParameter scrollParameter) {
        logger.debug("scrolling to end of the list with parameter: " + scrollParameter);
        Rectangle scrollViewRect = driver.findElement(scrollParameter.getScrollView()).getRect();
        for (int i = 0; i <= scrollParameter.getMaxRounds(); i++) {
            logger.debug("swiping in the element to the given direction and checking if we can scroll further");
            if (!scrollVerticallyInView(scrollParameter.getScrollView(), scrollViewRect, scrollParameter.getStrategy(), scrollParameter.getxPercent(), false)) {
                logger.debug("reached the end of the scroll view");
                return;
            }
        }
    }

    private Rectangle getScrollArea(By scrollView) {
        if (scrollView != null) {
            MobileElement scrollViewElement = driver.findElement(scrollView);
            return scrollViewElement.getRect();
        } else {
            return new Rectangle(0, 0, driver.manage().window().getSize().getWidth(), driver.manage().window().getSize().getHeight());
        }
    }

    private boolean isFullLengthScrollNeeded(Rectangle scrollArea, Rectangle elementRect, String direction) {
        switch (Objects.requireNonNull(driver.getPlatformName())) {
            case "android":
                return false;
            case "ios":
                return !isElementPartiallyScrolledIntoAreaByDirection(scrollArea, elementRect, direction);
            default:
                throw new RuntimeException("unknown platform name");
        }
    }

    private boolean scrollVerticallyInView(By scrollView, Rectangle scrollArea, String direction, float xPercent, boolean performAccurateScroll) {
        switch (Objects.requireNonNull(driver.getPlatformName())) {
            case "android":
                return androidScrollVerticallyInView(scrollArea, direction, xPercent, performAccurateScroll);
            case "ios":
                return iOSScrollVerticallyInView(scrollView, scrollArea, direction, xPercent, performAccurateScroll);
            default:
                throw new RuntimeException("unknown platform name");
        }
    }

    // It may not work in all cases. Sometimes incorrect AccessibilityEvent is sent from the Android views after scrolling.
    private boolean androidScrollVerticallyInView(Rectangle scrollArea, String direction, float xPercent, boolean performAccurateScroll) {
        driver.setSetting(Setting.IGNORE_UNIMPORTANT_VIEWS, true);
        String pageSourceBeforeScroll = driver.getPageSource();
        if (performAccurateScroll) {
            scrollVerticallyCommon(scrollArea, direction, xPercent);
        } else {
            touchGesture.swipeInRectToDirection(scrollArea, this.getSwipeDirectionForScrollDirection(direction));
        }
        String pageSourceAfterScroll = driver.getPageSource();
        driver.setSetting(Setting.IGNORE_UNIMPORTANT_VIEWS, false);
        return !pageSourceBeforeScroll.equals(pageSourceAfterScroll);
    }

    private boolean iOSScrollVerticallyInView(By scrollView, Rectangle scrollArea, String direction, float xPercent, boolean performAccurateScroll) {
        if (performAccurateScroll) {
            scrollVerticallyCommon(scrollArea, direction, xPercent);
        } else {
            touchGesture.swipeInRectToDirection(scrollArea, this.getSwipeDirectionForScrollDirection(direction));
        }
        return canScrollMoreIOS(scrollView, scrollArea, direction);
    }

    private void scrollVerticallyCommon(Rectangle scrollArea, String direction, float xPercent) {
        int x = Math.round(scrollArea.getX() + scrollArea.getWidth() * xPercent);
        Point source = adjustPointForAndroidScroll(new Point(x, getFarEdgeOfRectByDirection(scrollArea, direction).getY()), direction);
        Point target = new Point(x, getNearEdgeOfRectByDirection(scrollArea, direction).getY());
        Duration duration = touchGesture.getDurationForSwipe(source, target, true);
        touchGesture.swipe(duration, source, target);
    }

    private Point adjustPointForAndroidScroll(Point point, String direction) {
        if (direction.equals("down")) {
            return point.moveBy(0, -1);
        } else {
            return point.moveBy(0, 1);
        }
    }

    private void adjustScrollUntilElementIsFullyScrolledIntoArea(Rectangle scrollArea, Rectangle elementRect, String direction) {
        Point source = adjustPointForAndroidScroll(getNearEdgeOfRectByDirection(elementRect, direction), direction);
        Point target = getNearEdgeOfRectByDirection(scrollArea, direction); // for the scroll area the near edge is actually the far edge as it is not a moving element but we reuse this method
        Duration duration = touchGesture.getDurationForSwipe(source, target, true);
        touchGesture.swipe(duration, source, target);
    }

    private boolean canScrollMoreIOS(By scrollView, Rectangle scrollArea, String direction) {
        MobileElement scrollViewElement = driver.findElement(scrollView);
        Point edgeOfLastElement = getEdgeOfLastElementByDirection(scrollViewElement, direction);
        return !isPointInRect(scrollArea, edgeOfLastElement);
    }

    private boolean isPointInRect(Rectangle rect, Point point) {
        java.awt.Rectangle computableRect = new java.awt.Rectangle(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
        return computableRect.contains(point.getX(), point.getY());
    }

    private Point getEdgeOfLastElementByDirection(MobileElement scrollViewElement, String direction) {
        MobileElement edgeElement = getLastElementInListByDirection(scrollViewElement, direction);
        return getFarEdgeOfRectByDirection(edgeElement.getRect(), direction);
    }

    private MobileElement getLastElementInListByDirection(MobileElement scrollViewElement, String direction) {
        if (direction.equals("down")) {
            return scrollViewElement.findElement(MobileBy.xpath("descendant::*[last()]"));
        } else {
            return scrollViewElement.findElement(MobileBy.xpath("descendant::*[1]"));
        }
    }

    private String getSwipeDirectionForScrollDirection(String scrollDirection) {
        switch (scrollDirection) {
            case "down":
                return "up";
            case "up":
                return "down";
            case "right":
                return "left";
            case "left":
                return "right";
            default:
                throw new IllegalArgumentException("unknown direction");
        }
    }

    private boolean isElementFullyScrolledIntoAreaByDirection(Rectangle scrollArea, Rectangle elementRect, String direction) {
        Point elementEdge = getFarEdgeOfRectByDirection(elementRect, direction);
        return isPointInRect(scrollArea, elementEdge);
    }

    private boolean isElementPartiallyScrolledIntoAreaByDirection(Rectangle scrollArea, Rectangle elementRect, String direction) {
        Point elementEdge = getNearEdgeOfRectByDirection(elementRect, direction);
        return isPointInRect(scrollArea, elementEdge);
    }

    private Point getNearEdgeOfRectByDirection(Rectangle rect, String direction) {
        if (direction.equals("down")) {
            return new Point((rect.getX() + rect.getWidth() / 2), rect.getY());
        } else {
            return new Point(rect.getX(), rect.getY() + rect.getHeight());
        }
    }

    private Point getFarEdgeOfRectByDirection(Rectangle rect, String direction) {
        if (direction.equals("down")) {
            return new Point((rect.getX() + rect.getWidth() / 2), rect.getY() + rect.getHeight());
        } else {
            return new Point((rect.getX() + rect.getWidth() / 2), rect.getY());
        }
    }
}