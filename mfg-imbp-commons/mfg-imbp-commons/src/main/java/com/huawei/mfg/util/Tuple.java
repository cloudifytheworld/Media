package com.huawei.mfg.util;

/**
 * General purpose Pair class which can be used to pass/return two values
 *
 * @param <R>
 * @param <T>
 *
 */
public class Tuple<R, T> {
	private R first;
	private T second;

	Tuple(R first, T second) {
		super();
		this.first = first;
		this.second = second;
	}

	public static <R, T> Tuple<R, T> of(R first, T second) {
		return new Tuple<R, T>(first, second);
	}
	
	public R getFirst() {
		return first;
	}

	public T getSecond() {
		return second;
	}

	@Override
	public String toString() {
		return "Tuple{" +
				"first=" + first +
				", second=" + second +
				'}';
	}
}
