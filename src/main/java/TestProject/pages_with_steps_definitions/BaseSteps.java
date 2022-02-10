package TestProject.pages_with_steps_definitions;

import TestProject.utils.EnviromentUtils;

import net.thucydides.core.ThucydidesSystemProperty;

import net.thucydides.core.pages.Pages;
import net.thucydides.core.steps.ScenarioSteps;


public class BaseSteps extends ScenarioSteps {
    final public static String currentPageKey = "current_page";

    public BaseSteps() {
        super();
    }

    public BaseSteps(final Pages pages) {
        super(pages);
    }


    public void openBrowserInMaximiseMode() {
        getDriver().manage().window().maximize();
    }


    public void setBaseUrlValue(String newBaseUrl) {
        pages().getConfiguration().getEnvironmentVariables().setProperty(ThucydidesSystemProperty.WEBDRIVER_BASE_URL.getPropertyName(), newBaseUrl);
    }

    public String getUrlValueFromParams(){
        String res = "";
        try {
            res = EnviromentUtils.getValueByXMLKeyFromTestResources("BaseUrl");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return res;
    }

}
