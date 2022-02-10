package TestProject.pages_with_steps_definitions;

import net.thucydides.core.annotations.Steps;
import org.jbehave.core.annotations.BeforeStory;


public class BaseDefinitions {
    @Steps
    BaseSteps baseSteps;

    @BeforeStory
    public void setUp() {
        baseSteps.openBrowserInMaximiseMode();
    }

}
