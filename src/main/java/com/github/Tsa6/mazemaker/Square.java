package com.github.tsa6.mazemaker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Stream;

public class Square {
	
	private final int x;
	private final int y;
	private final Square[][] parentGrid;
	private final boolean isStart;
	private final HashMap<Direction, Boolean> blocks = new HashMap<>();
	
	public Square(int x, int y, Square[][] parentGrid, Node[][] nodeGrid) {
		this.x = x;
		this.y = y;
		this.parentGrid = parentGrid;

		blocks.put(Direction.UP, y == 0 || nodeGrid[y][x].directConnectedTo(nodeGrid[y][x+1]));
		blocks.put(Direction.LEFT, x == 0 || nodeGrid[y][x].directConnectedTo(nodeGrid[y+1][x]));
		blocks.put(Direction.RIGHT, x == parentGrid[0].length - 1 || nodeGrid[y+1][x+1].directConnectedTo(nodeGrid[y][x+1]));
		blocks.put(Direction.DOWN, y == parentGrid.length - 1 || nodeGrid[y+1][x+1].directConnectedTo(nodeGrid[y+1][x]));
		
		isStart = 
				(x == 0 && !nodeGrid[y][x].directConnectedTo(nodeGrid[y+1][x])) ||
				(y == 0 && !nodeGrid[y][x].directConnectedTo(nodeGrid[y][x+1])) ||
				(x == parentGrid[0].length - 1 && !nodeGrid[y][x+1].directConnectedTo(nodeGrid[y+1][x+1])) ||
				(y == parentGrid.length - 1 && !nodeGrid[y+1][x].directConnectedTo(nodeGrid[y+1][x+1]));
		
		if(isStart) {
			System.out.println(this+" is exit");
		}
	}
	
	Stream<Square> availableSquares() {
		Stream.Builder<Square> out = Stream.<Square>builder();
		if(!blocks.get(Direction.UP)) {
			out.accept(parentGrid[y-1][x]);
		}
		if(!blocks.get(Direction.DOWN)) {
			out.accept(parentGrid[y+1][x]);
		}
		if(!blocks.get(Direction.LEFT)) {
			out.accept(parentGrid[y][x-1]);
		}
		if(!blocks.get(Direction.RIGHT)) {
			out.accept(parentGrid[y][x+1]);
		}
		return out.build();
	}
	
	ArrayList<Square> pathfind(Square sender, int debug) {
		ArrayList<ArrayList<Square>> paths = new ArrayList<>();
		availableSquares().filter(s -> s != sender).forEach(s -> paths.add(s.pathfind(this, debug + 1)));
		ArrayList<Square> best = null;
		int bestScore = -1;
		for(ArrayList<Square> p: paths) {
			if(p.size() > bestScore) {
				best = p;
				bestScore = p.size();
			}
		}
		
		if(best == null) {
			ArrayList<Square> out = new ArrayList<>();
			out.add(this);
			return out;
		}else{
			best.add(0,this);
			return best;
		}
	}
	
	@Override
	public String toString() {
		return "Square ("+x+", "+y+")";
	}
	
	public boolean isStart() {
		return isStart;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
}
