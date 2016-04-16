import org.openqa.selenium.WebElement;
import ru.sbt.qa.bdd.AutotestError;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by sbt-neradovskiy-kl on 19.01.2016.
 */
public class CollectionsRef {
    List<WebElement> listEls;

    public WebElement findElementByText(String text) {
        for(WebElement el : listEls) {
            if(el.getText().equals(text)) return el;
        }
        //return null;
        return listEls.stream().filter(WebElement::isDisplayed)
                .filter(e->!e.getAttribute("id").isEmpty()).findFirst().orElse(null);
    }

    public void clickOnElementsContainingText(String text) {
        boolean found=false;
        for(WebElement el : listEls) {
            if(el.getText().contains(text)) {
                el.click();
                found = true;
            }
        if(!found) throw new AutotestError("No elements were found");
        }
        listEls.stream().filter(el->el.getText().contains(text))
                .peek(WebElement::click)
                .findAny().orElseThrow(()->new AutotestError("No elements were found"));
    }

    public List<WebElement> filterElementByAttribute(String attrName,String attrVal) {
        List<WebElement> filtered = new ArrayList<>();
        for(WebElement el : listEls) {
            if(el.getAttribute(attrName).equals(attrVal)) filtered.add(el);
        }
        //return filtered;
        return listEls.stream().filter(el->el.getAttribute(attrName).equals(attrVal))
                .collect(Collectors.toList());
    }

}
