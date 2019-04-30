package com.huawei.mfg.util;

/**
 * General purpose Pair class which can be used to pass/return 3 values
 * @param <I>
 * @param <R>
 * @param <T>
 */
public class Tuple3<I, R, T> {
    private I first;
    private R second;
    private T third;

    private Tuple3(I first, R second, T third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    public static <I, R, T> Tuple3<I, R, T> of(I first, R second, T third) {
        return new Tuple3<>(first, second, third);
    }

    public I getFirst() {
        return first;
    }

    public R getSecond() {
        return second;
    }

    public T getThird() {
        return third;
    }

    @Override
    public String toString() {
        return "Tuple3{" +
                "first=" + first +
                ", second=" + second +
                ", third=" + third +
                '}';
    }
}
