package ru.dkovalev.versioning;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Simple support for string based versions in "a.b.c.d" format.
 * <p/>
 * See maven
 * <a href="https://maven.apache.org/ref/3.6.1/maven-artifact/apidocs/org/apache/maven/artifact/versioning/ComparableVersion.html">ComparableVersion</a>
 * for more advanced implementation.
 */
public class Version implements Comparable<Version> {

    private static final int DEFAULT_COMPONENTS_COUNT = 4;
    private static final String SEPARATOR = ".";
    private static final Pattern VERSION_FORMAT = Pattern.compile("[0-9]+(?:\\.[0-9+])*");

    private final String version;
    private final String canonicalVersion;
    private final int[] components;

    public Version(String version) {
        this(version, DEFAULT_COMPONENTS_COUNT);
    }

    public Version(String version, int componentsCount) {
        this.version = Objects.requireNonNull(version);
        if (!VERSION_FORMAT.matcher(this.version).matches()) {
            throw new IllegalArgumentException("Invalid version format: " + version);
        }
        if (componentsCount < 1) {
            throw new IllegalArgumentException("Components count must be >= 1: " + componentsCount);
        }
        this.components = parseComponents(this.version, componentsCount);
        this.canonicalVersion = Arrays.stream(this.components)
                .mapToObj(String::valueOf)
                .collect(Collectors.joining(SEPARATOR));
    }

    private int[] parseComponents(String version, int componentsCount) {
        // String.split fastpath is not activated for "." hence using StringUtils
        String[] split = StringUtils.split(version, SEPARATOR);
        if (split.length > componentsCount) {
            throw new IllegalArgumentException("Only " + componentsCount + " components supported: " + version);
        }
        int[] components = new int[componentsCount];
        for (int i = 0; i < split.length; i++) {
            components[i] = Integer.parseInt(split[i]);
        }
        return components;
    }

    public String getVersion() {
        return version;
    }

    public String getCanonicalVersion() {
        return canonicalVersion;
    }

    public int getComponentsCount() {
        return components.length;
    }

    /**
     * Consistent with equals.
     */
    @Override
    public int compareTo(Version o) {
        int length = Math.max(components.length, o.components.length);
        for (int i = 0; i < length; i++) {
            int thisPart = getPart(components, i);
            int thatPart = getPart(o.components, i);
            if (thisPart < thatPart) {
                return -1;
            }
            if (thisPart > thatPart) {
                return 1;
            }
        }
        return 0;
    }

    private int getPart(int[] components, int i) {
        return i < components.length ? components[i] : 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Version version = (Version) o;
        return Objects.equals(canonicalVersion, version.canonicalVersion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(canonicalVersion);
    }

    @Override
    public String toString() {
        return version;
    }
}
