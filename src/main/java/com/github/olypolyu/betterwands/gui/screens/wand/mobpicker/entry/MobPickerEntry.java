package com.github.olypolyu.betterwands.gui.screens.wand.mobpicker.entry;

import com.github.olypolyu.betterwands.gui.screens.wand.mobpicker.entry.options.MobPickerOption;
import net.minecraft.core.entity.EntityDispatcher;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.lang.I18n;
import org.jspecify.annotations.Nullable;

import java.util.*;

public class MobPickerEntry {

	protected static Map<Class<? extends Mob>, MobPickerEntry> classMobPickerEntryMap = new HashMap<>();

	public final Class<? extends Mob> mobClass;
	public boolean shouldDisplay = true;

	protected final List<MobPickerOption<?>> options;

	protected MobPickerEntry(Class<? extends Mob> mobClass) {
		this.mobClass = mobClass;
		// calculate defaults
		this.options = new ArrayList<>();
	}

	protected MobPickerEntry(Class<? extends Mob> mobClass, List<MobPickerOption<?>> options) {
		this.mobClass = mobClass;
		this.options = options;
	}

	static public MobPickerEntry defineEntry(Class<? extends Mob> mobClass) {
		if (isInitialized(mobClass)) {
			throw new RuntimeException("Attempted to define entry twice for " + mobClass.getSimpleName());
		}

		MobPickerEntry result = new MobPickerEntry(mobClass);
		classMobPickerEntryMap.put(mobClass, result);
		return result;
	}

	static public MobPickerEntry defineEntry(Class<? extends Mob> mobClass, MobPickerOption<?> ...options) {
		if (isInitialized(mobClass)) {
			throw new RuntimeException("Attempted to define entry twice for " + mobClass.getSimpleName());
		}

		MobPickerEntry result = new MobPickerEntry(mobClass, Arrays.asList(options));
		classMobPickerEntryMap.put(mobClass, result);
		return result;
	}

	static public @Nullable MobPickerEntry getEntryFor(Class<? extends Mob> mobClass) {
		return classMobPickerEntryMap.getOrDefault(mobClass, null);
	}

	static public boolean isInitialized(Class<? extends Mob> mobClass) {
		return classMobPickerEntryMap.containsKey(mobClass);
	}

	public String getMobName(I18n translator) {
		String name = translator.translateKey(EntityDispatcher.nameKeyForClass(mobClass));
		if (name == null) return mobClass.getSimpleName();
		return name;
	}

	public List<MobPickerOption<?>> getOptions() {
		return new ArrayList<>(this.options);
	}
}
