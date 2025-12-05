package com.github.olypolyu.betterwands.util;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.function.Supplier;


public class Nullability {
	private Nullability() {}

	public static <T> @NonNull T orElse(@Nullable T value, @NonNull T def) {
		if (value == null) return def;
		return Objects.requireNonNull(value);
	}

	public static <T> T orElse(Supplier<@Nullable T> valueProvider, Supplier<@NonNull T> defProvider) {
		T val = valueProvider.get();
		if (val != null) return val;
		return Objects.requireNonNull(defProvider.get());
	}
}
