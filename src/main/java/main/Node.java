package main;

import java.awt.Color;
import java.awt.Graphics2D;

public class Node {
	private static final int DIAM = 10;
	//fields
	public int xpos;
	public int ypos;
	public double value;
	public Game game;
	public Node lastNode;
	public Node(int x, int y, Game g, double Value) {
		xpos =x;
		ypos = y;
		game=g;
		value=Value;
	}
	
	public void draw(Graphics2D g2) {
		g2.setColor(Color.white);
		g2.drawString(Double.toString(value), xpos, ypos+20);
		g2.fillOval(xpos-(DIAM/2), ypos-(DIAM/2), DIAM, DIAM);
	}
	
	public boolean isOn(int x, int y) {
    	if(x < xpos+5 && x > xpos-10 && y < ypos+5 && y > ypos-10) {
    		return true;
    	}else {
    		return false;
    	}
	}
	
	public void drawRed(Graphics2D g2) {
		g2.setColor(Color.red);
		g2.fillOval(xpos-(DIAM/2), ypos-(DIAM/2), DIAM, DIAM);
		g2.setColor(Color.white);
	}
	
	public void setX(int x) {
		xpos =x;
	}
	
	public void setY(int y) {
		ypos = y;
	}
	public void setGame(Game g) {
		game=g;
	}
	public void setGame(double Value) {
		value=Value;
	}
	public void setLastNode(Node node) {
		lastNode = node;
	}
	
}
