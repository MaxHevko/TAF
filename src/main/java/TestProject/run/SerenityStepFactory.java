package TestProject.run;

import com.google.common.collect.Lists;
import net.serenitybdd.jbehave.ClassFinder;
import org.jbehave.core.configuration.Configuration;

import java.util.ArrayList;
import java.util.List;

public class SerenityStepFactory extends net.serenitybdd.jbehave.SerenityStepFactory {

    private List<String> rootPackages;
    private ClassLoader classLoader;

    public SerenityStepFactory(Configuration configuration, List<String> rootPackages, ClassLoader classLoader) {
        super(configuration, null, classLoader);
        this.classLoader = classLoader;
        this.rootPackages = rootPackages;
    }

    @Override
    protected List<Class> getCandidateClasses() {
        List<Class<?>> allClassesUnderRootPackage = new ArrayList<>();
        ClassFinder classFinder = ClassFinder.loadClasses().withClassLoader(classLoader);


        for (String packageName: this.rootPackages) {
            allClassesUnderRootPackage.addAll(classFinder.fromPackage(packageName));
        }

        List<Class> candidateClasses = Lists.newArrayList();
        for(Class<?> classUnderRootPackage : allClassesUnderRootPackage) {

            if (hasAnnotatedMethods(classUnderRootPackage)) {
                candidateClasses.add(classUnderRootPackage);
            }
        }

        return candidateClasses;
    }

    public static SerenityStepFactory withStepsFromPackage(List<String> rootPackages, Configuration configuration) {
        return new SerenityStepFactory(configuration, rootPackages, defaultClassLoader());
    }

    private static ClassLoader defaultClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }
}

