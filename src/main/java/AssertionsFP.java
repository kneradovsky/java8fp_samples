import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.openqa.selenium.WebElement;

import java.util.function.BiFunction;
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

    public static <T> BiFunction<String, Predicate<T>, BaseMatcher<T>> typedMatcher2(Class<T> cls) {
        return (desc, pred) -> new BaseMatcher<T>() {
            @Override
            public boolean matches(Object o) {
                return pred.test((T) o);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText(desc);
            }
        };
    }

    public static <T> Function<String, Function<Predicate<T>, BaseMatcher<T>>> typedMatcher1(Class<T> cls) {
        return desc -> pred -> new BaseMatcher<T>() {
            @Override
            public boolean matches(Object o) {
                return pred.test((T) o);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText(desc);
            }
        };
    }


    public static Function<String, Function<Predicate<WebElement>, BaseMatcher<WebElement>>>
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
