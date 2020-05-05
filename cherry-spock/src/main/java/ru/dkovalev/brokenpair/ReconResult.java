package ru.dkovalev.brokenpair;

import java.util.Objects;

public class ReconResult<T> {

    private final T left;
    private final T right;

    private ReconResult(T left, T right) {
        this.left = left;
        this.right = right;
    }

    public static <T> ReconResult<T> of(T left, T right) {
        return new ReconResult<>(left, right);
    }

    public T getLeft() {
        return left;
    }

    public T getRight() {
        return right;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReconResult<?> that = (ReconResult<?>) o;
        return Objects.equals(left, that.left) &&
                Objects.equals(right, that.right);
    }

    @Override
    public int hashCode() {
        return Objects.hash(left, right);
    }

    @Override
    public String toString() {
        return "ReconResult{" +
                "left=" + left +
                ", right=" + right +
                '}';
    }
}
