package TestProject.utils.reflection;

import TestProject.pages_with_steps_definitions.BasePage;
import TestProject.utils.reflection.annotations.*;
import com.google.common.base.Optional;
import net.thucydides.core.annotations.DefaultUrl;
import net.thucydides.core.annotations.NotImplementedException;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.NotFoundException;
import org.openqa.selenium.WebDriver;
import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;

import static java.util.Arrays.stream;

public class Page {

    public static BasePage get(String pageName, WebDriver driver) {
        Class<BasePage> pageType = getPageType(pageName);

        return get(pageType, driver);
    }

    private static <T extends BasePage> BasePage get(Class<T> pageType, WebDriver driver) {
        Object pageObj = initPageObject(pageType, driver);
        BasePage page = (BasePage) pageObj;

        return page;
    }

    private static Object initPageObject(Class objectType, WebDriver driver) {
        Constructor[] ctors = objectType.getConstructors();
        Constructor ctor = ctors[0];

        Parameter[] ctorParams = ctor.getParameters();
        Object[] invokeParams = initParams(ctorParams, driver);

        try {

            return ctor.newInstance(invokeParams);
        } catch (Exception ex) {

            throw new RuntimeException("Failed to execute constructor for " + objectType, ex);
        }
    }

    public static String getPageNameAnnotationValue(Class<?> pageObjectType) {
        PageName annotation = (PageName) pageObjectType.getAnnotation(PageName.class);
        Optional<String> opAnnotation;

        if (annotation != null) {
            opAnnotation = Optional.fromNullable(annotation.value());
        } else {
            opAnnotation = Optional.absent();
        }

        if (opAnnotation.isPresent()) {

            return opAnnotation.get();
        } else {

            return StringUtils.EMPTY;
        }
    }

    public static String getUrlAnnotationValue(Class<?> pageObjectType) {
        DefaultUrl annotation = pageObjectType.getAnnotation(DefaultUrl.class);
        String hardcode = "DEADC0DE";//TODO as a temporary solution if DefaultUrl is not set return DEADC0DE
        if (annotation == null) return hardcode;

        return annotation.value();
    }

    private static Class getPageType(String pageName) {
        String[] packagesNames = System.getProperty("packages.to.run").split(",");
        Reflections reflections = new Reflections((Object[]) packagesNames);
        Set<Class<? extends BasePage>> allPageTypes = reflections.getSubTypesOf(BasePage.class);
        Class<? extends BasePage> pageType = allPageTypes
                .stream()
                .filter(cl -> getPageNameAnnotationValue(cl).equals(pageName))
                .findFirst()
                .orElse(null);

        if (pageType != null) {

            return pageType;
        }

        throw new NotFoundException(String.format("The page object with: '%s' annotation has not been found in packages: '%s'", pageName, Arrays.toString(packagesNames)));
    }

    private static Object[] initParams(Parameter[] ctorParams, WebDriver driver) {
        Object[] invokeParams = new Object[ctorParams.length];
        for (int i = 0; i < ctorParams.length; i++) {
            if (ctorParams[i].getType().getName() == WebDriver.class.getName()) {
                invokeParams[i] = driver;
                continue;
            } else {
                throw new NotImplementedException(String.format("Steps for parameter with `{0}` name are not implemented.", ctorParams[i].getName()));
            }
        }

        return invokeParams;
    }

    private static String getSelectorValue(String controlName, Map<Field, BasePage> fieldsToBasePageInstanceMap) {

        for (Field field : fieldsToBasePageInstanceMap.keySet()) {
            if (isAnnotationPresentAndControlNameIsEqual(field, controlName)) {
                try {
                    field.setAccessible(true);
                    return (String) field.get(fieldsToBasePageInstanceMap.get(field));
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        throw new NotFoundException(getExceptionText(controlName, fieldsToBasePageInstanceMap)); //TODO Poiner to the page where exception occurs
    }

    private static boolean isAnnotationPresentAndControlNameIsEqual(Field field, String expectedValue) {
        List<Class> annotations = Arrays.asList(ButtonLink.class, TextField.class, DropDown.class,
                RadioButton.class, CheckBox.class, Label.class);
        String actualAnotationVal = "";
        for (Class c : annotations)
            if (field.isAnnotationPresent(c)) {
                Annotation annotation = field.getAnnotation(c);
                try {
                    Method method = annotation.getClass().getMethod("value");
                    actualAnotationVal = (String) method.invoke(annotation);
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }

        return actualAnotationVal.equals(expectedValue);
    }

    private static String getExceptionText(String controlName, Map<Field, BasePage> fieldsToObject) {
        String msg = "The selector field has not been found. Control name: '%s', Looked up in classes : '%s'. Founded annotations: %s";
        final Set<String> classList = new HashSet<>();
        final StringBuilder anotationList = new StringBuilder();
        fieldsToObject.keySet().stream().filter(f -> f.getAnnotations().length > 0).forEach(f -> anotationList.append(f.getAnnotations()[0].toString()));
        fieldsToObject.values().stream().forEach(s -> classList.add(s.getClass().toString()));
        return String.format(msg, controlName, classList.toString(), anotationList.toString());
    }


    @Deprecated
    public static String getSelector(BasePage page, String controlName, Class annotationType) {
        return getSelector(page, controlName);
    }

    public static String getSelector(BasePage page, String controlName) {
        String selector;
        try {
            Map<Field, BasePage> fieldsToObj = getFields(page);
            selector = getSelectorValue(controlName, fieldsToObj);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return selector;
    }

    private static Map<Field, BasePage> getFields(BasePage currentPageObject) throws IllegalAccessException {
        Map<Field, BasePage> selfAndParentFields = expandContainers(
                getFieldsFromSelfAndParent(currentPageObject));
        Map<Field, BasePage> containerFields = expandContainers(getFieldsFromContainers(currentPageObject));
        selfAndParentFields.putAll(containerFields);

        while (selfAndParentFields.keySet().stream().allMatch(f -> f.getAnnotation(Container.class) != null))
            selfAndParentFields = expandContainers(selfAndParentFields);

        return selfAndParentFields;
    }

    private static Map<Field, BasePage> expandContainers(Map<Field, BasePage> fields) throws IllegalAccessException {
        Map<Field, BasePage> result = new HashMap<>();
        for (Field f : fields.keySet()) {
            if (f.isAnnotationPresent(Container.class)) {
                f.setAccessible(true);
                result.putAll(getFieldsFromContainers((BasePage) f.get(fields.get(f))));
            } else {
                result.put(f, fields.get(f));
            }
        }
        return result;
    }

    private static Map<Field, BasePage> getFieldsFromContainers(BasePage bpObject) throws IllegalAccessException {
        Map<Field, BasePage> innerFieldsToContainer = new LinkedHashMap<>();
        for (Field f : bpObject.getClass().getDeclaredFields()) {
            if (f.isAnnotationPresent(Container.class)) {
                f.setAccessible(true);
                BasePage containerObject = (BasePage) f.get(bpObject);
                Field[] innerFields = f.getType().getDeclaredFields();
                stream(innerFields).forEach(ifl -> innerFieldsToContainer.put(ifl, containerObject));
                f.setAccessible(false);
            }
        }
        return innerFieldsToContainer;
    }

    private static Map<Field, BasePage> getFieldsFromSelfAndParent(BasePage currentPageObject) {
        Field[] fields = currentPageObject.getClass().getDeclaredFields();
        Class parentPage = currentPageObject.getClass().getSuperclass();
        while (parentPage != BasePage.class) {
            Field[] parentFields = parentPage.getDeclaredFields();
            parentPage = parentPage.getSuperclass();
            fields = (Field[]) ArrayUtils.addAll(fields, parentFields);
        }

        Map<Field, BasePage> result = new LinkedHashMap<>();
        stream(fields).forEach(f -> result.put(f, currentPageObject));

        return result;
    }

}

