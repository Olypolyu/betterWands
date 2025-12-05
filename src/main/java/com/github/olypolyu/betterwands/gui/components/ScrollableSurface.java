package com.github.olypolyu.betterwands.gui.components;

import com.github.olypolyu.betterwands.gui.interfaces.GuiWidget;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiElement;

import java.util.List;

public class ScrollableSurface <T extends GuiWidget & GuiElement> extends BaseElement implements GuiWidget {

	protected final List<T> widgets;

	public ScrollableSurface(List<T> widgets) {
		this.widgets = widgets;
	}

	@Override
	public void render(Gui gui, int mouseX, int mouseY, float partialTick) {
		widgets.forEach(it -> it.render(gui, mouseX, mouseY, partialTick));
	}

	@Override
	public void tick() {
		int localY = 0;

		for (T widget : widgets) {
			widget.tick();
			widget.setX(this.getX());
			widget.setY(this.getY() + localY);
			localY += widget.getHeight();
		}
	}
}
