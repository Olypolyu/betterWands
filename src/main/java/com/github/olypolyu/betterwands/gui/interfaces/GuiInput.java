package com.github.olypolyu.betterwands.gui.interfaces;

public interface GuiInput {
	default void handleKeyPress(char character, int key) {};
	default void mouseClicked(int mouseX, int mouseY, int mouseKey) {};
	default void mouseDragged(int mouseX, int mouseY) {};
	default void mouseReleased(int mouseX, int mouseY) {};
}
