package com.github.olypolyu.betterwands.mixin;

import com.github.olypolyu.betterwands.gui.screens.wand.mobpicker.MobPickerScreen;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.PlayerLocal;
import net.minecraft.core.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = PlayerLocal.class, remap = false)
public class summoningWandMixin {

	@Shadow
	protected Minecraft mc;

	@WrapMethod(method = "displayWandMobPickerScreen")
	public void displayWandMobPickerScreen(ItemStack itemStack, Operation<Void> original) {
		mc.displayScreen(new MobPickerScreen(itemStack));
	}
}
