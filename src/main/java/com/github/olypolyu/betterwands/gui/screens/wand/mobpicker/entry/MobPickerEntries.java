package com.github.olypolyu.betterwands.gui.screens.wand.mobpicker.entry;

import com.github.olypolyu.betterwands.BetterWands;
import com.github.olypolyu.betterwands.compat.BetterWandsPlugin;
import com.github.olypolyu.betterwands.gui.screens.wand.mobpicker.entry.options.MobPickerOptionBoolean;
import net.minecraft.core.entity.EntityDispatcher;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.entity.animal.MobAnimal;
import net.minecraft.core.entity.animal.MobChicken;
import net.minecraft.core.entity.animal.MobCow;
import net.minecraft.core.entity.animal.MobPig;
import net.minecraft.core.entity.monster.*;

import javax.print.attribute.standard.MediaSize;
import java.util.List;
import java.util.stream.Collectors;

public final class MobPickerEntries {
	static final MobPickerCategory ANIMAL = new MobPickerCategory("animal");
	static final MobPickerCategory HOSTILE = new MobPickerCategory("hostile");
	static final MobPickerCategory NETHER = new MobPickerCategory("nether");
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

		NETHER.addEntries(
			MobPickerEntry.defineEntry(MobGhast.class),
			MobPickerEntry.defineEntry(MobZombiePig.class)
		);

		OTHER.addEntries(
			MobPickerEntry.defineEntry(MobHuman.class)
		);

		List<Class<? extends Mob>> allEntities = EntityDispatcher.classToIdMap.keySet()
			.stream()
			.filter(Mob.class::isAssignableFrom)
			.map(it -> ((Class<? extends Mob>) it))
			.collect(Collectors.toList());

		for (Class<? extends Mob> mobClass : allEntities) {
			if (!MobPickerEntry.isInitialized(mobClass)) {
				if (MobMonster.class.isAssignableFrom(mobClass)) {
					HOSTILE.addEntry(MobPickerEntry.defineEntry(mobClass));
				}
				else if (MobAnimal.class.isAssignableFrom(mobClass)) {
					ANIMAL.addEntry(MobPickerEntry.defineEntry(mobClass));
				}
				else {
					OTHER.addEntry(MobPickerEntry.defineEntry(mobClass));
				}
			}
		}
	}
}
