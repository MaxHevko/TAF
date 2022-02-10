package TestProject.utils.element;

import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.core.pages.WebElementFacadeImpl;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.pagefactory.ElementLocator;

import java.util.concurrent.TimeUnit;

public class WebElementFacadeImplementation extends WebElementFacadeImpl implements IWebElementFacade {
    private final WebDriver driver;

    public WebElementFacadeImplementation(WebDriver driver, ElementLocator locator, WebElement webElement, long implicitTimeoutInMilliseconds, long waitForTimeoutInMilliseconds) {
        super(driver, locator, webElement, implicitTimeoutInMilliseconds, waitForTimeoutInMilliseconds);
        this.driver = driver;
    }

    @Override
    public <T extends WebElementFacade> T setExplicitTimeout(final int timeout, final TimeUnit unit) {
        long implicitTimeoutInMilliseconds = getImplicitTimeoutInMilliseconds();
        long explicitTimeoutInMilliseconds = TimeUnit.MILLISECONDS.convert(timeout, unit);

        return wrapWebElement(driver, this, implicitTimeoutInMilliseconds, explicitTimeoutInMilliseconds);
    }
}
