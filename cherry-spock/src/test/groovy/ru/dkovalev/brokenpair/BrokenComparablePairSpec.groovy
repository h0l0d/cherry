package ru.dkovalev.brokenpair

import org.apache.commons.lang3.tuple.ImmutablePair
import spock.lang.Specification

class BrokenComparablePairSpec extends Specification {

    def left = Stub(JobService)
    def right = Stub(JobService)

    def "numeric type coercion on equals for comparable"() {
        expect:
        123L == 123.0
        "1" == 49

        and:
        !Objects.equals(123L, 123.0)
        !Objects.equals("1", 49)
    }

    /**
     * {@link org.codehaus.groovy.runtime.ScriptBytecodeAdapter#compareEqual(java.lang.Object, java.lang.Object)}
     * {@link org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation#compareEqual(java.lang.Object, java.lang.Object)}
     * {@link org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation#compareToWithEqualityCheck(java.lang.Object, java.lang.Object, boolean)}
     */
    def "compareToWithEqualityCheck fails when compareTo throws"() {
        given:
        def left = ImmutablePair.of(
                new Person(name: "Bob"),
                new Integer(42)
        )
        def right = ImmutablePair.of(
                new Person(name: "Bob"),
                new Integer(42)
        )

        expect: "java says it's equals"
        Objects.equals(left, right)

        and: "groovy thinks it's not"
        left != right
    }

    def "reconciliation mismatches when it should match"() {
        given:
        ReconcileService<ImmutablePair<JobStats, JobStats>> service = new ReconcileService<>(
                left,
                right,
                { left, right -> ImmutablePair.of(left, right) })
        left.getStatsByStatus() >> ["Completed": new JobStats(count: 3, duration: 10)]
        right.getStatsByStatus() >> ["Completed": new JobStats(count: 3, duration: 10)]

        expect:
        service.reconcile() != [
                "Completed": ImmutablePair.of(
                        new JobStats(count: 3, duration: 10),
                        new JobStats(count: 3, duration: 10))
        ]
    }

    def "reconciliation matches"() {
        given:
        ReconcileService<ReconResult<JobStats>> service = new ReconcileService(
                left,
                right,
                { left, right -> ReconResult.of(left, right) })
        left.getStatsByStatus() >> ["Completed": new JobStats(count: 3, duration: 10)]
        right.getStatsByStatus() >> ["Completed": new JobStats(count: 3, duration: 10)]

        expect:
        service.reconcile() == [
                "Completed": ReconResult.of(
                        new JobStats(count: 3, duration: 10),
                        new JobStats(count: 3, duration: 10))
        ]
    }

}
