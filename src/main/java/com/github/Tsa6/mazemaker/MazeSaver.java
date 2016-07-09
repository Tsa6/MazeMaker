/*
 */
package com.github.tsa6.mazemaker;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author Taizo Simpson
 */
public class MazeSaver {
	public static void main(String[] args) throws IOException {
		Maze maze = new Maze(50,50, false);
		
		//Save unsolved
		maze.saveImageTo(new File("output.png"));
		maze.saveImageTo(new File("outputSolved.png"), true);
	}
}
