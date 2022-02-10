package TestProject.pages_with_steps_definitions;

import TestProject.utils.reflection.annotations.*;
import net.serenitybdd.core.annotations.findby.FindBy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.concurrent.TimeUnit;


@PageName("AutoRIA Home Page")
public class AutoRIAHomePage extends BasePage {
    public AutoRIAHomePage(WebDriver driver) {
        super(driver);
    }

    @ButtonLink(Controls.LOGIN)
    private final String loginBtn = "//*[contains(@href, 'cabinet')]/span";

    @FindBy(xpath = "//*[contains(@class, 'areabar')]")
    private WebElement sell;

    @Override
    public void verify() {
        $(sell).withTimeoutOf(15, TimeUnit.SECONDS).shouldBeVisible();
    }

    public static class Controls {
        public static final String LOGIN = "Login";
    }
}
