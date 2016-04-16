import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.openqa.selenium.WebElement;

import java.util.function.Function;
import java.util.function.Predicate;


import static org.hamcrest.core.AllOf.allOf;
import static org.hamcrest.core.AnyOf.anyOf;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

/**
 * Created by vk on 23.02.16.
 */
public class AssertionsFP {
    Function<String,Function<Predicate<WebElement>,BaseMatcher<WebElement>>>
            createMatcher = desc -> pred -> new BaseMatcher<WebElement>() {
        @Override
        public boolean matches(Object o) {
            return pred.test((WebElement)o);
        }

        @Override
        public void describeTo(Description description) {
            description.appendText(desc);
        }
    };

    Function<Predicate<WebElement>,BaseMatcher<WebElement>> simpleDescMatcher = createMatcher.apply("WebElement check failed");
    BaseMatcher<WebElement> isEnabled = simpleDescMatcher.apply(WebElement::isEnabled);
    BaseMatcher<WebElement> hasSomeText = createMatcher.apply("WebElement doesn't contain 'SomeText'")
                                    .apply(e->e.getText().contains("SomeText"));
    public void testAssertions(WebElement elem2Test) {

        assertThat(elem2Test,anyOf(isEnabled,hasSomeText));

        assertThat(elem2Test,allOf(isEnabled,hasSomeText));

        assertThat(elem2Test,not(isEnabled));

        assertThat(elem2Test,createMatcher.apply("Should be Table").apply(e->e.getTagName().equalsIgnoreCase("table")));
    }
}
