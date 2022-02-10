package TestProject.run;

import net.serenitybdd.jbehave.SerenityStories;
import org.jbehave.core.steps.InjectableStepsFactory;
import java.util.Arrays;
import java.util.List;

public class CustomSerenityStories extends SerenityStories {
    @Override
    public InjectableStepsFactory stepsFactory() {
        List<String> packages = Arrays.asList(System.getProperty("packages.to.run").split(","));

        return SerenityStepFactory.withStepsFromPackage(packages, configuration()).andClassLoader(getClassLoader());
    }
}
