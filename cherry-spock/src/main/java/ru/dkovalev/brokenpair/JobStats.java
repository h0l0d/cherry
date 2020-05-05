package ru.dkovalev.brokenpair;

import java.util.Objects;

public class JobStats {

    private int count;

    private int duration;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JobStats jobStats = (JobStats) o;
        return count == jobStats.count &&
                duration == jobStats.duration;
    }

    @Override
    public int hashCode() {
        return Objects.hash(count, duration);
    }

    @Override
    public String toString() {
        return "JobStats{" +
                "count=" + count +
                ", duration=" + duration +
                '}';
    }
}
