import com.google.common.base.Function;
import javaslang.control.Try;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import ru.sbt.qa.bdd.AutotestError;

/**
 * Created by sbt-neradovskiy-kl on 19.01.2016.
 */
public class WebDriverWaitRef {
    WebDriver driver;
    public WebElement waitForElement(By selector) {
        /*
        try {
            WebElement el = new WebDriverWait(driver, 5).until(new Function<WebDriver, WebElement>() {
            public WebElement apply(WebDriver webDriver) {
                try {
                    WebElement el = driver.findElement(selector);
                    if (el.isEnabled())
                        return el;
                } catch (Exception e) {
                    //silently catch
                }
                return null;}
            });
            el.click();
            return el;
        } catch (Exception e) {
            throw new AutotestError("Не удается найти элемент");
        }
*/
        WebDriverWait shortWait = new WebDriverWait(driver,5);
        Try.of(()->shortWait.until((WebDriver e)-> Try.of(()->driver.findElement(selector))
                .filter(WebElement::isEnabled).orElse(null)))
                .andThen(WebElement::click)
                .onFailure(exp->{throw new AutotestError("Не удается найти элемент",exp);});

    }
}
