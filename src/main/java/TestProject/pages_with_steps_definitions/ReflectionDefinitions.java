package TestProject.pages_with_steps_definitions;

import TestProject.utils.EnviromentUtils;
import net.thucydides.core.annotations.Steps;

import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;


public class ReflectionDefinitions {
    @Steps
    BaseSteps baseSteps;
    @Steps
    ReflectionSteps reflectionSteps;


    @Then("I switched to the New Tab")
    public void switchToNewWindow() {
        reflectionSteps.switchToNewWindow();
    }

    @Given("The '$pageName' page is opened")
    @When("I open the '$pageName' page")
    public void ThePageIsOpened(String pageName) {
        reflectionSteps.openAndVerifyPage(pageName);
    }

    @When("I click on the '$buttonLinkName' {link|button}")
    public void whenIClickOnTheButton(String buttonLinkName) {
        reflectionSteps.clickOnTheButtonOrLink(buttonLinkName);
    }

    @When("I click on the '$buttonLinkName' {link|button} and open '$pageName' page")
    public void whenIClickOnTheButtonOrLinkAndOpenAnotherPage(String buttonLinkName, String pageName) {
        reflectionSteps.clickOnTheButtonOrLinkAndVerifyPage(buttonLinkName, pageName);
    }

    @When("I select the '$checkBoxName' checkbox")
    public void whenISelectTheСheckBox(String checkBoxName) {
        reflectionSteps.selectCheckBox(checkBoxName, true);
    }

    @When("I select '$value' from the '$dropdownName' dropdown")
    public void whenISelectFromDropdown(String value, String dropdownName) throws Exception {
        value = EnviromentUtils.getEnvironmentDependentValue(value);

        reflectionSteps.selectValueFromDropDown(value, dropdownName);
    }

    @Then("The '$pageName' page is loaded")
    @Given("The '$pageName' page is loaded")
    public void thenThePageIsLoaded(String pageName) {
        reflectionSteps.verifyOpenedPage(pageName);
    }
}

