package com.github.olypolyu.betterwands.gui.screens.wand.mobpicker.entry;

import com.github.olypolyu.betterwands.BetterWands;
import com.github.olypolyu.betterwands.compat.BetterWandsPlugin;
import com.github.olypolyu.betterwands.gui.screens.wand.mobpicker.entry.options.MobPickerOptionBoolean;
import net.minecraft.core.entity.EntityDispatcher;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.entity.animal.MobChicken;
import net.minecraft.core.entity.animal.MobCow;
import net.minecraft.core.entity.animal.MobPig;
import net.minecraft.core.entity.monster.MobCreeper;
import net.minecraft.core.entity.monster.MobSkeleton;
import net.minecraft.core.entity.monster.MobSpider;
import net.minecraft.core.entity.monster.MobZombie;

import java.util.List;
import java.util.stream.Collectors;

public final class MobPickerEntries {
	static final MobPickerCategory ANIMAL = new MobPickerCategory("animal");
	static final MobPickerCategory NETHER = new MobPickerCategory("nether");
	static final MobPickerCategory UNDEAD = new MobPickerCategory("undead");

	static final MobPickerCategory PASSIVE = new MobPickerCategory("passive");
	static final MobPickerCategory HOSTILE = new MobPickerCategory("hostile");
	static final MobPickerCategory NEUTRAL = new MobPickerCategory("neutral");

	static final MobPickerCategory OTHER = new MobPickerCategory("other");

	public static void defineMobPickerEntries() {
		BetterWands.plugins.forEach(BetterWandsPlugin::defineMobPickerEntries);

		ANIMAL.addEntries(
			MobPickerEntry.defineEntry(
				MobPig.class,
				new MobPickerOptionBoolean("has_saddle", true)
			),

			MobPickerEntry.defineEntry(MobChicken.class),
			MobPickerEntry.defineEntry(MobCow.class)
		);

		HOSTILE.addEntries(
			MobPickerEntry.defineEntry(MobZombie.class),
			MobPickerEntry.defineEntry(MobSkeleton.class),
			MobPickerEntry.defineEntry(MobSpider.class),
			MobPickerEntry.defineEntry(MobCreeper.class)
		);

		List<Class<? extends Mob>> allEntities = EntityDispatcher.classToIdMap.keySet()
			.stream()
			.filter(Mob.class::isAssignableFrom)
			.map(it -> ((Class<? extends Mob>) it))
			.collect(Collectors.toList());

		for (Class<? extends Mob> mobClass : allEntities) {
			if (!MobPickerEntry.isInitialized(mobClass)) {
				OTHER.addEntry(MobPickerEntry.defineEntry(mobClass));
			}
		}
	}
}
