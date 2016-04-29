import javaslang.control.Try;
import org.hamcrest.BaseMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import ru.sbt.qa.bdd.AutotestError;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertThat;

/**
 * Created by vk on 29.04.16.
 */
@RunWith(JUnit4.class)
public class WebDriverTest {
    private WebDriver drv;

    protected Function<SearchContext, Function<By, Try<WebElement>>> tryFindElement =
            e -> selector -> Try.of(() -> e.findElement(selector));

    protected WebElement waitForElement(By selector) {
        return longWait.until((WebDriver drv) -> tryFindElement.apply(drv).apply(selector)
                .filter(WebElement::isEnabled)
                .orElse(null)
        );
    }

    protected Function<String, Consumer<Throwable>> rethrowAutoException = msg -> e -> {
        throw new AutotestError(msg, e);
    };

    WebDriverWait longWait;
    WebDriverWait shortWait;
    WebElement searchInput, searchBtn;

    BiFunction<String, Predicate<Object>, BaseMatcher<Object>> objMatcherSupp = AssertionsFP.typedMatcher2(Object.class);
    Function<String, Function<Predicate<WebElement>, BaseMatcher<WebElement>>> webElMatcherSupp = AssertionsFP.typedMatcher1(WebElement.class);


    @Before
    public void before() {
        System.setProperty("webdriver.chrome.driver", "chromedriver");
        drv = new ChromeDriver();
        longWait = new WebDriverWait(drv, 1200);
        shortWait = new WebDriverWait(drv, 500);
        drv.get("http://www.yandex.ru");
        searchInput = waitForElement(By.id("text"));
        searchBtn = tryFindElement.apply(drv).apply(By.cssSelector("div.search2__button > button"))
                .onFailure(rethrowAutoException.apply("searchButton not found"))
                .get();
    }

    @After
    public void after() {
        drv.close();
    }

    @Test
    public void extractSearchResultsTest() {
        searchInput.sendKeys("qaconf Омск");
        searchBtn.click();
        longWait.until((WebDriver drv) -> tryFindElement.apply(drv).apply(By.cssSelector("div.input__found"))
                .filter(WebElement::isDisplayed)
                .filter(e -> !e.getText().isEmpty())
                .orElse(null));
        List<WebElement> searchResults = drv.findElements(By.cssSelector("div.serp-list div.serp-item > h2.serp-item__title > a"));
        List<String> searchTitles = searchResults.stream().map((el) -> el.getText()).collect(Collectors.toList());

        BaseMatcher<Object> sizeAboveZero = objMatcherSupp.apply("Длина массива должна быть >0", o -> ((List<?>) o).size() > 0);

        BaseMatcher<WebElement> qaConfMatcher = webElMatcherSupp
                .apply("Результаты должны содержать qaconf.ru")
                .apply(e -> e.getAttribute("href").contains("qaconf.ru"));

        assertThat("no data found", searchTitles, sizeAboveZero);
        assertThat("no qaconf in results", searchResults, hasItem(qaConfMatcher));
    }


}
