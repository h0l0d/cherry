package ru.dkovalev.brokenpair;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class ReconcileService<R> {

    private final JobService left;
    private final JobService right;
    private final BiFunction<JobStats, JobStats, R> reconResultSupplier;

    public ReconcileService(JobService left,
                            JobService right,
                            BiFunction<JobStats, JobStats, R> reconResultSupplier) {
        this.left = Objects.requireNonNull(left);
        this.right = Objects.requireNonNull(right);
        this.reconResultSupplier = Objects.requireNonNull(reconResultSupplier);
    }

    public Map<String, R> reconcile() {
        Map<String, JobStats> leftStats = left.getStatsByStatus();
        Map<String, JobStats> rightStats = right.getStatsByStatus();

        Set<String> statuses = new HashSet<>();
        statuses.addAll(leftStats.keySet());
        statuses.addAll(rightStats.keySet());

        return statuses.stream()
                .collect(Collectors.toMap(
                        s -> s,
                        s -> reconResultSupplier.apply(leftStats.get(s), rightStats.get(s))));
    }
}
