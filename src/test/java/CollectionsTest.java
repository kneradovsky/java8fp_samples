

import javaslang.collection.Array;
import javaslang.collection.Stream;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;
import org.junit.runners.JUnit4;
import org.openqa.selenium.WebElement;

import java.util.function.Function;
import java.util.function.Predicate;

import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

/**
 * Created by vk on 22.04.16.
 */
@RunWith(JUnit4.class)
public class CollectionsTest {
    Function<String,Function<Predicate<Array<Integer>>,BaseMatcher<Array<Integer>>>>
            createMatcher = desc -> pred -> new BaseMatcher<Array<Integer>>() {
        @Override
        public boolean matches(Object o) {
            return pred.test((Array<Integer>)o);
        }

        @Override
        public void describeTo(Description description) {
            description.appendText(desc);
        }
    };    @Before
    public void BeforeTests() {

    }
    public CollectionsTest() {};

    @Test
    public void testEven() {
        Array<Integer> evens = Stream.gen(Math::random).take(1000)
                .map( d -> ((int)(d*1000))%1000).filter( i -> i%2==0).toArray();

        BaseMatcher<Array<Integer>> testEvens = createMatcher.apply("Arrays contains an odd number")
                                                .apply(arr -> arr.toStream().exists(i-> i%2==0));
        assertThat("only evens",evens,testEvens);
        assertThat("odds",evens,not(testEvens));
    }

}
