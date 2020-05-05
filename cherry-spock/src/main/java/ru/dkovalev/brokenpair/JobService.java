package ru.dkovalev.brokenpair;

import java.util.Map;

public interface JobService {
    Map<String, JobStats> getStatsByStatus();
}
