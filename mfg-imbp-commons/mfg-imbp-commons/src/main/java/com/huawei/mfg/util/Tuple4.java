package com.huawei.mfg.util;

/**
 * General purpose Pair class which can be used to pass/return 4 values
 *
 * @param <E>
 * @param <I>
 * @param <R>
 * @param <T>
 */
public class Tuple4<E, I, R, T> {
    private E first;
    private I second;
    private R third;
    private T fourth;

    private Tuple4(E first, I second, R third, T fourth) {
        this.first = first;
        this.second = second;
        this.third = third;
        this.fourth = fourth;
    }

    public static <E, I, R, T> Tuple4<E, I, R, T> of(E first, I second, R third, T fourth) {
        return new Tuple4(first, second, third, fourth);
    }

    public E getFirst() {
        return first;
    }

    public I getSecond() {
        return second;
    }

    public R getThird() {
        return third;
    }

    public T getFourth() {
        return fourth;
    }

    @Override
    public String toString() {
        return "Tuple4{" +
                "first=" + first +
                ", second=" + second +
                ", third=" + third +
                ", fourth=" + fourth +
                '}';
    }
}
