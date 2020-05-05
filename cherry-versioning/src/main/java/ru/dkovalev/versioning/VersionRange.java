package ru.dkovalev.versioning;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Simple support for versions range.
 * <p/>
 * See maven
 * <a href="https://maven.apache.org/ref/3.6.1/maven-artifact/apidocs/org/apache/maven/artifact/versioning/VersionRange.html">VersionRange</a>
 * for more advanced implementation.
 *
 * To be implemented:
 * 1. list of ranges, e.g. (1.0,2.0),[3.1,4.0)
 * 2. simplified range spec for one value range, e.g. 1.0 for [1.0,1.0]
 */
public class VersionRange {

    private static final char LOWER_INCLUSIVE_MARK = '[';
    private static final char LOWER_EXCLUSIVE_MARK = '(';
    private static final char UPPER_INCLUSIVE_MARK = ']';
    private static final char UPPER_EXCLUSIVE_MARK = ')';

    private static final List<Character> LOWER_MARKS = Arrays.asList(LOWER_INCLUSIVE_MARK, LOWER_EXCLUSIVE_MARK);
    private static final List<Character> UPPER_MARKS = Arrays.asList(UPPER_INCLUSIVE_MARK, UPPER_EXCLUSIVE_MARK);

    private final Bound lowerBound;
    private final Bound upperBound;

    private VersionRange(Version lowerBound, boolean lowerBoundInclusive, Version upperBound, boolean upperBoundInclusive) {
        this.lowerBound = new Bound(lowerBound, lowerBoundInclusive);
        this.upperBound = new Bound(upperBound, upperBoundInclusive);
    }

    /**
     * Builds a version range from specification.
     * <p/>
     * <p>The following notations are supported:</p>
     * Proper and bounded:
     * <ul>
     *     <li>Open: (a,b) = { a < x < b } {@link VersionRange#open(Version, Version) open}</li>
     *     <li>Closed: [a,b] = { a <= x <= b } {@link VersionRange#closed(Version, Version) closed}</li>
     *     <li>Left-closed, right-open: [a,b) = { a <= x < b } {@link VersionRange#leftClosedRightOpen(Version, Version) leftClosedRightOpen}</li>
     *     <li>Left-open, right-closed: (a,b] = { a < x <= b } {@link VersionRange#leftOpenRightClosed(Version, Version) leftOpenRightClosed}</li>
     * </ul>
     *
     * Left-bounded and right-unbounded
     * <ul>
     *     <li>Left-open: (a,) = { a < x } {@link VersionRange#leftOpen(Version) leftOpen}</li>
     *     <li>Left-closed: [a,) = { a <= x } {@link VersionRange#leftClosed(Version) leftClosed}</li>
     * </ul>
     *
     * Left-unbounded and right-bounded:
     * <ul>
     *     <li>Right-open: (,b) = { x < b } {@link VersionRange#rightOpen(Version) rightOpen}</li>
     *     <li>Right-closed: (,b] = { x <= b } {@link VersionRange#rightClosed(Version) rightClosed}</li>
     * </ul>
     *
     * Unbounded at both ends (simultaneously open and closed): (,) = no bounds {@link VersionRange#unbounded() unbounded}
     */
    public static VersionRange fromSpec(String spec) {
        // todo Rewrite with regexp
        Objects.requireNonNull(spec);
        if (spec.length() < 3) {
            throw new IllegalArgumentException("Invalid version range specification: " + spec);
        }

        char lowerMark = spec.charAt(0);
        char upperMark = spec.charAt(spec.length() - 1);
        if (!LOWER_MARKS.contains(lowerMark)) {
            throw new IllegalArgumentException("Invalid version range specification: " + spec);
        }
        if (!UPPER_MARKS.contains(upperMark)) {
            throw new IllegalArgumentException("Invalid version range specification: " + spec);
        }

        String interval = spec.substring(1, spec.length() - 1);
        String[] bounds = StringUtils.splitPreserveAllTokens(interval, ",");
        if (bounds.length != 2) {
            throw new IllegalArgumentException("Invalid version range specification: " + spec);
        }

        Version lowerBound = getBound(bounds[0]);
        Version upperBound = getBound(bounds[1]);

        return new VersionRange(
                lowerBound,
                lowerBound != null && lowerMark == LOWER_INCLUSIVE_MARK,
                upperBound,
                upperBound != null && upperMark == UPPER_INCLUSIVE_MARK);
    }

    private static Version getBound(String text) {
        text = StringUtils.trimToNull(text);
        return text == null ? null : new Version(text);
    }


    /**
     * (,) = no bounds
     */
    public static VersionRange unbounded() {
        return new VersionRange(null, false, null, false);
    }

    /**
     * (a,b) = { a < x < b }
     */
    public static VersionRange open(Version lowerBound, Version upperBound) {
        return new VersionRange(lowerBound, false, upperBound, false);
    }

    /**
     * [a,b] = { a <= x <= b }
     */
    public static VersionRange closed(Version lowerBound, Version upperBound) {
        return new VersionRange(lowerBound, true, upperBound, true);
    }

    /**
     * [a,b) = { a <= x < b }
     */
    public static VersionRange leftClosedRightOpen(Version lowerBound, Version upperBound) {
        return new VersionRange(lowerBound, true, upperBound, false);
    }

    /**
     * (a,b] = { a < x <= b }
     */
    public static VersionRange leftOpenRightClosed(Version lowerBound, Version upperBound) {
        return new VersionRange(lowerBound, false, upperBound, true);
    }

    /**
     * (a,) = { a < x }
     */
    public static VersionRange leftOpen(Version lowerBound) {
        return new VersionRange(lowerBound, false, null, false);
    }

    /**
     * [a,) = { a <= x }
     */
    public static VersionRange leftClosed(Version lowerBound) {
        return new VersionRange(lowerBound, true, null, false);
    }

    /**
     * (,b) = { x < b }
     */
    public static VersionRange rightOpen(Version upperBound) {
        return new VersionRange(null, false, upperBound, false);
    }

    /**
     * (,b] = { x <= b }
     */
    public static VersionRange rightClosed(Version upperBound) {
        return new VersionRange(null, false, upperBound, true);
    }

    public boolean contains(Version version) {
        return lowerBound.leftOf(version) && upperBound.rightOf(version);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VersionRange that = (VersionRange) o;
        return Objects.equals(lowerBound, that.lowerBound) &&
                Objects.equals(upperBound, that.upperBound);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lowerBound, upperBound);
    }

    @Override
    public String toString() {
        return "VersionRange{" +
                "lowerBound=" + lowerBound +
                ", upperBound=" + upperBound +
                '}';
    }

    private static class Bound {
        private final Version bound;
        private final boolean inclusive;

        Bound(Version bound, boolean inclusive) {
            this.bound = bound;
            this.inclusive = inclusive;
        }

        boolean leftOf(Version version) {
            Objects.requireNonNull(version);
            return (bound == null) || less(bound, version);
        }

        boolean rightOf(Version version) {
            Objects.requireNonNull(version);
            return (bound == null) || less(version, bound);
        }

        private boolean less(Version a, Version b) {
            int cmp = a.compareTo(b);
            return inclusive ? cmp <= 0 : cmp < 0;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Bound bound1 = (Bound) o;
            return inclusive == bound1.inclusive &&
                    Objects.equals(bound, bound1.bound);
        }

        @Override
        public int hashCode() {
            return Objects.hash(bound, inclusive);
        }

        @Override
        public String toString() {
            return "Bound{" +
                    "bound=" + bound +
                    ", inclusive=" + inclusive +
                    '}';
        }
    }
}
