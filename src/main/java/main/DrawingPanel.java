package main;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.*;

@SuppressWarnings({ "serial", "unused" })
public class DrawingPanel extends JComponent {
	private static final int CANVAS_WIDTH = 684;
	private static final int CANVAS_HEIGHT = 661;
	//fields
	// Image in which we're going to draw
	private List<Integer> currentDataList;
	private List<Game> currentGamesList;
	List<Node> nodesToPlot;
	private Image image;
	// Graphics2D object ==> used to draw on
	public Graphics2D g2;
	private int currentX, currentY, oldX, oldY;
	public boolean penOn = false;
	public Node redNode; //last node that was red

	public DrawingPanel() {
		setDoubleBuffered(false);
		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if(penOn) {
					// save coord x,y when mouse is pressed
					oldX = e.getX();
					oldY = e.getY();
				}
				}});

		addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent e) {
				if(penOn) {
					// coord x,y when drag mouse
					currentX = e.getX();
					currentY = e.getY();

					if (g2 != null) {
						// draw line if g2 context not null
						g2.drawLine(oldX, oldY, currentX, currentY);
						// refresh draw area to repaint
						repaint();
						// store current coords x,y as olds x,y
						oldX = currentX;
						oldY = currentY;
					}}
				
				}});
	}

	protected void paintComponent(Graphics g) {
		if (image == null) {
			//image to draw null ==> we create
			image = createImage(getSize().width, getSize().height);
			g2 = (Graphics2D) image.getGraphics();
			// enable antialiasing
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			// clear draw area
			clear();
		}
		g.drawImage(image, 0, 0, null);
	}

	// now we create exposed methods
	public void clear() {
		g2.setPaint(Color.black);
		// draw black on entire draw area to clear
		g2.fillRect(0, 0, 1000, 1000);
		g2.setPaint(Color.white);
		repaint();
	}

	public void drawGraf(List<Game> gamesList, String valueToPlot) {
		List<Integer> intsToPlot = new ArrayList<>();
		switch (valueToPlot) {
		case "dragons":
			for(Game g : gamesList) {
				intsToPlot.add(g.tDragons);
			}
			break;
		case "kills":
			for(Game g : gamesList) {
				intsToPlot.add(g.tKills);
			}
			break;
		default:
			return;
		}
		
		currentDataList = intsToPlot;
		currentGamesList=gamesList;
		drawGrafLines(gamesList,intsToPlot);
		
	}

	public void drawGrafLines(List<Game> gamesList,List<Integer> intsToPlot ) {
		//list of nodes to hold each plot point and game its from
		nodesToPlot = new ArrayList<>();
		clear();//paint everything black.
		// draw x and y axis
		g2.setPaint(Color.white);
		g2.drawLine(GRAPH_LEFT, GRAPH_TOP, GRAPH_LEFT, GRAPH_BOTTOM);
		g2.drawLine(GRAPH_LEFT, GRAPH_BOTTOM, GRAPH_RIGHT, GRAPH_BOTTOM);
        
        // draw x notches
        int xNotches = intsToPlot.size();
        double xGaps = GRAPH_WIDTH/xNotches;
        double currentXNotch = GRAPH_LEFT+xGaps;
        
        // make y notches data
        int minNotches = Collections.min(intsToPlot);
        int yNotches = Collections.max(intsToPlot)-minNotches+2;
        double yGaps = GRAPH_HEIGHT/yNotches;
        double currentYNotch = GRAPH_BOTTOM-yGaps;
        
        
        int lastX=0;
        int lastY=0;
        Node preNode = null;
        for(int i =0; i< intsToPlot.size(); i++){
        	
        	//node
        	Game g = gamesList.get(i);
        	int value = intsToPlot.get(i);
        	int yPoint = (int) (GRAPH_BOTTOM - (yGaps*value)+(minNotches-2)*yGaps);
        	Node node = new Node((int) currentXNotch,yPoint, g, value);
        	nodesToPlot.add(node);
        	node.draw(g2);
        	
        	//line from node to node
        	if(lastX != 0) {
        		g2.drawLine((int)currentXNotch, yPoint, lastX, lastY);
        	}
        	if(preNode != null) {
        		node.setLastNode(preNode);
        	}
        	
        	//Notchs along bottom line
            g2.drawLine((int)currentXNotch, GRAPH_BOTTOM, (int)currentXNotch, GRAPH_BOTTOM+10);
            //data for next point
            lastX = (int)currentXNotch;
            lastY = yPoint;
            currentXNotch += xGaps;
            preNode=node;
        }
        
        // draw y notches
        for(int i = 1; i < yNotches+1; i++){
            g2.drawLine(GRAPH_LEFT-10, (int)currentYNotch, GRAPH_LEFT, (int)currentYNotch);
            g2.drawString(Integer.toString(i+minNotches-2), GRAPH_LEFT-30, (int)currentYNotch);
            currentYNotch -= yGaps;
        }
        
		this.repaint();
	}
	
	private static final double GRAPH_WIDTH = 550;
	private static final double GRAPH_HEIGHT = 525;
	private static final int GRAPH_LEFT = 50;
	private static final int GRAPH_RIGHT = 600;
	private static final int GRAPH_TOP = 75;
	private static final int GRAPH_BOTTOM = 600;
}
