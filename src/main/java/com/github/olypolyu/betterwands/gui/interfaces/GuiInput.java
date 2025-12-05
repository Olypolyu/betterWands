package com.github.olypolyu.betterwands.gui.interfaces;

public interface GuiInput {
	void handleKeyPress(char character, int key);
	void mouseClicked(int mouseX, int mouseY, int mouseKey);
}
