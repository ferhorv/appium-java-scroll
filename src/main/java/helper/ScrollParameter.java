package helper;

import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScrollParameter {
    public static Logger logger = LoggerFactory.getLogger(ScrollParameter.class);

    public static final String DEFAULT_SCROLL_STRATEGY = "down";
    public static final float DEFAULT_X_PERCENT = 0.5f;
    public static final int DEFAULT_MAX_ROUNDS = 1;

    private final By scrollView;
    private final By element;
    private final String strategy;
    private final float xPercent;
    private final int maxRounds;

    public static class Builder {

        private By scrollView;
        private By element;
        private String strategy;
        private float xPercent;
        private int maxRounds;

        public Builder() {
            strategy(DEFAULT_SCROLL_STRATEGY).xPercent(DEFAULT_X_PERCENT).maxRounds(DEFAULT_MAX_ROUNDS);
        }

        public Builder scrollView(By scrollView) {
            this.scrollView = scrollView;
            return Builder.this;
        }

        public Builder element(By element) {
            this.element = element;
            return Builder.this;
        }

        public Builder strategy(String strategy) {
            this.strategy = strategy;
            return Builder.this;
        }

        public Builder xPercent(float xPercent) {
            this.xPercent = xPercent;
            return Builder.this;
        }

        public Builder maxRounds(int maxRounds) {
            this.maxRounds = maxRounds;
            return Builder.this;
        }

        public ScrollParameter build() {
            ScrollParameter scrollParameter = new ScrollParameter(this);
            logger.debug("created scrollParameter: " + scrollParameter);
            return scrollParameter;
        }
    }

    private ScrollParameter(Builder builder) {
        this.scrollView = builder.scrollView;
        this.element = builder.element;
        this.strategy = builder.strategy;
        this.xPercent = builder.xPercent;
        this.maxRounds = builder.maxRounds;
    }

    public By getScrollView() {
        return scrollView;
    }

    public By getElement() {
        return element;
    }

    public String getStrategy() {
        return strategy;
    }

    public float getxPercent() {
        return xPercent;
    }

    public int getMaxRounds() {
        return maxRounds;
    }

    @Override
    public String toString() {
        return String.format("helper.ScrollParameter [scrollView=%s, element=%s, strategy=%s, xPercent=%s, maxRounds=%s]",
                scrollView, element, strategy, xPercent, maxRounds);
    }
}

