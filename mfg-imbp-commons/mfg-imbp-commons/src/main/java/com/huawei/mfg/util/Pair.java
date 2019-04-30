package com.huawei.mfg.util;

/**
 * General purpose Pair class which can be used to pass/return two values
 *
 * @param <R>
 * @param <T>
 *
 */
public class Pair <R, T> {
	private R first;
	private T second;
	
	Pair(R first, T second) {
		super();
		this.first = first;
		this.second = second;
	}

	public static <R, T> Pair<R, T> of(R first, T second) {
		return new Pair<R, T>(first, second);
	}
	
	public R getFirst() {
		return first;
	}

	public T getSecond() {
		return second;
	}

	@Override
	public String toString() {
		return "Pair{" +
				"first=" + first +
				", second=" + second +
				'}';
	}
}
