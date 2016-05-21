import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static org.hamcrest.core.AllOf.allOf;
import static org.hamcrest.core.CombinableMatcher.both;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeThat;

/**
 * Created by vk on 22.04.16.
 */
@RunWith(JUnit4.class)
public class CollectionsTest {

    static Function<String, Function<Predicate<Integer[]>, BaseMatcher<Integer[]>>>
            createMatcher = desc -> pred -> new BaseMatcher<Integer[]>() {
        @Override
        public boolean matches(Object o) {
            return pred.test((Integer[]) o);
        }

        @Override
        public void describeTo(Description description) {
            description.appendText(desc);
        }
    };


    BaseMatcher<Integer[]> testEvens = createMatcher.apply("Ожидаются только четные числа")
            .apply(arr -> !Stream.of(arr).filter(i -> i % 2 != 0).findFirst().isPresent());


    Function<Integer, BaseMatcher<Integer[]>> testDiv = div -> createMatcher.apply("Ожидаются только числа, делящиеся на " + div)
            .apply(arr -> !Stream.of(arr).filter(i -> i % div != 0).findFirst().isPresent());


    static Function<Integer, BaseMatcher<Integer[]>> testLen = len -> createMatcher.apply("Ожидается длина " + len)
            .apply(arr -> arr.length == len);

    BiFunction<String,Integer[],BaseMatcher<Integer[]>> arrayMatcherSupp = typedMatcher2(Integer[].class)
    Function<Integer, BaseMatcher<Integer[]>> testLen2 = len -> arrayMatcherSupp.apply("Ожидается длина " + len,arr -> arr.length == len);

    

    Integer[] evens, odds;


    @Before
    public void BeforeTests() {
        evens = Stream.generate(Math::random)
                .map(d -> ((int) (d * 1000)) % 1000).filter(i -> i % 2 == 0)
                .limit(1000)
                .toArray((len) -> new Integer[len]);

    }
    public CollectionsTest() {};

    @Test
    public void testEven() {

        BaseMatcher<Integer[]> testEvens = createMatcher.apply("Ожидаются только четные числа")
                .apply(arr -> !Stream.of(arr).filter(i -> i % 2 != 0).findFirst().isPresent());
        assumeThat("assuming only evens", evens, testEvens);
        assertThat("only evens", evens, allOf(testEvens, testLen.apply(1000)));
    }

    @Test
    public void testOdds() {
        odds = Stream.generate(Math::random)
                .map(d -> ((int) (d * 1000)) % 1000).filter(i -> i % 2 != 0)
                .filter(i -> i % 3 == 0)
                .limit(500)
                .toArray((len) -> new Integer[len]);

        assumeThat("assume only odds", odds, not(testDiv.apply(2)));
        assertThat("делятся на 3 и длина равна 500", odds, both(testDiv.apply(3)).and(testLen.apply(500)));

    }

}
