package com.github.tsa6.mazemaker;

public class Point {
	private final int x;
	private final int y;
	
	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	@Override
	public String toString() {
		return "("+x+", "+y+")";
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
}
