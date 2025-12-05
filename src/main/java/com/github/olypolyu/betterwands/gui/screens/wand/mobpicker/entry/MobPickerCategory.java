package com.github.olypolyu.betterwands.gui.screens.wand.mobpicker.entry;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MobPickerCategory {

	public final String name;

	public static List<MobPickerCategory> allCategories = new ArrayList<>();
	protected static Set<MobPickerEntry> allEntries = new HashSet<>();

	protected Set<MobPickerEntry> entries = new HashSet<>();

	public MobPickerCategory(String name) {
		allCategories.add(this);
		this.name = name;
	}

	public void addEntry(MobPickerEntry entry) {
		this.entries.add(entry);
		allEntries.add(entry);
	}

	public void addEntries(MobPickerEntry ...entries) {
		for (MobPickerEntry entry : entries) {
			this.entries.add(entry);
			allEntries.add(entry);
		}
	}

	public Set<MobPickerEntry> getEntries() {
		return new HashSet<>(entries);
	}

	public static Set<MobPickerEntry> getEntriesAllCategories() {
		return new HashSet<>(allEntries);
	}
}
