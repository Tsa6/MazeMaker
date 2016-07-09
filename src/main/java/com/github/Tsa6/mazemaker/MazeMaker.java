package com.github.tsa6.mazemaker;

import javax.swing.JFrame;

public class MazeMaker {
	
	public static void main(String[] args) {
		int width = 30;
		int height = -1;
		int spacing = 17;
		boolean noload = false;
		boolean solve = false;
		
		for(int i = 0; i < args.length; i++) {
			try{
				switch(args[i]) {
					case "-w":
						width = Integer.parseInt(args[++i]);
						break;
					case "-h":
						height = Integer.parseInt(args[++i]);
						if(height < 0) {
							System.err.println("Height must be >= 1");
							System.exit(1);
						}
						break;
					case "-s":
						spacing = Integer.parseInt(args[++i]);
						break;
					case "-noload":
						noload = true;
						break;
					case "-solve":
						solve = true;
						break;
					default:
						System.err.println("Usage: [-w width [-h height]] [-solve] [-noload]");
						System.exit(0);
				}
			}catch(NumberFormatException e) {
				System.err.println("Argument "+(--i)+" was not provided in proper integer format ("+(++i)+")");
				System.exit(1);
			}
		}
		
		if(width < 1) {
			System.err.println("Width must be >= 1");
			System.exit(1);
		}else if(height < 1) {
			height = width;
		}
		
		Maze maze = new Maze(width,height,noload);
		
		//Create the frame
		JFrame frame = new JFrame("Maze Maker");
		MazePanel contentPane = new MazePanel(maze,solve,spacing);
		frame.getContentPane().add(contentPane);
		frame.pack();
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
