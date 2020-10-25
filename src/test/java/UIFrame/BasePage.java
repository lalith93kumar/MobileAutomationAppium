package UIFrame;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileBy;
import io.appium.java_client.TouchAction;
import org.junit.Assert;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;
import static io.appium.java_client.touch.WaitOptions.waitOptions;
import static io.appium.java_client.touch.offset.PointOption.point;

public class BasePage {

    protected AppiumDriver driver;
    private WebDriverWait wait;
    private FluentWait<WebDriver> fluentlyWait;

    public BasePage(AppiumDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(this.driver, 40);
        fluentlyWait = new FluentWait<WebDriver>(this.driver)
                .withTimeout(Duration.ofSeconds(40).toMillis(), TimeUnit.SECONDS)
                .pollingEvery(Duration.ofSeconds(1).toMillis(), TimeUnit.SECONDS)
                .ignoring(StaleElementReferenceException.class);
    }

    public WebElement waitForElementToBeVisible(WebElement element) {
        return wait.until(ExpectedConditions.visibilityOf(element));
    }

    public Boolean waitForElementToBeInvisible(WebElement element) {
        try {
            return wait.until(ExpectedConditions.invisibilityOf(element));
        }
        catch (Exception e)
        {
            return false;
        }
    }

    public Boolean waitForElementToBeInvisible(WebElement element, int timeoutInSeconds) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, timeoutInSeconds);
            return wait.until(ExpectedConditions.invisibilityOf(element));
        }
        catch (Exception e)
        {
            return false;
        }
    }

    protected String getText(WebElement webElement) {
        waitForElementToBeVisible(webElement);
        return webElement.getText();
    }

    public WebElement waitForElementToBeVisible(WebElement element, int timeoutInSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, timeoutInSeconds);
        return wait.until(ExpectedConditions.visibilityOf(element));
    }

    public WebElement waitForElementToBeClickableIgnoringStaleElement(WebElement element) {
        return fluentlyWait.until(ExpectedConditions.elementToBeClickable(element));
    }

    public void waitForVisibilityOfAllElements(List<WebElement> webElementList) {
        wait.until(ExpectedConditions.visibilityOfAllElements(webElementList));
    }

    public void sendKeys(WebElement elem, String text) {
        waitForElementToBeVisible(elem);
        elem.click();
        if (text != null) {
            if (!elem.getText().isEmpty()) {
                elem.clear();
            }
            elem.sendKeys(text);
        } else {
            Assert.assertNotNull(elem.getText());
        }
        driver.getKeyboard();
        hideKeyboard();
    }

    public void hideKeyboard() {
        try {
            driver.hideKeyboard();
        } catch (WebDriverException e) {
            // ignore exception
        }
    }

    public WebElement scrollToText(String text) {
        try {
            return driver.findElement(MobileBy.AndroidUIAutomator(
                    "new UiScrollable(new UiSelector()" +
                            ".scrollable(true)" +
                            ".instance(0))" +
                            ".scrollIntoView(new UiSelector()" +
                            ".text(\"" + text + "\")" +
                            ".instance(0))"
            ));
        } catch (Exception e) {
            Assert.fail("Did not find element by text - " + text);
        }
        return null;
    }

    public WebElement scrollToResourceId(String resourceId) {
        try {
            return driver.findElement(MobileBy.AndroidUIAutomator(
                    "new UiScrollable(new UiSelector()" +
                            ".scrollable(true)" +
                            ".instance(0))" +
                            ".scrollIntoView(new UiSelector()" +
                            ".resourceId(\"" + resourceId + "\")" +
                            ".instance(0))"
            ));
        } catch (Exception e) {
            Assert.fail("Did not find element by resourceId - " + resourceId);
        }
        return null;
    }

    public void scrollDownToText(String text) {
        scrollDownTo(By.xpath("//*[@text=\"" + text + "\"]"));
    }

    public void scrollDownToTextAndTap(String text) {
        scrollDownToText(text);
        tapOnText(text);
    }

    public void scrollDownTo(By byOfElementToBeFound) {
        hideKeyboard();
        for (int i = 0; i < 40; i++) {
            if (driver.findElements(byOfElementToBeFound).size() > 0)

                return;
            scrollDown();
        }
        Assert.fail("Did not find : " + byOfElementToBeFound.toString());
    }

    public void scrollUpTo(By byOfElementToBeFound) {
        hideKeyboard();
        for (int i = 0; i < 40; i++) {
            if (driver.findElements(byOfElementToBeFound).size() > 0)
                return;

            scrollUp();
        }
        Assert.fail("Did not find : " + byOfElementToBeFound.toString());
    }

    public void scrollDown() {
        int height = driver.manage().window().getSize().getHeight();
        int width = driver.manage().window().getSize().getWidth();
        swipe(width / 2, height * 2 / 3, width / 2, height / 3, 1000);


    }

    public void scrollUp() {
        int height = driver.manage().window().getSize().getHeight();
        int width = driver.manage().window().getSize().getWidth();
        swipe(width / 2, height / 3, width / 2, height * 2 / 3, 1000);
    }

    public void swipe(int startX, int startY, int endX, int endY, int durationInMilliSeconds) {
        new TouchAction(driver)
                .press(point(startX, startY))
                .waitAction(waitOptions(Duration.ofMillis(durationInMilliSeconds)))
                .moveTo(point(endX, endY)).release().perform();
    }

    protected void swipeFromTo(WebElement startElement, WebElement stopElement) {
        swipeFromTo(startElement, stopElement, 1000);
    }

    protected void swipeFromTo(WebElement startElement, WebElement stopElement, int durationInMilliSeconds) {
        swipe(startElement.getLocation().getX(), startElement.getLocation().getY(), stopElement.getLocation().getX(), stopElement.getLocation().getY(), durationInMilliSeconds);
    }

    protected void swipeFromLeftToRight(WebElement webElement) {
        waitForElementToBeVisible(webElement);
        int xAxisStartPoint = webElement.getLocation().getX();
        int xAxisEndPoint = xAxisStartPoint + webElement.getSize().width;
        int yAxis = webElement.getLocation().getY() + webElement.getSize().getHeight() / 2;
        swipe(xAxisStartPoint, yAxis, xAxisEndPoint, yAxis, 1000);
    }

    public void tapOnText(String text) {
        waitForElementToBeVisible(By.xpath(String.format("//*[@text = '%s']", text)), 20);
        driver.findElement(By.xpath(String.format("//*[@text = '%s']", text))).click();
    }

    protected void swipeFromRightToLeft(WebElement webElement) {
        swipeFromRightToLeft(webElement, 1000);
    }

    protected void swipeFromRightToLeft(WebElement webElement, int durationInMilliSeconds) {
        waitForElementToBeVisible(webElement);
        int xAxisEndPoint = webElement.getLocation().getX();
        int xAxisStartPoint = xAxisEndPoint + webElement.getSize().getWidth();
        int yAxis = webElement.getLocation().getY() + webElement.getSize().getHeight() / 2;
        swipe(xAxisStartPoint, yAxis, xAxisEndPoint, yAxis, durationInMilliSeconds);
    }

    public WebElement waitForElementToBeVisible(By by, int timeoutInSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, timeoutInSeconds);
        return wait.until(ExpectedConditions.visibilityOfElementLocated(by));
    }

    protected void clickOn(WebElement webElement) {
        waitForElementToBeVisible(webElement);
        webElement.click();
    }

    protected boolean isDisplayed(WebElement webElement) {
        waitForElementToBeVisible(webElement);
        return webElement.isDisplayed();
    }
}
