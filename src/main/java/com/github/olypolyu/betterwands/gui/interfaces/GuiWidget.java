package com.github.olypolyu.betterwands.gui.interfaces;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiElement;

public interface GuiWidget {
	void render(Gui gui, int mouseX, int mouseY, float partialTick);
	void tick();
}
