package com.github.tsa6.mazemaker;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.Arrays;
import java.util.List;
import javax.swing.JPanel;

@SuppressWarnings("serial")
class MazePanel extends JPanel {
	
	private final Maze maze;
	private final int margin = 10;
	private final boolean solve;
	
	public MazePanel(Maze maze, boolean solve, int size) {
		this.maze = maze;
		this.solve = solve;
		super.setPreferredSize(new Dimension(maze.getWidth()*size, maze.getHeight()*size));
	}
	
	@Override
	public void paintComponent(Graphics page) {
		maze.paintOnto(page, solve, getSize().width, getSize().height, margin);
	}
}