package ru.dkovalev.versioning

import spock.lang.Specification

class VersionRangeSpec extends Specification {

    def "builds unbounded range"() {
        expect:
        VersionRange.fromSpec('(,)') == VersionRange.unbounded()
        VersionRange.fromSpec('[,]') == VersionRange.unbounded()
    }

    def "builds open range"() {
        expect:
        VersionRange.fromSpec('(1.0,2.0)') == VersionRange.open(new Version('1.0'), new Version('2.0'))
    }

    def "builds open range skipping spaces"() {
        expect:
        VersionRange.fromSpec('( 1.0 , 2.0 )') == VersionRange.open(new Version('1.0'), new Version('2.0'))
    }

    def "open range contains"() {
        given:
        def range = VersionRange.fromSpec('(1.0,2.0)')

        expect:
        !range.contains(new Version('1.0'))
        range.contains(new Version('1.5'))
        !range.contains(new Version('2.0'))
    }

    def "builds closed range"() {
        expect:
        VersionRange.fromSpec('[1.0,2.0]') == VersionRange.closed(new Version('1.0'), new Version('2.0'))
    }

    def "closed range contains"() {
        given:
        def range = VersionRange.fromSpec('[1.0,2.0]')

        expect:
        range.contains(new Version('1.0'))
        range.contains(new Version('1.5'))
        range.contains(new Version('2.0'))
    }

    def "builds left-closed and right-open range"() {
        expect:
        VersionRange.fromSpec('[1.0,2.0)') == VersionRange.leftClosedRightOpen(new Version('1.0'), new Version('2.0'))
    }

    def "left-closed and right-open range contains"() {
        given:
        def range = VersionRange.fromSpec('[1.0,2.0)')

        expect:
        range.contains(new Version('1.0'))
        range.contains(new Version('1.5'))
        !range.contains(new Version('2.0'))
    }

    def "builds left-open and right-closed range"() {
        expect:
        VersionRange.fromSpec('(1.0,2.0]') == VersionRange.leftOpenRightClosed(new Version('1.0'), new Version('2.0'))
    }

    def "left-open and right-closed range contains"() {
        given:
        def range = VersionRange.fromSpec('(1.0,2.0]')

        expect:
        !range.contains(new Version('1.0'))
        range.contains(new Version('1.5'))
        range.contains(new Version('2.0'))
    }

    def "builds left-open range"() {
        expect:
        VersionRange.fromSpec('(1.0,)') == VersionRange.leftOpen(new Version('1.0'))
        VersionRange.fromSpec('(1.0,]') == VersionRange.leftOpen(new Version('1.0'))
    }

    def "left-open range contains"() {
        given:
        def range = VersionRange.fromSpec('(1.0,)')

        expect:
        !range.contains(new Version('1.0'))
        range.contains(new Version('1.5'))
        range.contains(new Version('2.0'))
    }

    def "builds left-closed range"() {
        expect:
        VersionRange.fromSpec('[1.0,)') == VersionRange.leftClosed(new Version('1.0'))
        VersionRange.fromSpec('[1.0,]') == VersionRange.leftClosed(new Version('1.0'))
    }

    def "left-closed range contains"() {
        given:
        def range = VersionRange.fromSpec('[1.0,)')

        expect:
        range.contains(new Version('1.0'))
        range.contains(new Version('1.5'))
        range.contains(new Version('2.0'))
    }

    def "builds right-open range"() {
        expect:
        VersionRange.fromSpec('(,2.0)') == VersionRange.rightOpen(new Version('2.0'))
        VersionRange.fromSpec('[,2.0)') == VersionRange.rightOpen(new Version('2.0'))
    }

    def "right-open range contains"() {
        given:
        def range = VersionRange.fromSpec('(,2.0)')

        expect:
        range.contains(new Version('1.0'))
        range.contains(new Version('1.5'))
        !range.contains(new Version('2.0'))
    }

    def "builds right-closed range"() {
        expect:
        VersionRange.fromSpec('(,2.0]') == VersionRange.rightClosed(new Version('2.0'))
        VersionRange.fromSpec('[,2.0]') == VersionRange.rightClosed(new Version('2.0'))
    }

    def "right-closed range contains"() {
        given:
        def range = VersionRange.fromSpec('(,2.0]')

        expect:
        range.contains(new Version('1.0'))
        range.contains(new Version('1.5'))
        range.contains(new Version('2.0'))
    }
}
