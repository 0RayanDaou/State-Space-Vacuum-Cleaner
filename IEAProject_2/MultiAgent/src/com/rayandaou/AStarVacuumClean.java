package com.rayandaou;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeListener;

import org.w3c.dom.events.EventTarget;
import org.w3c.dom.events.MouseEvent;
import org.w3c.dom.views.AbstractView;

public class AStarVacuumClean extends JPanel implements ActionListener  {

	final int SCREEN_WIDTH;
	final int SCREEN_HEIGHT;
	static int UNIT_SIZE = 25;
	static int GAME_UNITS;
	static int DELAY = 500;
	int stepCount = 0;
	//placement of Vacuum Cleaner
	int x;
	int y;
	
	int temp;
	int probability [][];
	int probability2 [][];;
	int dirtCleaned;
	int dirtX;
	int dirtY;
	char direction = 'R';
	boolean running = false;
	boolean paint = false;
	private Node[][] grid;
	Timer timer = new Timer(DELAY, this);
	Random random;
	JFrame frame;
	int xDistance;
	int yDistance;
	int hCost;
	int numDirections = 0;
	char directions[];
	int count = 0;
	int dirtCount = 0;
	int gCost;
	
	public AStarVacuumClean(JFrame frame, int w, int h) {
		SCREEN_WIDTH = w;
		SCREEN_HEIGHT = h;
		random = new Random();
		
		JTextField TF1 = new JTextField();
		TF1.setFont(new Font("Arial", Font.PLAIN, 15));
		TF1.setLayout(null);
		TF1.setBounds(705, 100, 85, 40);
		TF1.setBackground(Color.WHITE);
		TF1.setEditable(true);
		TF1.setBorder(BorderFactory.createBevelBorder(0));
		add(TF1);
		JSlider js = new JSlider(10, 1000, 10);
		js.setOrientation(SwingConstants.HORIZONTAL);
		js.setValue(500);
		js.setBounds(620, 350, 170, 40);
		js.setMajorTickSpacing(10);
		add(js);
		js.addChangeListener(ChangeListener ->{
			DELAY = 1000 - js.getValue();
		});
		JButton b3 = new JButton("S/R Walls");//Set or remove walls
		JButton b4 = new JButton("S/R Dirt!");//Set or remove walls
		JButton b5 = new JButton("Set Agent!");
		JButton b6 = new JButton("Random Room");
		JButton b2 = new JButton("Resize!");
		JButton b7 = new JButton("Back!");
		b7.setFont(new Font("Arial", Font.PLAIN, 15));
		b7.setLayout(null);
		b7.setBounds(620, 400 , 170, 40);
		b7.setBackground(Color.GREEN);
		b7.setBorder(BorderFactory.createBevelBorder(0));
		b7.addActionListener(ActionEvent  -> {		
			Frames frame2 = new Frames();
			frame.dispose();
		});
		add(b7);
		b2.setFont(new Font("Arial", Font.PLAIN, 15));
		b2.setLayout(null);
		b2.setBounds(620, 100 , 80, 40);
		b2.setBackground(Color.GREEN);
		b2.setBorder(BorderFactory.createBevelBorder(0));
		b2.addActionListener(ActionEvent  -> {
			UNIT_SIZE = Integer.parseInt(TF1.getText());
			GAME_UNITS = ((SCREEN_WIDTH)/UNIT_SIZE)*((SCREEN_HEIGHT)/UNIT_SIZE);
			paint = true;
			resetWalls();
//			putWalls();
			resetDirt();
			stepCount =0;
//			putDirt();
			x=0;
			y=0;
			dirtX=0;
			dirtY=0;
			b3.setEnabled(true);
			b4.setEnabled(true);
			b5.setEnabled(true);
			b6.setEnabled(true);
			timer.start();
			repaint();
			running = false;
			//startCleaning();
		});
		add(b2);

		GAME_UNITS = ((SCREEN_WIDTH)/UNIT_SIZE)*((SCREEN_HEIGHT)/UNIT_SIZE);
		probability = new int[GAME_UNITS][GAME_UNITS];
		probability2 = new int[GAME_UNITS][GAME_UNITS];
		grid = new Node[(int)GAME_UNITS][(int)GAME_UNITS];
		setLayout(null);
		for(int x =0; x< Math.sqrt(GAME_UNITS) ; x++) {
			for (int y = 0; y < Math.sqrt(GAME_UNITS); y++) { 
				grid[x][y] =new Node(x,y,gCost, 0);
			}
		}
		this.frame = frame;
		setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
		setBackground(Color.WHITE);
		setFocusable(true);
		JTextField TF2 = new JTextField();
		TF2.setFont(new Font("Arial", Font.PLAIN, 15));
		TF2.setLayout(null);
		TF2.setBounds(705, 150, 40, 40);
		TF2.setBackground(Color.WHITE);
		TF2.setEditable(true);
		TF2.setBorder(BorderFactory.createBevelBorder(0));
		add(TF2);
		JTextField TF3 = new JTextField();
		TF3.setFont(new Font("Arial", Font.PLAIN, 15));
		TF3.setLayout(null);
		TF3.setBounds(750, 150, 40, 40);
		TF3.setBackground(Color.WHITE);
		TF3.setEditable(true);
		TF3.setBorder(BorderFactory.createBevelBorder(0));
		add(TF3);

		b3.setFont(new Font("Arial", Font.PLAIN, 15));
		b3.setLayout(null);
		b3.setBounds(620, 150, 80, 40);
		b3.setBackground(Color.GREEN);
		b3.setBorder(BorderFactory.createBevelBorder(0));
		b3.addActionListener(ActionEvent -> {
			if(!grid[Integer.parseInt(TF2.getText())][Integer.parseInt(TF3.getText())].isWalkable()) {
				grid[Integer.parseInt(TF2.getText())][Integer.parseInt(TF3.getText())].setWalkable();
			}else {
			grid[Integer.parseInt(TF2.getText())][Integer.parseInt(TF3.getText())].setWall();
			}
			repaint();
		});
		add(b3);
		
		JTextField TF4 = new JTextField();
		TF4.setFont(new Font("Arial", Font.PLAIN, 15));
		TF4.setLayout(null);
		TF4.setBounds(705, 200, 40, 40);
		TF4.setBackground(Color.WHITE);
		TF4.setEditable(true);
		TF4.setBorder(BorderFactory.createBevelBorder(0));
		add(TF4);
		JTextField TF5 = new JTextField();
		TF5.setFont(new Font("Arial", Font.PLAIN, 15));
		TF5.setLayout(null);
		TF5.setBounds(750, 200, 40, 40);
		TF5.setBackground(Color.WHITE);
		TF5.setEditable(true);
		TF5.setBorder(BorderFactory.createBevelBorder(0));
		add(TF5);
	
		b4.setFont(new Font("Arial", Font.PLAIN, 15));
		b4.setLayout(null);
		b4.setBounds(620, 200, 80, 40);
		b4.setBackground(Color.GREEN);
		b4.setBorder(BorderFactory.createBevelBorder(0));
		b4.addActionListener(ActionEvent -> {
			if(!grid[Integer.parseInt(TF4.getText())][Integer.parseInt(TF5.getText())].isClean()) {
				grid[Integer.parseInt(TF4.getText())][Integer.parseInt(TF5.getText())].Clean();
				dirtCount--;
			}else {
			grid[Integer.parseInt(TF4.getText())][Integer.parseInt(TF5.getText())].isDirt();
			dirtCount++;
			}
			
			repaint();
		});
		add(b4);
		
		JButton b1 = new JButton("Start!");
		b1.setFont(new Font("Arial", Font.PLAIN, 15));
		b1.setLayout(null);
		b1.setBounds(620, 50 , 170, 40);
		b1.setBackground(Color.GREEN);
		b1.setBorder(BorderFactory.createBevelBorder(0));
		b1.addActionListener(ActionEvent  -> {
			newDirt();
			running = true;
			b3.setEnabled(false);
			b4.setEnabled(false);
			b5.setEnabled(false);
			b6.setEnabled(false);
			
		});
		add(b1);
		
		b6.setFont(new Font("Arial", Font.PLAIN, 15));
		b6.setLayout(null);
		b6.setBounds(620, 300 , 170, 40);
		b6.setBackground(Color.GREEN);
		b6.setBorder(BorderFactory.createBevelBorder(0));
		b6.addActionListener(ActionEvent  -> {
			resetWalls();
			resetDirt();
			putWalls();
			putDirt();
		});
		add(b6);
		JTextField TF6 = new JTextField();
		TF6.setFont(new Font("Arial", Font.PLAIN, 15));
		TF6.setLayout(null);
		TF6.setBounds(705, 250, 40, 40);
		TF6.setBackground(Color.WHITE);
		TF6.setEditable(true);
		TF6.setBorder(BorderFactory.createBevelBorder(0));
		add(TF6);
		JTextField TF7 = new JTextField();
		TF7.setFont(new Font("Arial", Font.PLAIN, 15));
		TF7.setLayout(null);
		TF7.setBounds(750, 250, 40, 40);
		TF7.setBackground(Color.WHITE);
		TF7.setEditable(true);
		TF7.setBorder(BorderFactory.createBevelBorder(0));
		add(TF7);
		b5.setFont(new Font("Arial", Font.PLAIN, 15));
		b5.setLayout(null);
		b5.setBounds(620, 250, 80, 40);
		b5.setBackground(Color.GREEN);
		b5.setBorder(BorderFactory.createBevelBorder(0));
		b5.addActionListener(ActionEvent -> {
			x = Integer.parseInt(TF6.getText())*UNIT_SIZE;
			y = Integer.parseInt(TF7.getText())*UNIT_SIZE;
			repaint();
		});
		add(b5);
		timer.start();
		//2-startCleaning();
	
	}

	public void resetDirt() {
		for (int i = 0; i < Math.sqrt(GAME_UNITS); i++) {
			for (int j = 0; j < Math.sqrt(GAME_UNITS); j++) {
				probability2 [i][j]= 0;
				grid[i][j].Clean();
				dirtCount = 0;
				}
			}
		}
	
	public void putDirt() {
		for (int i = 0; i < Math.sqrt(GAME_UNITS); i++) {
			for (int j = 0; j < Math.sqrt(GAME_UNITS); j++) {
				probability2 [i][j]= random.nextInt(10);
				probability2 [0][0]= 0;
				probability2 [0][1] = 0;
				if(probability2[i][j]>7) {
					if(grid[i][j].isWalkable()) {
						grid[i][j].isDirt();
						dirtCount++;
					}
				}
			}
		}
	}
	
//	public void startCleaning() {
//		putDirt();
//		newDirt();
//		paint = true;
//		//1-running = true;
//		timer = new Timer(DELAY, this);
//		timer.start();
//	}
	
	public void putWalls() {
		for (int i = 0; i < Math.sqrt(GAME_UNITS); i++) {
			for (int j = 0; j < Math.sqrt(GAME_UNITS); j++) {
				probability [i][j]= random.nextInt(10);
				probability [0][0]= 0;
				probability [0][1] = 0;
				if(probability[i][j]>7) {
					grid[i][j].setWall();
				}
				grid[(int)Math.sqrt(GAME_UNITS)-1][(int)Math.sqrt(GAME_UNITS)-1].isWalkable();
			}
		}
	}
	
	public void resetWalls() {
		for (int i = 0; i < Math.sqrt(GAME_UNITS); i++) {
			for (int j = 0; j < Math.sqrt(GAME_UNITS); j++) {
				probability [i][j]= 0;
				grid[i][j].setWalkable();
				
			}
		}
	}
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		if (paint) {
			if(dirtCount<1) {
				running = false;
			}
			g.setColor(Color.BLACK);
			for (int i = 0; i < Math.sqrt(GAME_UNITS); i++) {
				for (int j = 0; j < Math.sqrt(GAME_UNITS); j++) {
					g.drawRect(i*UNIT_SIZE, j*UNIT_SIZE, UNIT_SIZE, UNIT_SIZE);
						if(!grid[i][j].isWalkable()) {
						g.fillRect(i*UNIT_SIZE, j*UNIT_SIZE, UNIT_SIZE, UNIT_SIZE);
					
					}
				}
			}
			g.setColor(Color.BLUE);
			for (int i = 0; i < Math.sqrt(GAME_UNITS); i++) {
				for (int j = 0; j < Math.sqrt(GAME_UNITS); j++) {
						if(!grid[i][j].isClean()) {
						g.fillOval(i*UNIT_SIZE, j*UNIT_SIZE, UNIT_SIZE, UNIT_SIZE);
					
					}
				}
			}
			g.setColor(Color.GRAY);
			if(dirtCount != 0) {
			g.fillOval(dirtX, dirtY, UNIT_SIZE, UNIT_SIZE);
			}
			g.setColor(Color.GREEN);
			g.fillRect(x, y, UNIT_SIZE, UNIT_SIZE);
			g.setColor(Color.BLACK);
			g.setColor(Color.RED);
			g.setFont(new Font("Ink Free", Font.BOLD, 40));
			FontMetrics metrics = getFontMetrics(g.getFont());
			g.drawString("Dirt Cleaned: " + dirtCleaned, (SCREEN_WIDTH - metrics.stringWidth("Dirt Cleaned: " + dirtCleaned))/2, g.getFont().getSize());
			timer.setDelay(DELAY);
		}
	}
	
	public void newDirt() {
		if(dirtCount>=1) {
			outerloop:
			for (int i = 0; i < Math.sqrt(GAME_UNITS); i++) {
				for (int j = 0; j < Math.sqrt(GAME_UNITS); j++) {
					if(!grid[i][j].isClean()) {
						dirtX = i * UNIT_SIZE;
						dirtY = j * UNIT_SIZE;
					break outerloop;
					}
				}
			}
		System.out.println(dirtX + " " + dirtY);

		List<Node> path = aStar();
		if (path == null) {
			numDirections = -1;
			System.out.println("A* is blocked!");
			return;
		}
		numDirections = path.size();
		directions = new char[numDirections];
		for (int i = 0; i < numDirections; i++) {
			directions[i] = path.get(i).getDirection();
		}
		}
		System.out.println(dirtX + " " + dirtY);
	}
	
	public void move() {
		stepCount++;
		if (numDirections != -1) {
			direction = directions[numDirections - 1];
			System.out.println(stepCount +"- " +direction);
			numDirections--;
		}
		
		switch(direction) {
		case 'U':
			y = y - UNIT_SIZE;
			break;
		case 'D':
			y = y + UNIT_SIZE;
			break;
		case 'L':
			x = x - UNIT_SIZE;
			break;
		case 'R':
			x = x + UNIT_SIZE;
			break;
		}
		System.out.println(x + ", " + y);
		if(!grid[x/UNIT_SIZE][y/UNIT_SIZE].isClean()) {
			grid[x/UNIT_SIZE][y/UNIT_SIZE].Clean();
			dirtCleaned++;
			dirtCount--;
		}
	}
	
	public void checkDirt() {
		if((x == dirtX) && (y == dirtY)) {		
			//running = true;
			if(dirtCount>=1) {
				System.out.println("New Dirt");
				newDirt();
				System.out.println("New Dirt");
			}else {
				System.out.println("Cleaning is done!");
				System.out.println("Steps taken = "+ stepCount);
				running = false;
			}
		}
	}
	
	public void checkCollisions() {
		if(x < 0) {
			running = false;
		}
		if(x >= SCREEN_WIDTH) {
			running = false;
		}
		if (y < 0) {
			running = false;
		}
		if (y >= SCREEN_HEIGHT) {
			running = false;
		}
		if (!running) {
			timer.stop();
			System.out.println("Stop");
		}
	}
	
	public void actionPerformed(ActionEvent event) {
		if(running) {
			if (numDirections == -1) {
				chooseDirection();
				return;
			}
			else {
			move();
			checkDirt();
			checkCollisions();
			}
		}
		repaint();
	}
	
	private void chooseDirection() {
		switch (direction) {
		case 'U':
			direction = 'D';
			break;
		case 'D':
			direction = 'U';
			break;
		case 'R':
			direction = 'L';
			break;
		case 'L':
			direction = 'R';
			break;
		}
		List<Node> path = aStar();
		if (path == null) {
			numDirections = -1;
			return;
		}
		numDirections = path.size();
		directions = new char[numDirections];
		for (int i = 0; i < numDirections; i++) {
			directions[i] = path.get(i).getDirection();
		}
		
	}

	private boolean isBlocked( char d, int x, int y) { 
		if (d == 'R' ) {
			if (x >= SCREEN_WIDTH) {
				return true;
			}
			if(x<0) {
				return true;
			}
			if(!grid[x/UNIT_SIZE][y/UNIT_SIZE].isWalkable()) {
				return true;
			}

		} else if (d == 'L') {
			if (x < 0) {
				return true;
			}
			if(x>SCREEN_WIDTH) {
				return true;
			}
			if(!grid[x/UNIT_SIZE][y/UNIT_SIZE].isWalkable()) {
				return true;
			}


		} else if (d == 'D') {
			if (y >= SCREEN_WIDTH) {
				return true;
			}
			if(y<0) {
				return true;
			}
			if(!grid[x/UNIT_SIZE][y/UNIT_SIZE].isWalkable()) {
				return true;
			}

		} else if(d == 'U') {
			if (y < 0) {
				return true;
			}
			if(y>SCREEN_WIDTH) {
				return true;
			}
			if(!grid[x/UNIT_SIZE][y/UNIT_SIZE].isWalkable()) {
				return true;
			}

		}
		return false;
	}
	
	private List<Node> aStar() {

		List<Node> parents = new ArrayList<Node>();
		PriorityQueue<Node> open = new PriorityQueue<Node>();
		List<Node> closed = new ArrayList<Node>();
		
		gCost = 0;
		int gCost2 ;
		Node startNode = new Node(x, y, gCost, findHCost(x, y));
		startNode.setDirection(direction);
		Node goalNode = new Node(dirtX, dirtY, findHCost(x, y), 0);

		open.add(startNode);
		
		while (!open.isEmpty()) { 
			
			//I set my start node to be traversed(closed
			Node current = open.poll();
			current.setVis();
			current.close();
			closed.add(current);
			
			
			//if my count exceeds the Grid units then i passed over all possible traversable Tiles
			//and i couldn't find a path to the dirt

			//if my destination is my current node 
			//generate new dirt and look for the path
			if (current.same(goalNode)) {
				//backtrack and create parents list
				//in order to know the path
				boolean finished = false;
				Node n = current;
				while (!finished) {
					parents.add(n);
					n = n.getParent();
					if (n.getParent() == null) {
						finished = true;
					}
				}
				return parents;
			}
			
			// set neighbours cost
			for (int i = 0; i < 4; i++) {
				
				if (i == 0 ) {
					gCost = 10; // if current direction
					gCost2= 1;
				} else {
					gCost = 10; // if change direction, costs more
					gCost2 = 1;
				}
								//check neighbors cost and path
				boolean exists = false;
				//badak passed bil grid kermel heik 7ateita for every node
				//then 7oto bil isBlocked method which blocks the loop li 3a 
				//betsir
				Node n;
				//if i=0 stay in same direction
				//check if this direction is blocked
				//if blocked i continue and check other directions
				//if not blocked this node exists
				if (i == 0) {
					if (current.getDirection() == 'R') { // Continue Right
						// CHECK IF BLOCKED
						if (!isBlocked(current.getDirection(), current.getxAxis() + UNIT_SIZE, current.getyAxis())) {
							//if not blocked
							if(grid[(current.getxAxis()+UNIT_SIZE)/UNIT_SIZE][current.getyAxis()/UNIT_SIZE].isClean()) {
								n = new Node(current.getxAxis() + UNIT_SIZE, current.getyAxis(), gCost, findHCost(current.getxAxis(), current.getyAxis()));
							}else {
								n = new Node(current.getxAxis() + UNIT_SIZE, current.getyAxis(), gCost2, findHCost(current.getxAxis(), current.getyAxis()));
							}
								if (open.contains(n) || closed.contains(n) || parents.contains(n)) {
								exists = true;
							}
						} else {
							continue;
						}
					} else if (current.getDirection() == 'L') { // Continue Left
						if (!isBlocked(current.getDirection(), current.getxAxis() - UNIT_SIZE, current.getyAxis())) {
							if(grid[(current.getxAxis()-UNIT_SIZE)/UNIT_SIZE][current.getyAxis()/UNIT_SIZE].isClean()) {
								n = new Node(current.getxAxis() - UNIT_SIZE, current.getyAxis(), gCost , findHCost(current.getxAxis(), current.getyAxis()));
							}else {
								n = new Node(current.getxAxis() - UNIT_SIZE, current.getyAxis(), gCost2 , findHCost(current.getxAxis(), current.getyAxis()));
							}
								if (open.contains(n) || closed.contains(n) || parents.contains(n)) {
								exists = true;
							}
						} else {
							continue;
						}
					} else if (current.getDirection() == 'D') { // Continue Down
						if (!isBlocked(current.getDirection(), current.getxAxis(), current.getyAxis() + UNIT_SIZE)) {
							if(grid[(current.getxAxis())/UNIT_SIZE][(current.getyAxis()+UNIT_SIZE)/UNIT_SIZE].isClean()) {
								n = new Node(current.getxAxis(), current.getyAxis() + UNIT_SIZE, gCost , findHCost(current.getxAxis(), current.getyAxis()));
							}else {
								n = new Node(current.getxAxis(), current.getyAxis() + UNIT_SIZE, gCost2 , findHCost(current.getxAxis(), current.getyAxis()));
							}
							if (open.contains(n) || closed.contains(n) || parents.contains(n)) {
								exists = true;
							}
						} else {
							continue;
						}
					} else { // Continue Up
						if(!isBlocked(current.getDirection(), current.getxAxis(), current.getyAxis() - UNIT_SIZE)) {
							if(grid[(current.getxAxis())/UNIT_SIZE][(current.getyAxis()-UNIT_SIZE)/UNIT_SIZE].isClean()) {	
								n = new Node(current.getxAxis(), current.getyAxis() - UNIT_SIZE, gCost , findHCost(current.getxAxis(), current.getyAxis()));
							}else {
								n = new Node(current.getxAxis(), current.getyAxis() - UNIT_SIZE, gCost2 , findHCost(current.getxAxis(), current.getyAxis()));
							}
							if (open.contains(n) || closed.contains(n) || parents.contains(n)) {
								exists = true;
							}
						} else {
							continue;
						}
					}
				} else if (i == 1) {
					if (current.getDirection() == 'R') { // Turn Down
						if(!isBlocked('D', current.getxAxis(), current.getyAxis() + UNIT_SIZE)) {
							if(grid[(current.getxAxis())/UNIT_SIZE][(current.getyAxis()+UNIT_SIZE)/UNIT_SIZE].isClean()) {
								n = new Node(current.getxAxis(), current.getyAxis() + UNIT_SIZE, gCost , findHCost(current.getxAxis(), current.getyAxis()));
							}else {
								n = new Node(current.getxAxis(), current.getyAxis() + UNIT_SIZE, gCost2 , findHCost(current.getxAxis(), current.getyAxis()));
							}
							if (open.contains(n) || closed.contains(n) || parents.contains(n)) {
								exists = true;
							}
						} else {
							continue;
						}
					} else if (current.getDirection() == 'L') { // Turn Up
						if(!isBlocked('U', current.getxAxis(), current.getyAxis() - UNIT_SIZE)) {
							if(grid[(current.getxAxis())/UNIT_SIZE][(current.getyAxis()-UNIT_SIZE)/UNIT_SIZE].isClean()) {	
								n = new Node(current.getxAxis(), current.getyAxis() - UNIT_SIZE, gCost , findHCost(current.getxAxis(), current.getyAxis()));
							}else {
								n = new Node(current.getxAxis(), current.getyAxis() - UNIT_SIZE, gCost2 , findHCost(current.getxAxis(), current.getyAxis()));
							}
							if (open.contains(n) || closed.contains(n) || parents.contains(n)) {
								exists = true;
							}
						} else {
							continue;
						}
					} else if (current.getDirection() == 'D') { // Turn Left
						if(!isBlocked('L', current.getxAxis() - UNIT_SIZE, current.getyAxis())) {
							if(grid[(current.getxAxis()-UNIT_SIZE)/UNIT_SIZE][current.getyAxis()/UNIT_SIZE].isClean()) {
								n = new Node(current.getxAxis() - UNIT_SIZE, current.getyAxis(), gCost , findHCost(current.getxAxis(), current.getyAxis()));
							}else {
								n = new Node(current.getxAxis() - UNIT_SIZE, current.getyAxis(), gCost2 , findHCost(current.getxAxis(), current.getyAxis()));
							}
							if (open.contains(n) || closed.contains(n) || parents.contains(n)) {
								exists = true;
							}
						} else {
							continue;
						}
					} else { // Turn Right
						if(!isBlocked('R', current.getxAxis() + UNIT_SIZE, current.getyAxis())) {
							if(grid[(current.getxAxis()+UNIT_SIZE)/UNIT_SIZE][current.getyAxis()/UNIT_SIZE].isClean()) {
								n = new Node(current.getxAxis() + UNIT_SIZE, current.getyAxis(), gCost, findHCost(current.getxAxis(), current.getyAxis()));
							}else {
								n = new Node(current.getxAxis() + UNIT_SIZE, current.getyAxis(), gCost2, findHCost(current.getxAxis(), current.getyAxis()));
							}
							if (open.contains(n) || closed.contains(n) || parents.contains(n)) {
								exists = true;
							}
						} else {
							continue;
						}
					}
				} else {
					if (current.getDirection() == 'R') { // Turn Up
						if(!isBlocked('U', current.getxAxis(), current.getyAxis() - UNIT_SIZE)) {
							if(grid[(current.getxAxis())/UNIT_SIZE][(current.getyAxis()-UNIT_SIZE)/UNIT_SIZE].isClean()) {	
								n = new Node(current.getxAxis(), current.getyAxis() - UNIT_SIZE, gCost , findHCost(current.getxAxis(), current.getyAxis()));
							}else {
								n = new Node(current.getxAxis(), current.getyAxis() - UNIT_SIZE, gCost2 , findHCost(current.getxAxis(), current.getyAxis()));
							}
							if (open.contains(n) || closed.contains(n) || parents.contains(n)) {
								exists = true;
							}
						} else {
							continue;
						}
					} else if (current.getDirection() == 'L') { // Turn Down
						if(!isBlocked('D', current.getxAxis(), current.getyAxis() + UNIT_SIZE)) {
							if(grid[(current.getxAxis())/UNIT_SIZE][(current.getyAxis()+UNIT_SIZE)/UNIT_SIZE].isClean()) {
								n = new Node(current.getxAxis(), current.getyAxis() + UNIT_SIZE, gCost , findHCost(current.getxAxis(), current.getyAxis()));
							}else {
								n = new Node(current.getxAxis(), current.getyAxis() + UNIT_SIZE, gCost2 , findHCost(current.getxAxis(), current.getyAxis()));
							}
							if (open.contains(n) || closed.contains(n) || parents.contains(n)) {
								exists = true;
							}
						} else {
							continue;
						}
					} else if (current.getDirection() == 'D') { // Turn Right
						if(!isBlocked('R', current.getxAxis() + UNIT_SIZE, current.getyAxis())) {
							if(grid[(current.getxAxis()+UNIT_SIZE)/UNIT_SIZE][current.getyAxis()/UNIT_SIZE].isClean()) {
								n = new Node(current.getxAxis() + UNIT_SIZE, current.getyAxis(), gCost, findHCost(current.getxAxis(), current.getyAxis()));
							}else {
								n = new Node(current.getxAxis() + UNIT_SIZE, current.getyAxis(), gCost2, findHCost(current.getxAxis(), current.getyAxis()));
							}
							if (open.contains(n) || closed.contains(n) || parents.contains(n)) {
								exists = true;
							}
						} else {
							continue;
						}
					} else { // Turn Left
						if(!isBlocked('L', current.getxAxis() - UNIT_SIZE, current.getyAxis())) {
							if(grid[(current.getxAxis()-UNIT_SIZE)/UNIT_SIZE][current.getyAxis()/UNIT_SIZE].isClean()) {
								n = new Node(current.getxAxis() - UNIT_SIZE, current.getyAxis(), gCost , findHCost(current.getxAxis(), current.getyAxis()));
							}else {
								n = new Node(current.getxAxis() - UNIT_SIZE, current.getyAxis(), gCost2 , findHCost(current.getxAxis(), current.getyAxis()));
							}
							if (open.contains(n) || closed.contains(n) || parents.contains(n)) {
								exists = true;
							}
						} else {
							continue;
						}
					}
				}			
				if (exists && n.isClosed()) {
					continue;
				}
				if(!isBlocked(n.getDirection(), n.getxAxis(), n.getyAxis())) {
				if (n.getFCost() <= current.getFCost() || !open.contains(n)) {
					n.setParent(current);
					if (!open.contains(n)) {
						n.setgCost(n.getParent().getgCost() + n.getgCost());
						open.add(n);
						n.setVis();
					}
				}
				}
			}
		}
		return null;
	}
	//Heuristic Cost
	private int findHCost(int xAxis, int yAxis) {
		hCost = 0;
		xDistance = Math.abs((dirtX - xAxis) / UNIT_SIZE);
		yDistance = Math.abs((dirtY - yAxis) / UNIT_SIZE);
		hCost += (xDistance * 10) + (yDistance * 10);
		return hCost;
	}
	
}

