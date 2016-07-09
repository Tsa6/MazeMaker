package com.github.tsa6.mazemaker;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Node{
	
	private final int x;
	private final int y;
	private boolean connected = false;
	private final Node[][] parentGrid;
	private ArrayList<Node> nodeGroup;
	private final ArrayList<Node> directConnections;
	
	@SuppressWarnings("LeakingThisInConstructor")
	public Node(int x, int y, Node[][] parentGrid) {
		this.x = x;
		this.y = y;
		this.parentGrid = parentGrid;
		nodeGroup = new ArrayList<>();
		nodeGroup.add(this);
		directConnections = new ArrayList<>();
	}
	
	public Stream<Node> adjorningNodes() {
		Stream.Builder<Node> out = Stream.<Node>builder();
		if(x != 0) {
			out.accept(parentGrid[y][x-1]);
		}
		if(x != parentGrid[0].length - 1) {
			out.accept(parentGrid[y][x+1]);
		}
		if(y != 0) {
			out.accept(parentGrid[y-1][x]);
		}
		if(y != parentGrid.length - 1) {
			out.accept(parentGrid[y+1][x]);
		}
		return out.build();
	}
	
	public void formPerimeter() {
		List<Node> nodes = adjorningNodes().filter(n -> !n.isConnected()).filter(n -> n.isOnPerimeter()).collect(Collectors.toList());
		if(nodes.size() > 0){
			Node next = nodes.get(0);
			connect(next);
			next.formPerimeter();
		}
	}
	
	public void connect(Node n) {
		if(connectedTo(n)) {
			throw new IllegalArgumentException("Tried to connect "+n+" and "+this+", two already connected nodes");
		}
		mergeNodeGroups(n);
		directConnections.add(n);
		n.directConnections.add(this);
		connected = true;
		n.connected = true;
	}
	
	@SuppressWarnings("AccessingNonPublicFieldOfAnotherObject")
	private void mergeNodeGroups (Node otherNode) {
		if(otherNode.nodeGroup.size() > nodeGroup.size()) {
			otherNode.mergeNodeGroups(this);
		}else if (connectedTo(otherNode)){
			throw new IllegalArgumentException(otherNode + " shares a node group with "+this);
		}else {
			otherNode.nodeGroup.forEach(n -> {
				nodeGroup.add(n);
				n.nodeGroup = nodeGroup;
			});
		}
	}
	
	public Stream<Connection> allConnections() {
		return adjorningNodes().filter(directConnections::contains).map(n -> new Connection(this, n));
	}
	
	public boolean canExpand() {
		return adjorningNodes().anyMatch(n -> !connectedTo(n));
	}
	
	public Node expand() {
		List<Node> nodes = adjorningNodes().filter(n -> !connectedTo(n)).collect(Collectors.toList());
		if(nodes.size()>0){
			Node selected = nodes.get((int)(Math.random()*nodes.size()));
			connect(selected);
			return selected;
		}else{
			return null;
		}
	}
	
	@Override
	public String toString() {
		return "Node ("+x+", "+y+")";
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	boolean isConnected() {
		return connected;
	}
	
	public boolean connectedTo(Node n) {
		return n.nodeGroup == nodeGroup;
	}
	
	public boolean directConnectedTo(Node n) {
		return directConnections.contains(n);
	}
	
	boolean isOnPerimeter() {
		return x==0||
			   y==0||
			   x==parentGrid[0].length-1||
			   y==parentGrid.length-1;
	}
	
	@SuppressWarnings("PublicInnerClass")
	public class Connection {
		private final Point from;
		private final Point to;

		private Connection(Node from, Node to) {
			this.from = new Point(from.x, from.y);
			this.to = new Point(to.x, to.y);
		}
		
		@Override
		public String toString() {
			return from+" --> "+to;
		}

		public Point getPoint1() {
			return from;
		}

		public Point getPoint2() {
			return to;
		}
	}
}