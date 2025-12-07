package com.github.olypolyu.betterwands.util;

public class Area2d extends Point2d {
	public int width;
	public int height;

	public Area2d(int x, int y, int width, int height) {
		super(x, y);
		this.width = width;
		this.height = height;
	}

	public int getMaxX() {
		return x + width;
	}

	public int getMaxY() {
		return x + height;
	}

	public int getCenterX() {
		return x + width/2;
	}

	public int getCenterY() {
		return y + height/2;
	}

	public boolean pointIntersects(int y, int x) {
		return (
			this.y < y && y < (this.y + this.height) &&
				this.x < x && x < (this.x + this.width)
		);
	}
}
