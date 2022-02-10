package TestProject.utils.driver;

import TestProject.pages_with_steps_definitions.BasePage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.concurrent.TimeUnit;

public class DriverUtils {
    private static final Logger logger = LogManager.getLogger();
    public static void openPageWithAttempts(BasePage page, WebDriver driver, int timeLimitForAttempt, int maxNumberOfAttempts) {

        boolean load_is_successfull = false;
        int number_of_attempts = 0;

        WebDriver.Timeouts timeouts = driver.manage().timeouts();

        timeouts.pageLoadTimeout(timeLimitForAttempt, TimeUnit.SECONDS);
        timeouts.setScriptTimeout(timeLimitForAttempt, TimeUnit.SECONDS);

        while (!load_is_successfull && number_of_attempts <= maxNumberOfAttempts) {

            try {
                page.open();
                load_is_successfull = true;
            }
            catch (TimeoutException ex) {
                logger.warn("Expected TimeoutException exception has happened when waiting for loading page");
            }
            logger.info("Page re-open attempt #: " + number_of_attempts);
            number_of_attempts++;
        }


        driver.manage().timeouts().pageLoadTimeout(600, TimeUnit.SECONDS);
        driver.manage().timeouts().setScriptTimeout(600, TimeUnit.SECONDS);
    }

    public static void waitForPageLoad(WebDriver driver) {
        waitForPageLoad(driver, 60L);
    }

    public static void waitForPageLoad(WebDriver driver, long timeOutInSeconds){
        ExpectedCondition<Boolean> pageLoadCondition = new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {

                return ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete");
            }
        };

        WebDriverWait wait = new WebDriverWait(driver, timeOutInSeconds);
        wait.until(pageLoadCondition);
    }

}