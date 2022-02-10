package TestProject.utils.element;

import net.serenitybdd.core.pages.WebElementFacade;

import java.util.concurrent.TimeUnit;

public interface IWebElementFacade extends WebElementFacade {
    public <T extends WebElementFacade> T setExplicitTimeout(final int timeout, final TimeUnit unit);
}
