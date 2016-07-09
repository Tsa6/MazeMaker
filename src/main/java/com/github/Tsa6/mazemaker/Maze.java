package com.github.tsa6.mazemaker;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.imageio.ImageIO;

public class Maze {
	
	private final Square[][] tiles;
	private final Node[][] nodeGrid;
	private final int WIDTH;
	private final int HEIGHT;
	private final ArrayList<Square> path;
	
	public Maze(int width, int height, boolean noload) {
		WIDTH = width;
		HEIGHT = height;
		//Initialize nodeGrid and form perimeter of nodes
		nodeGrid = new Node[HEIGHT][WIDTH];
		ArrayList<Node> perimeterNodes = new ArrayList<>();
		for(int r = 0; r < nodeGrid.length; r++) {
			for(int c = 0; c < nodeGrid[r].length; c++) {
				nodeGrid[r][c] = new Node(c, r, nodeGrid);
				if(nodeGrid[r][c].isOnPerimeter()) {
					perimeterNodes.add(nodeGrid[r][c]);
				}
			}
		}
		Node startNode = perimeterNodes.get((int)(Math.random()*perimeterNodes.size()));
		startNode.formPerimeter();
		
		//Generate the maze
		int totalNodes = WIDTH * HEIGHT - perimeterNodes.size();
		int remainingNodes = totalNodes;
		Node[] expandableNodes = Arrays.stream(nodeGrid)
				.flatMap(a -> Arrays.stream(a))
				.filter(n -> n.canExpand())
				.toArray(l -> new Node[l]);
		while(expandableNodes.length > 0) {
			Node rand = expandableNodes[(int)(Math.random() * expandableNodes.length)];
			rand.expand();
			expandableNodes = Arrays.stream(nodeGrid)
					.flatMap(a -> Arrays.stream(a))
					.filter(n -> n.canExpand())
					.toArray(l -> new Node[l]);
			if(!noload)
				System.out.printf("\rGenerating Maze:  %.2f%%", 100 - (100f*--remainingNodes/totalNodes));
		}
		System.out.println();
		
		//Solve the maze
		tiles = new Square[nodeGrid.length-1][nodeGrid[0].length-1];
		for(int r = 0; r < nodeGrid.length - 1; r++) {
			for(int c = 0; c < nodeGrid[r].length - 1; c++) {
				tiles[r][c] = new Square(c, r, tiles, nodeGrid);
			}
		}
		path = Arrays.stream(tiles).flatMap(a -> Arrays.stream(a))
								   .filter(s -> s != null && s.isStart())
								   .findAny()
								   .get()
								   .pathfind(null, 0);
		System.out.println(path.get(path.size()-1) + " is start");
	}
	
	public void paintOnto(Graphics page, boolean showSolution, int width, int height, int margin) {
		
		margin -= 0.5f * Math.min(xMult(width, margin), yMult(height, margin));
		final int finalMargin = margin;
		
		page.setColor(Color.WHITE);
		page.fillRect(0, 0, width, height);
		
		if(showSolution) {
			page.setColor(Color.RED);
			for(int i = 1; i < path.size(); i++) {
				page.drawLine(
						formatX(path.get(i - 1).getX() + 0.5f, width, margin),
						formatY(path.get(i - 1).getY() + 0.5f, height, margin),
						formatX(path.get(i).getX() + 0.5f, width, margin),
						formatY(path.get(i).getY() + 0.5f, height, margin)
				);
			}
		}
		
		Square end = path.get(path.size() - 1);
		
		page.setColor(Color.GREEN);
		page.fillOval(formatX(end.getX(), width, margin), formatY(end.getY(), height, margin), Math.round(xMult(width, margin)), Math.round(yMult(height, margin)));
		page.setColor(Color.BLACK);
		
		Arrays.stream(getNodes())
				.flatMap(a -> Arrays.stream(a))
				.flatMap(n -> n.allConnections())
				.forEach(c -> page.drawLine(formatX(c.getPoint1().getX(), width, finalMargin),formatY(c.getPoint1().getY(), height, finalMargin),formatX(c.getPoint2().getX(), width, finalMargin),formatY(c.getPoint2().getY(), height, finalMargin)));
	}
	
	public void saveImageTo(File file) throws IOException {
		saveImageTo(file, false);
	}
	
	public void saveImageTo(File file, boolean showSolution) throws IOException {
		saveImageTo(file, 600, 600, 10, showSolution);
	}
	
	public void saveImageTo(File file, int width, int height) throws IOException {
		saveImageTo(file, width, height, 10, false);
	}
	
	public void saveImageTo(File file, int width, int height, int margin, boolean showSolution) throws IOException {
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		paintOnto(image.getGraphics(), showSolution, image.getWidth(), image.getHeight(), margin);
		ImageIO.write(image, "png", file);
	}
	
	private int formatX(float x, int width, int margin) {
		return (int)(margin + (x + 0.5) * xMult(width, margin));
	}
	
	private int formatY(float y, int height, int margin) {
		return (int)(margin + (y + 0.5) * yMult(height, margin));
	}
	
	private float xMult(int width, int margin) {
		return (width - 2 * margin) / (float)getNodes()[0].length;
	}
	
	private float yMult(int height, int margin) {
		return (height - 2 * margin) / (float)getNodes().length;
	}

	public int getWidth() {
		return WIDTH;
	}

	public int getHeight() {
		return HEIGHT;
	}
	
	@SuppressWarnings("ReturnOfCollectionOrArrayField")
	Node[][] getNodes() {
		//I know it's bad practice, but it's better than working with 2D lists.
		return nodeGrid;
	}
	
	public List<Square> getPath() {
		return Collections.unmodifiableList(path);
	}
}
