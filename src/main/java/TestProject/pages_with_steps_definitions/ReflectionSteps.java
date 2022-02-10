package TestProject.pages_with_steps_definitions;

import TestProject.utils.driver.DriverUtils;
import TestProject.utils.element.IWebElementFacade;
import TestProject.utils.reflection.Page;
import TestProject.utils.reflection.annotations.*;
import net.serenitybdd.core.Serenity;

import net.thucydides.core.annotations.Step;
import net.thucydides.core.pages.Pages;


import static java.util.concurrent.TimeUnit.SECONDS;

public class ReflectionSteps extends BaseSteps {

    private static final int DEFAULT_ELEMENT_LOOKUP_TIMEOUT = 10;

    public ReflectionSteps(Pages pages) {
        super(pages);
    }


    @Step
    public void switchToNewWindow() {
        for (String winHandle : getDriver().getWindowHandles()) {
            getDriver().switchTo().window(winHandle);
        }
    }

    @Step
    public BasePage openPage(String pageName) {
        BasePage page = Page.get(pageName, getDriver());
        page.open();
        Serenity.setSessionVariable(currentPageKey).to(page);

        return page;
    }

    @Step
    public void clickOnTheButtonOrLink(String buttonLinkName) {
        clickOnControl(buttonLinkName, ButtonLink.class);
    }

    @Step
    public void selectCheckBox(String checkBoxName, boolean isToSelect) {
        BasePage page = Serenity.sessionVariableCalled(currentPageKey);
        String selector = Page.getSelector(page, checkBoxName);
        DriverUtils.waitForPageLoad(getDriver());
        IWebElementFacade elem = page.element(selector, DEFAULT_ELEMENT_LOOKUP_TIMEOUT, SECONDS);
        if ((!elem.isSelected() && isToSelect) || (elem.isSelected() && !isToSelect)) {
            page.retryingFindClick(selector);
        }
    }


    private void clickOnControl(String controlName, Class controlType) {
        BasePage page = Serenity.sessionVariableCalled(currentPageKey);
        String selector = Page.getSelector(page, controlName, controlType);
        DriverUtils.waitForPageLoad(getDriver());
        page.retryingFindClick(selector);
    }

    @Step
    public void verifyOpenedPage(String pageName) {
        BasePage page = Page.get(pageName, getDriver());
        Serenity.setSessionVariable(currentPageKey).to(page);
    }


    @Step
    public void selectValueFromDropDown(String value, String dropdownName) {
        BasePage page = Serenity.sessionVariableCalled(currentPageKey);
        String selector = Page.getSelector(page, dropdownName);
        IWebElementFacade element = page.element(selector, DEFAULT_ELEMENT_LOOKUP_TIMEOUT, SECONDS);
        element
                .waitUntilClickable()
                .selectByVisibleText(value);
    }

    @Step
    public void clickOnTheButtonOrLinkAndVerifyPage(String buttonLinkName, String pageNameAfterClick) {
        clickOnTheButtonOrLink(buttonLinkName);
        verifyOpenedPage(pageNameAfterClick);
    }


    @Step
    public void openAndVerifyPage(String pageName) {
        String newBaseUrl = getUrlValueFromParams();
        setBaseUrlValue(newBaseUrl);

        BasePage basePage = openPage(pageName);
        basePage.verify();
    }


    @Deprecated
    @Step
    public IWebElementFacade getWebElementFromCurPageByName(String elementName) {
        BasePage page = Serenity.sessionVariableCalled(currentPageKey);
        String selector = null;
        selector = Page.getSelector(page, elementName);
        IWebElementFacade element = page.element(selector, DEFAULT_ELEMENT_LOOKUP_TIMEOUT, SECONDS);
        return element;
    }

}

