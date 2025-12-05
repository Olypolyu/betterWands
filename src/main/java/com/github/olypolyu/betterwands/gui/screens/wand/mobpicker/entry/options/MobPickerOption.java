package com.github.olypolyu.betterwands.gui.screens.wand.mobpicker.entry.options;

import org.jspecify.annotations.Nullable;

public abstract class MobPickerOption<E> {
	public final String nameKey;
	public final E defaultValue;

	@Nullable
	protected E value = null;

	protected MobPickerOption(String nameKey, E defaultValue) {
		this.nameKey = nameKey;
		this.defaultValue = defaultValue;
	}

	public E getValue() {
		if (value == null) return defaultValue;
		return value;
	}

	public void setValue(@Nullable E value) {
		this.value = value;
	}
}
