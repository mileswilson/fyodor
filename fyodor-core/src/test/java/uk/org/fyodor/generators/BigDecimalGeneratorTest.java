package uk.org.fyodor.generators;

import uk.org.fyodor.BaseTestWithRule;
import uk.org.fyodor.Sampler;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.Set;

import static uk.org.fyodor.Sampler.from;
import static uk.org.fyodor.Sampler.largest;
import static uk.org.fyodor.range.Range.closed;
import static uk.org.fyodor.range.Range.fixed;
import static java.math.BigDecimal.*;
import static org.assertj.core.api.Assertions.assertThat;

public final class BigDecimalGeneratorTest extends BaseTestWithRule {

    @Test
    public void generatesFixedBigDecimalWithZeroScale() {
        assertThat(from(RDG.bigDecimal(fixed(TEN))).sample(1000).unique())
                .containsOnly(TEN);
    }

    @Test
    public void defaultScaleIsUpTo2dp() {
        assertThat(RDG.bigDecimal(closed(ZERO, TEN)).next().scale())
                .isGreaterThanOrEqualTo(1)
                .isLessThanOrEqualTo(2);
    }

    @Test
    public void generatesFixedBigDecimalWithNonZeroScale() {
        final BigDecimal fixedValue = new BigDecimal("100.567");

        assertThat(from(RDG.bigDecimal(fixed(fixedValue), 1)).sample(10000).unique())
                .containsOnly(fixedValue);
    }

    @Test(expected = IllegalArgumentException.class)
    public void rangeCannotBeNull() {
        RDG.bigDecimal(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void rangeCannotBeNullForValidScale() {
        RDG.bigDecimal(null, 2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void scaleCannotBeNegative() {
        RDG.bigDecimal(fixed(ONE), -1);
    }

    @Test
    public void decimalPartsAreDifferentForRange() {
        final Set<Integer> decimalParts = decimalPartsOnly(from(RDG.bigDecimal(closed(ZERO, TEN), 1)).sample(100)).unique();
        assertThat(decimalParts).hasSize(10);
    }

    @Test
    public void scaledBigDecimalsDoNotExceedTheUpperBound() {
        final BigDecimal upperBound = TEN;
        final BigDecimal lowerBound = upperBound.subtract(new BigDecimal("0.000000001"));

        assertThat(largest(from(RDG.bigDecimal(closed(lowerBound, upperBound))).sample(1000)))
                .describedAs("The largest value should not exceed the upper bound")
                .isEqualByComparingTo(upperBound);
    }

    @Test
    public void generatesUniqueBigDecimalsWithoutRange() {
        assertThat(from(RDG.bigDecimal()).sample(100).unique())
                .hasSize(100);
    }

    private static Sampler.Sample<Integer> decimalPartsOnly(final Sampler.Sample<BigDecimal> sample) {
        final LinkedList<Integer> decimalParts = new LinkedList<Integer>();
        for (final BigDecimal bigDecimal : sample) {
            decimalParts.add(bigDecimal
                    .remainder(ONE)
                    .movePointRight(bigDecimal.scale())
                    .intValue());
        }
        return new Sampler.Sample<Integer>(decimalParts);
    }
}