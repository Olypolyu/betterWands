package com.github.olypolyu.betterwands.gui.components;

import com.github.olypolyu.betterwands.gui.interfaces.GuiInput;
import com.github.olypolyu.betterwands.gui.interfaces.GuiWidget;
import com.github.olypolyu.betterwands.util.Area2d;
import com.github.olypolyu.betterwands.util.Signal;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.render.texture.Texture;
import net.minecraft.core.util.helper.MathHelper;
import org.lwjgl.input.Mouse;

import java.awt.image.AreaAveragingScaleFilter;

public class ScrollBarVertical extends BaseElement implements GuiInput, GuiWidget {

	protected Area2d head = new Area2d(18, 1, 9, 2);
	protected Area2d body = new Area2d(18, 3, 9, 2);
	protected Area2d tail = new Area2d(18, 5, 9, 3);
	protected Texture texture = Minecraft.getMinecraft().textureManager.loadTexture("/assets/betterwands/textures/gui/buttons.png");

	public boolean beingDragged;
	protected boolean hasFocus;
	protected float cursorPositionPercent = 0;

	public boolean HasFocus() {
		return hasFocus;
	}

	public void setFocus(boolean hasFocus) {
		this.hasFocus = hasFocus;
	}

	protected int cursorHeight = 1;
	protected int trackHeight = 10;

	public ScrollBarVertical(int x, int y, int h) {
		this.setX(x);
		this.setY(y);
		this.setHeight(h);
		this.width = 9;
	}

	@Override
	public void setWidth(int width) {}

	public void mouseReleased(int mouseX, int mouseY) {
		beingDragged = false;
	}

	@Override
	public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		this.beingDragged = new Area2d(this.getX(), this.getY(), getWidth(), getHeight()).pointIntersects(mouseY, mouseX);
	}

	@Override
	public void mouseDragged(int mouseX, int mouseY) {
		this.cursorPositionPercent = (float) (mouseY - this.y) / this.height;
	}

	float cursorPositionPercentLast = 0;
	public Signal<Float> cursorMoved = new Signal<>();

	@Override
	public void tick() {
		if (!beingDragged) {
			cursorPositionPercent += (float) ((Mouse.getDWheel() * -1) * cursorHeight) / trackHeight;
			cursorPositionPercent = MathHelper.clamp(cursorPositionPercent, 0, 1);
		}

		if (cursorPositionPercentLast != cursorPositionPercent) {
			cursorMoved.emit(cursorPositionPercent);
		}

		cursorPositionPercentLast = cursorPositionPercent;
	}

	@Override
	public void render(Gui gui, int mouseX, int mouseY, float partialTick) {
		if (beingDragged) {
			mouseDragged(mouseX, mouseY);
			cursorPositionPercent = MathHelper.clamp(cursorPositionPercent, 0, 1);
		}


		int scrollBarHeightPx = (int) (((float)this.cursorHeight / this.height) * this.height);

		if (scrollBarHeightPx < 32) {
			scrollBarHeightPx = 32;
		}

		int start = (int) (
			(float) this.y + (float) (this.getHeight() - scrollBarHeightPx) * cursorPositionPercent
		);

		int end = start + 30;

		for (int localY = start; localY < end; localY += 2) {
			texture.bind();
			gui.drawTexturedModalRect(this.getX(), localY, body.x, body.y, body.width, body.height);
		}

		gui.drawTexturedModalRect(this.getX(), start, head.x, head.y, head.width, head.height);
		gui.drawTexturedModalRect(this.getX(), end -2, tail.x, tail.y, tail.width, tail.height);
	}

}
