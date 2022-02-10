package TestProject.pages_with_steps_definitions;

import TestProject.utils.driver.DriverUtils;
import ch.lambdaj.function.convert.Converter;
import TestProject.utils.element.IWebElementFacade;
import TestProject.utils.element.WebElementFacadeImplementation;
import TestProject.utils.reflection.Page;
import com.google.common.base.Stopwatch;
import net.serenitybdd.core.exceptions.SerenityManagedException;
import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.core.pages.RenderedPageObjectView;
import net.thucydides.core.annotations.NotImplementedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Duration;

import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static net.serenitybdd.core.selectors.Selectors.xpathOrCssSelector;

public class BasePage extends PageObject {
    private static final Logger logger = LogManager.getLogger();





    public IWebElementFacade $(String xpathOrCssSelector, long timeout, TimeUnit unit) {

        return element(xpathOrCssSelector, timeout, unit);
    }

    public <T extends IWebElementFacade> T element(String xpathOrCssSelector, long timeout, TimeUnit unit) {

        return element(xpathOrCssSelector(xpathOrCssSelector), timeout, unit);
    }

    public <T extends IWebElementFacade> T element(By bySelector, long timeout, TimeUnit unit) {
        WebElement elem = getElementAndWaitUntilFound(bySelector, timeout, unit);

        return (T) new WebElementFacadeImplementation(
                getDriver(),
                null,
                elem,
                getImplicitWaitTimeout().in(MILLISECONDS),
                TimeUnit.MILLISECONDS.convert(timeout, unit));
    }

    protected BasePage(WebDriver driver) {
        super(driver);
    }

    protected BasePage(WebDriver driver, TimeUnit unit, final int ajaxTimeout) {
        super(driver, (int) TimeUnit.MILLISECONDS.convert(ajaxTimeout, unit));
    }


    public void verify() {
        DriverUtils.waitForPageLoad(getDriver());
        Class pageType = this.getClass();
        String expectedPageUrlFromAnnotation = Page.getUrlAnnotationValue(pageType);
        expectedPageUrlFromAnnotation = updateUrlWithBaseUrlIfDefined(expectedPageUrlFromAnnotation);
        String expectedPageNameFromAnnotation = Page.getPageNameAnnotationValue(pageType);

        if (expectedPageUrlFromAnnotation.isEmpty()) {
            throw new NotImplementedException(String.format("Annotation 'DefaultUrl' has not been implemented for '%s' page object", pageType));
        }

        if (expectedPageNameFromAnnotation.isEmpty()) {
            throw new NotImplementedException(String.format("Annotation 'PageName' has not been implemented for '%s' page object", pageType));
        }

        String currentUrl = getDriver().getCurrentUrl();
        String currentPageTitle = getDriver().getTitle();

        if (!currentUrl.startsWith(expectedPageUrlFromAnnotation)) {
            String message = String.format(
                    "Page url expected to start from '%s', but actual url: '%s'."
                    , expectedPageUrlFromAnnotation, currentUrl);
            throw new NotFoundException(message);
        }

        if (!currentPageTitle.contains(expectedPageNameFromAnnotation)) {
            String message = String.format(
                    "Page title expected to contain '%s', but actual title: '%s'. "
                    , expectedPageNameFromAnnotation, currentPageTitle);
            throw new NotFoundException(message);
        }
    }

    private WebElement getElementAndWaitUntilFound(By selector, long timeout, TimeUnit unit) {
        timeout = TimeUnit.MILLISECONDS.convert(timeout, unit);
        Stopwatch stopwatch = Stopwatch.createStarted();

        while (stopwatch.elapsed(MILLISECONDS) < timeout) {
            try {
                WebElement element = getDriver().findElement(selector);
                logger.debug(String.format("Element has been found after: `%d` milliseconds with selector: '%s'", stopwatch.elapsed(MILLISECONDS), selector));
                return element;
            } catch (NoSuchElementException | SerenityManagedException | ElementNotVisibleException ignored) {
            }
            ;
        }

        throw new NoSuchElementException(String.format("Element by `%s` selector was not found after: `%d` milliseconds", selector, timeout));
    }

    public void retryingFindClick(String selector) {
        retryingFindAction(selector, null, 10, SECONDS, WebElementActions.CLICK);
    }

    private void retryingFindAction(String selector, String text, long timeout, TimeUnit unit, WebElementActions action) {
        long timeoutInMs = TimeUnit.MILLISECONDS.convert(timeout, unit);
        Stopwatch stopwatch = Stopwatch.createStarted();
        while (stopwatch.elapsed(MILLISECONDS) < timeoutInMs) {
            try {
                IWebElementFacade element = $(selector, timeoutInMs, MILLISECONDS);
                element.waitUntilClickable();
                switch (action) {
                    case TYPE:
                        element.type(text);
                        break;
                    case CLICK:
                        element.click();
                        break;
                }

                return;
            } catch (NoSuchElementException ex) {
                throw new NoSuchElementException(String.format("Element by `%s` selector was not found after: `%d` milliseconds. %s", selector, timeout, ex));
            } catch (Exception ignored) {
            }
        }
    }

    private enum WebElementActions {
        TYPE,
        CLICK
    }
}