import javaslang.control.Try;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sbt.qa.bdd.AutotestError;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by sbt-neradovskiy-kl on 19.01.2016.
 */
public class WebDriverWaitFP {
    WebDriver driver;
    Logger logger = LoggerFactory.getLogger(WebDriverWaitFP.class);

    protected Function<String, Function<Throwable, String>> recoverDebugException = v -> e -> {
        logger.debug("Exception for value: " + v, e);
        return v;
    };

    protected Function<String, Consumer<Throwable>> rethrowAutoException = msg -> e -> {
        throw new AutotestError(msg, e);
    };

    protected Function<String, Consumer<Throwable>> reportDebugException = msg -> e -> {
        logger.debug("Exception: " + msg, e);
    };

    protected Function<String, Supplier<RuntimeException>> supplyRuntimeException = msg -> () -> new RuntimeException(msg);


    protected Function<SearchContext, Function<By, Try<WebElement>>> tryFindElement = e -> selector -> Try.of(() -> e.findElement(selector));

    WebDriverWait longWait = new WebDriverWait(driver,100);
    WebDriverWait shortWait = new WebDriverWait(driver,100);

    protected WebElement tryWaitForElement(By selector) {
        Try.of(() -> longWait.until((WebDriver d) -> findElementOnPage(selector).orElse(null)))
                .andThen(WebElement::click)
                .onFailure(rethrowAutoException.apply("Элемент не найден")).get();
        findElementOnPage(selector).map(WebElement::getText).onFailure(rethrowAutoException.apply("text"));
        WebElement table = findElementOnPage(By.id("table")).onFailure(rethrowAutoException.apply("text")).get();
        final Function<By, Try<WebElement>> searchInTable = tryFindElement.apply(table);
        searchInTable.apply(By.xpath(".//td"));
        return null;
    }

    protected Try<WebElement> findElementOnPage(By selector) {
        return Try.of(() -> driver.findElement(selector));
    }
}
