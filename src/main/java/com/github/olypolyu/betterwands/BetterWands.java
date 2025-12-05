package com.github.olypolyu.betterwands;

import com.github.olypolyu.betterwands.compat.BetterWandsPlugin;
import com.github.olypolyu.betterwands.gui.screens.wand.mobpicker.entry.MobPickerEntries;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.util.collection.NamespaceID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import turniplabs.halplibe.util.GameStartEntrypoint;
import turniplabs.halplibe.util.RecipeEntrypoint;

import java.util.ArrayList;
import java.util.List;

public class BetterWands implements ModInitializer, RecipeEntrypoint, GameStartEntrypoint {
	public static final String MOD_ID = "betterwands";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static NamespaceID id(String key) {
		return NamespaceID.getPermanent(MOD_ID, key);
	}

	public static final List<BetterWandsPlugin> plugins = new ArrayList<>();

	@Override
	public void onInitialize() {
		LOGGER.info("ExampleMod initialized.");

		FabricLoader.getInstance()
			.getEntrypointContainers("aether", BetterWandsPlugin.class)
			.forEach(plugin -> plugins.add(plugin.getEntrypoint()));
	}

	@Override
	public void onRecipesReady() {

	}

	@Override
	public void initNamespaces() {

	}

	@Override
	public void beforeGameStart() {
	}

	@Override
	public void afterGameStart() {
		MobPickerEntries.defineMobPickerEntries();
	}
}
