package ru.dkovalev.versioning

import spock.lang.Specification

class VersionSpec extends Specification {

    def "checks canonical format"() {
        given:
        def v = new Version(s)

        expect:
        v.componentsCount == 4
        v.version == s
        v.canonicalVersion == '1.0.0.0'

        where:
        s << ['1', '1.0', '1.0.0', '1.0.0.0']
    }

    def "checks equality"() {
        given:
        def v1 = new Version(s1)
        def v2 = new Version(s2)

        expect:
        v1.equals(v2)
        v2.equals(v1)

        and: "compareTo is consistent with equals"
        v1.compareTo(v2) == 0
        v2.compareTo(v1) == 0

        where:
        s1        | s2
        '1'       | '1'
        '1'       | '1.0'
        '1'       | '1.0.0'
        '1'       | '1.0.0.0'

        '1.0'     | '1.0'
        '1.0'     | '1.0.0'
        '1.0'     | '1.0.0.0'

        '1.0.0'   | '1.0.0'
        '1.0.0'   | '1.0.0.0'

        '1.0.0.0' | '1.0.0.0'
    }

    def "checks non equality"() {
        given:
        def v1 = new Version(s1)
        def v2 = new Version(s2)

        expect:
        !v1.equals(v2)
        !v2.equals(v1)

        and: "compareTo is consistent with equals"
        v1.compareTo(v2) != 0
        v2.compareTo(v1) != 0

        where:
        s1        | s2
        '1'       | '2'
        '1.1'     | '1.2'
        '1.1.1'   | '1.1.2'
        '1.1.1.1' | '1.1.1.2'
    }

    def "checks order"() {
        given:
        def v1 = new Version(s1)
        def v2 = new Version(s2)

        expect:
        v1.compareTo(v2) < 0
        v2.compareTo(v1) > 0

        where:
        s1        | s2
        '1'       | '2'
        '1.1'     | '1.2'
        '1.1.1'   | '1.1.2'
        '1.1.1.1' | '1.1.1.2'

        '1'       | '1.2'
        '1'       | '1.1.2'
        '1'       | '1.1.1.2'
    }

    def "validates version format"() {
        when:
        new Version('bla')

        then:
        def e = thrown(IllegalArgumentException)
        e.message == 'Invalid version format: bla'
    }

    def "validates min component count"() {
        when:
        new Version('1.0', 0)

        then:
        def e = thrown(IllegalArgumentException)
        e.message == 'Components count must be >= 1: 0'
    }

    def "validates max component count"() {
        when:
        new Version('1.0.0.0.0')

        then:
        def e = thrown(IllegalArgumentException)
        e.message == 'Only 4 components supported: 1.0.0.0.0'
    }
}
