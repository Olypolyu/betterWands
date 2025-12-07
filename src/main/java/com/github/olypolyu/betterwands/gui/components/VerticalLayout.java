package com.github.olypolyu.betterwands.gui.components;

import com.github.olypolyu.betterwands.gui.interfaces.GuiInput;
import com.github.olypolyu.betterwands.gui.interfaces.GuiWidget;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiElement;

import java.util.List;

public class VerticalLayout<T extends GuiWidget & GuiElement> extends BaseElement implements GuiWidget, GuiInput {

	public final List<T> widgets;

	@Override
	public void mouseClicked(int mouseX, int mouseY, int mouseKey) {
		widgets.stream().filter(it -> GuiInput.class.isAssignableFrom(it.getClass())).forEach(it -> ((GuiInput) it).mouseClicked(mouseX, mouseY, mouseKey));
	}

	public VerticalLayout(List<T> widgets) {
		this.widgets = widgets;
	}

	@Override
	public void render(Gui gui, int mouseX, int mouseY, float partialTick) {
		widgets.forEach(it -> it.render(gui, mouseX, mouseY, partialTick));
	}

	@Override
	public void tick() {
		for (T widget : widgets) {
			widget.tick();
			updatePositions();
		}
	}

	@Override
	public int getHeight() {
		return widgets.stream().map(GuiElement::getHeight).reduce(0, Integer::sum);
	}

	public int getYDiff() {
		if (this.widgets.isEmpty()) return 0;

		return this.getHeight() - this.widgets.get(this.widgets.size() -1).getHeight();
	}

	protected void updatePositions() {
		int localY = 0;

		for (T widget : widgets) {
			widget.setX(this.getX());
			widget.setY(this.getY() + localY);
			localY += widget.getHeight();
		}
	}

	@Override
	public void setX(int x) {
		super.setX(x);
		updatePositions();
	}

	@Override
	public void setY(int y) {
		super.setY(y);
		updatePositions();
	}
}
