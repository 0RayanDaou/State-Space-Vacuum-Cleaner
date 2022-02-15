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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;

import javax.swing.*;


public class MultiAgentSim extends JPanel implements ActionListener  {
	final int SCREEN_WIDTH;
	final int SCREEN_HEIGHT;
	static int UNIT_SIZE = 25;
	static int GAME_UNITS;
	static int DELAY = 500;
	int stepCount = 0;
	int stepCount2 = 0;
	int targetCount = 0;
	boolean follow = false;
	int count3 = 0;
	//placement of Vacuum Cleaner
	int x;
	int y;
	//placement of Dirt Producer
	int a;
	int b;
	//using a queue to set the targets for the dirt producer
	Queue<Node> targets = new LinkedList<>();
	
	//Use the lists for paths and get the last one as a reference node to get to the dirt producer
	boolean dirtyingFin = false;
	int rayan = 0;
	int mario = 0;
	boolean cleaningFin = false;
	int temp;
	int probability [][];
	int probability2 [][];;
	int dirtCleaned;
	int batteryVC = 1000;
	int batteryDP = 1000;
	//dirt target for the vacuum cleaner
	int dirtX;
	int dirtY;
	//Node target for the dirt producer
	int targetX;
	int targetY;
	//initial direction of vacuum cleaner
	char direction = 'R';
	//initial direction of dirt producer
	char direction2 = 'L';
	boolean running = false;
	boolean paint = false;
	private Node[][] grid;
	Timer timer = new Timer(DELAY, this);
	Random random;
	JFrame frame;
	int xDistance;
	int yDistance;
	int hCost;
	//number of directions in path of vacuum cleaner
	int numDirections = 0;
	//number of directions in path of dirt producer
	int numDirections2 = 0;
	//array that will store the directions the vacuum cleaner will follow
	char directions[];
	//array that will store the directions the dirt producer will follow
	char directions2[];
	int count = 0;
	int dirtCount = 0;
	int gCost;
	JCheckBox cb1 = new JCheckBox("Neareast");
	public MultiAgentSim(JFrame frame, int w, int h) {
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
		js.setBounds(620, 400, 170, 40);
		js.setMajorTickSpacing(10);
		add(js);
		js.addChangeListener(ChangeListener ->{
			DELAY = 1000 - js.getValue();
		});
		JButton b7 = new JButton("Back!");
		b7.setFont(new Font("Arial", Font.PLAIN, 15));
		b7.setLayout(null);
		b7.setBounds(620, 450 , 170, 40);
		b7.setBackground(Color.GREEN);
		b7.setBorder(BorderFactory.createBevelBorder(0));
		b7.addActionListener(ActionEvent  -> {
			Frames frame2 = new Frames();
			frame.dispose();
		});
		add(b7);
		JButton b3 = new JButton("S/R Walls");//Set or remove walls
		JButton b4 = new JButton("S/R Dirt!");//Set or remove walls
		JButton b5 = new JButton("Set Agent!");
		JButton b6 = new JButton("Random Room");
		JButton b8 = new JButton("Same Room");
		JTextField TF8 = new JTextField();
		TF8.setFont(new Font("Arial", Font.PLAIN, 15));
		TF8.setLayout(null);
		TF8.setBounds(705, 250, 40, 40);
		TF8.setBackground(Color.WHITE);
		TF8.setEditable(true);
		TF8.setBorder(BorderFactory.createBevelBorder(0));
		add(TF8);
		JTextField TF9 = new JTextField();
		TF9.setFont(new Font("Arial", Font.PLAIN, 15));
		TF9.setLayout(null);
		TF9.setBounds(750, 250, 40, 40);
		TF9.setBackground(Color.WHITE);
		TF9.setEditable(true);
		TF9.setBorder(BorderFactory.createBevelBorder(0));
		add(TF9);
		JButton b10 = new JButton("S/R Target!");
		b10.setFont(new Font("Arial", Font.PLAIN, 15));
		b10.setLayout(null);
		b10.setBounds(620, 250 , 80, 40);
		b10.setBackground(Color.GREEN);
		b10.setBorder(BorderFactory.createBevelBorder(0));
		b10.addActionListener(ActionEvent -> {
			if(!grid[Integer.parseInt(TF8.getText())][Integer.parseInt(TF9.getText())].isTarget()) {
				grid[Integer.parseInt(TF8.getText())][Integer.parseInt(TF9.getText())].setTarget();
				probability2[Integer.parseInt(TF8.getText())][Integer.parseInt(TF9.getText())] = 8;
				targetCount++;
			}else {
			grid[Integer.parseInt(TF8.getText())][Integer.parseInt(TF9.getText())].notTarget();
			probability2[Integer.parseInt(TF8.getText())][Integer.parseInt(TF9.getText())] = 0;
			targetCount--;
			}
			repaint();
		});
		add(b10);
		cb1.setFont(new Font("Arial", Font.PLAIN, 15));
		cb1.setLayout(null);
		cb1.setBackground(Color.GREEN);
		cb1.setBounds(620, 550, 170, 40);
		cb1.setBorder(BorderFactory.createBevelBorder(0));
		cb1.addActionListener(ActionEvent-> {
			if(cb1.isSelected()) {
				follow = true;
			}else {
				follow = false;
			}
		});
		add(cb1);
		JButton b2 = new JButton("Resize!");
		b2.setFont(new Font("Arial", Font.PLAIN, 15));
		b2.setLayout(null);
		b2.setBounds(620, 100 , 80, 40);
		b2.setBackground(Color.GREEN);
		b2.setBorder(BorderFactory.createBevelBorder(0));
		b2.addActionListener(ActionEvent  -> {
			UNIT_SIZE = Integer.parseInt(TF1.getText());
			GAME_UNITS = ((SCREEN_WIDTH)/UNIT_SIZE)*((SCREEN_HEIGHT)/UNIT_SIZE);
			if(75>UNIT_SIZE && UNIT_SIZE>=50) {
			batteryDP = 1000;
			batteryVC = 1000;
			}else if(UNIT_SIZE>=25 && UNIT_SIZE<50) {
				batteryDP = 10000;
				batteryVC = 10000;
			}else if(UNIT_SIZE>=75 && UNIT_SIZE <=100) {
				batteryDP = 500;
				batteryVC = 500;
			}else {
				batteryDP = 1000;
				batteryVC = 1000;
			}
			
			probability = new int[(int)Math.sqrt(GAME_UNITS)][(int)Math.sqrt(GAME_UNITS)];
			probability2 = new int[(int)Math.sqrt(GAME_UNITS)][(int)Math.sqrt(GAME_UNITS)];
			grid = new Node[(int)Math.sqrt(GAME_UNITS)][(int)Math.sqrt(GAME_UNITS)];
			for(int x =0; x< Math.sqrt(GAME_UNITS) ; x++) {
				for (int y = 0; y < Math.sqrt(GAME_UNITS); y++) {
					probability2 [x][y]= random.nextInt(10);
					probability2 [0][0]= 0;
					probability2 [(int)Math.sqrt(GAME_UNITS) - 1][(int)Math.sqrt(GAME_UNITS) - 1] = 0;
					probability [x][y]= random.nextInt(10);
					probability [0][0]= 0;
					probability [0][1] = 0;
					grid[x][y] =new Node(x,y,gCost, 0);
				}
			}
			paint = true;
			resetWalls();
			resetDirt();
			resetTarget();
			stepCount = 0;
			stepCount2 = 0;
			dirtCleaned = 0;
			
			a= SCREEN_WIDTH - UNIT_SIZE;
			b= SCREEN_HEIGHT - UNIT_SIZE;
			
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
		});
		add(b2);
		setLayout(null);
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
				probability[Integer.parseInt(TF2.getText())][Integer.parseInt(TF3.getText())] = 0;
			}else {
			grid[Integer.parseInt(TF2.getText())][Integer.parseInt(TF3.getText())].setWall();
			probability[Integer.parseInt(TF2.getText())][Integer.parseInt(TF3.getText())] = 8;
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
			newTarget();
			newDirt(a, b);
			running = true;
			b3.setEnabled(false);
			b4.setEnabled(false);
			b5.setEnabled(false);
			b6.setEnabled(false);
			
		});
		add(b1);
		
		b6.setFont(new Font("Arial", Font.PLAIN, 15));
		b6.setLayout(null);
		b6.setBounds(620, 350 , 170, 40);
		b6.setBackground(Color.GREEN);
		b6.setBorder(BorderFactory.createBevelBorder(0));
		b6.addActionListener(ActionEvent  -> {
			for(int x =0; x< Math.sqrt(GAME_UNITS) ; x++) {
				for (int y = 0; y < Math.sqrt(GAME_UNITS); y++) {
					probability2 [x][y]= random.nextInt(10);
					probability2 [0][0]= 0;
					probability2 [(int)Math.sqrt(GAME_UNITS) - 1][(int)Math.sqrt(GAME_UNITS) - 1] = 0;
					probability [x][y]= random.nextInt(10);
					probability [0][0]= 0;
					probability [0][1] = 0;
				}
			}
			resetWalls();
			resetDirt();
			resetTarget();
			putWalls();
			putTarget();
			repaint();
		});
		add(b6);
		b8.setFont(new Font("Arial", Font.PLAIN, 15));
		b8.setLayout(null);
		b8.setBounds(620, 500, 170, 40);
		b8.setBackground(Color.GREEN);
		b8.setBorder(BorderFactory.createBevelBorder(0));
		b8.addActionListener(ActionEvent  -> {
			a= SCREEN_WIDTH - UNIT_SIZE;
			b= SCREEN_HEIGHT - UNIT_SIZE;
			
			x=0;
			y=0;
			dirtCleaned = 0;
			batteryDP = 1000;
			batteryVC = 1000;
			stepCount = 0;
			stepCount2 = 0;
			resetWalls();
			resetDirt();
			resetTarget();
			putWalls2();
			putTarget2();
			repaint();
		});
		add(b8);
		JTextField TF6 = new JTextField();
		TF6.setFont(new Font("Arial", Font.PLAIN, 15));
		TF6.setLayout(null);
		TF6.setBounds(705, 300, 40, 40);
		TF6.setBackground(Color.WHITE);
		TF6.setEditable(true);
		TF6.setBorder(BorderFactory.createBevelBorder(0));
		add(TF6);
		JTextField TF7 = new JTextField();
		TF7.setFont(new Font("Arial", Font.PLAIN, 15));
		TF7.setLayout(null);
		TF7.setBounds(750, 300, 40, 40);
		TF7.setBackground(Color.WHITE);
		TF7.setEditable(true);
		TF7.setBorder(BorderFactory.createBevelBorder(0));
		add(TF7);
		b5.setFont(new Font("Arial", Font.PLAIN, 15));
		b5.setLayout(null);
		b5.setBounds(620, 300, 80, 40);
		b5.setBackground(Color.GREEN);
		b5.setBorder(BorderFactory.createBevelBorder(0));
		b5.addActionListener(ActionEvent -> {
			x = Integer.parseInt(TF6.getText())*UNIT_SIZE;
			y = Integer.parseInt(TF7.getText())*UNIT_SIZE;
			repaint();
		});
		add(b5);
		timer.start();
	
	}
	public void resetTarget() {
		for (int i = 0; i < Math.sqrt(GAME_UNITS); i++) {
			for (int j = 0; j < Math.sqrt(GAME_UNITS); j++) {
//				probability2 [i][j]= 0;
				grid[i][j].notTarget();
				targetCount = 0;
				}
			}
	}
	public void resetDirt() {
		for (int i = 0; i < Math.sqrt(GAME_UNITS); i++) {
			for (int j = 0; j < Math.sqrt(GAME_UNITS); j++) {
//				probability2 [i][j]= 0;
				grid[i][j].Clean();
				dirtCount = 0;
				}
			}
		}
	
	//in order to set targets
	public void putTarget() {
		for (int i = 0; i < Math.sqrt(GAME_UNITS); i++) {
			for (int j = 0; j < Math.sqrt(GAME_UNITS); j++) {

				if(probability2[i][j]>7) {
					if(grid[i][j].isWalkable() && grid[i][j].isClean()) {
						grid[i][j].setTarget();
						targetCount++;
					}
				}
			}
		}
	temp = targetCount;
	}
	public void putTarget2() {
		for (int i = 0; i < Math.sqrt(GAME_UNITS); i++) {
			for (int j = 0; j < Math.sqrt(GAME_UNITS); j++) {
				if(probability2[i][j]>7) {
					if(grid[i][j].isWalkable() && grid[i][j].isClean()) {
						grid[i][j].setTarget();
						targetCount++;
					}
				}
			}
		}
	temp = targetCount;
	}
	
	public void putWalls() {
		for (int i = 0; i < Math.sqrt(GAME_UNITS); i++) {
			for (int j = 0; j < Math.sqrt(GAME_UNITS); j++) {
				if(probability[i][j]>7) {
					grid[i][j].setWall();
				}
				grid[(int)Math.sqrt(GAME_UNITS)-1][(int)Math.sqrt(GAME_UNITS)-1].isWalkable();
			}
		}
	}
	public void putWalls2() {
		for (int i = 0; i < Math.sqrt(GAME_UNITS); i++) {
			for (int j = 0; j < Math.sqrt(GAME_UNITS); j++) {
				if(probability[i][j]>7) {
					grid[i][j].setWall();
				}
				grid[(int)Math.sqrt(GAME_UNITS)-1][(int)Math.sqrt(GAME_UNITS)-1].isWalkable();
			}
		}
	}
	public void checkCollisions() {
		if(a + UNIT_SIZE == x && b == y ) {
			System.err.println("Collision!");
			chooseDirection();
			chooseDirection2();
		
		}
		if(a - UNIT_SIZE == x && b == y) {
			System.err.println("Collision!");
			chooseDirection();
			chooseDirection2();
		}
		if(a == x && b -UNIT_SIZE == y) {
			System.err.println("Collision!");
			chooseDirection();
			chooseDirection2();
		}
		if (a == x && b + UNIT_SIZE == y) {
			System.err.println("Collision!");
			chooseDirection();
			chooseDirection2();
		}
	}
	
	public void resetWalls() {
		for (int i = 0; i < Math.sqrt(GAME_UNITS); i++) {
			for (int j = 0; j < Math.sqrt(GAME_UNITS); j++) {
//				probability [i][j]= 0;
				grid[i][j].setWalkable();
				
			}
		}
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		if (paint) {
//			if(dirtCount<1) {
//				running = false;
//			}
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
						if(grid[i][j].isTarget()) {
						g.fillOval(i*UNIT_SIZE, j*UNIT_SIZE, UNIT_SIZE, UNIT_SIZE);
					
					}
				}
			}
			g.setColor(Color.GRAY);
			for (int i = 0; i < Math.sqrt(GAME_UNITS); i++) {
				for (int j = 0; j < Math.sqrt(GAME_UNITS); j++) {
						if(!grid[i][j].isClean()) {
						g.fillOval(i*UNIT_SIZE, j*UNIT_SIZE, UNIT_SIZE, UNIT_SIZE);
					
					}
				}
			}
			g.setColor(Color.YELLOW);
			g.fillOval(targetX, targetY, UNIT_SIZE, UNIT_SIZE);
			g.setColor(Color.ORANGE);
			g.fillRect(a, b, UNIT_SIZE, UNIT_SIZE);
			g.setColor(Color.GREEN);
			g.fillRect(x, y, UNIT_SIZE, UNIT_SIZE);
			g.setColor(Color.RED);
			g.setFont(new Font("Ink Free", Font.BOLD, 20));
			FontMetrics metrics = getFontMetrics(g.getFont());
			g.drawString("Dirt Cleaned: " + dirtCleaned, 20 ,620);
			timer.setDelay(DELAY);
			g.drawString("Battery of vacuum cleaner: " + batteryVC, 20, 650 );
			g.drawString("Battery of dirt producer: " + batteryDP, 20, 680);
			g.drawString("Targets remaining: " + targetCount, 20, 710);
			g.drawString("Dirt Producer steps taken: " + stepCount, 400, 620);
			g.drawString("Vacuum Cleaner steps taken: "+ stepCount2, 400, 650);

			g.setColor(Color.RED);
			g.setFont(new Font("Ink Free", Font.BOLD, 20));
			g.drawString("After finishing dirtying: " + mario, 400, 680);
			

			g.setColor(Color.RED);
			g.setFont(new Font("Ink Free", Font.BOLD, 20));
			g.drawString("After finishing cleaning: " + rayan, 400, 710);
			
		}
	}
	
	public void newDirt(int m, int n) {
		int temp = 100000;
		if(dirtCount == 0) {
			if(targetCount == 0) {
			rayan = stepCount;
			}
		dirtX = m;
		dirtY = n;
			System.out.println("New dirt!");
		System.out.println(dirtX + " " + dirtY);

		}
		else {
				for (int i = 0; i < Math.sqrt(GAME_UNITS); i++) {
					for (int j = 0; j < Math.sqrt(GAME_UNITS); j++) {
						if(!grid[i][j].isClean()) {
							int temp0 = getDistance(x/UNIT_SIZE, y/UNIT_SIZE, i, j);
								if(temp0<temp) {
									temp = temp0;
										dirtX = i * UNIT_SIZE;
										dirtY = j * UNIT_SIZE;
							}

						}
					}
			}
	
			System.out.println("New dirt!");
		System.out.println(dirtX + " " + dirtY);
		}
		List<Node> path = aStar(dirtX, dirtY);
		if (path == null) {
			numDirections = -1;
			System.out.println("Vacuum Cleaner is Blocked");
			return;
		}
		numDirections = path.size();
		directions = new char[numDirections];
		for (int i = 0; i < numDirections; i++) {
			directions[i] = path.get(i).getDirection();
		}
	}
	
	//sets a new target for the dirt producer
	public void newTarget() {
		int r = 0;
		int m = 0;
		//targets are the corners of the grid
		//no walls should be there
		//no agent and no dirt
		if(targetCount == 0) {
			mario = stepCount2;
			targetX = ((int)Math.sqrt(GAME_UNITS)-1) * UNIT_SIZE;
			targetY = ((int)Math.sqrt(GAME_UNITS)-1) * UNIT_SIZE;
		}else {
			int temp = 100000;
			if(cb1.isSelected()) {
		System.err.println(targetCount);
		for (int i =0 ; i < Math.sqrt(GAME_UNITS); i++) {
			for (int j = 0; j < Math.sqrt(GAME_UNITS); j++) {
				if(grid[i][j].isTarget()) {
					int temp0 = getDistance(a/UNIT_SIZE, b/UNIT_SIZE, i, j);
						if(temp0<temp) {
							temp = temp0;
								r = i;
								m = j; 
					}
				}
			}
		}
		}else{
			int temp00 = 0;
			for (int i =0; i < Math.sqrt(GAME_UNITS); i++) {
				for (int j = 0; j < Math.sqrt(GAME_UNITS); j++) {
					if(grid[i][j].isTarget()) {
						int temp01 = getDistance(x/UNIT_SIZE, y/UNIT_SIZE, i, j);
							if(temp01>temp00) {
								temp00 = temp01;
								r = i;
								m = j;
						}
					}
				}
			}
		}
		targetX = r*UNIT_SIZE;
		targetY = m*UNIT_SIZE;
		
		
		}
		System.out.println("New Target!");
		System.out.println(targetX + " " + targetY);
		
		List<Node> path2 = aStarD(targetX, targetY);
		if (path2 == null) {
			numDirections2 = -1;
			System.out.println("Dirt Producer is Blocked");
			return;
		}
		numDirections2 = path2.size();
		directions2 = new char[numDirections2];
		for (int i = 0; i < numDirections2; i++) {
			directions2[i] = path2.get(i).getDirection();
		}
				
	
	}
	public void move() {
		stepCount++;
		if (numDirections != -1) {
			direction = directions[numDirections - 1];
//			System.out.println(stepCount +"- " +direction);
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
//		System.out.println(x + ", " + y);
		if(!grid[x/UNIT_SIZE][y/UNIT_SIZE].isClean()) {
			grid[x/UNIT_SIZE][y/UNIT_SIZE].Clean();
			dirtCleaned++;
			dirtCount--;
			batteryVC = batteryVC-25;
		}
		batteryVC--;
	}

	//moves the dirt producer-done
	public void move2() {
		stepCount2++;
		if (numDirections2 != -1) {
			direction2 = directions2[numDirections2 - 1];
//			System.out.println(stepCount2 +"- " +direction2);
			numDirections2--;
		}
		switch(direction2) {
		case 'U':
			b = b - UNIT_SIZE;
			break;
		case 'D':
			b = b + UNIT_SIZE;
			break;
		case 'L':
			a = a - UNIT_SIZE;
			break;
		case 'R':
			a = a + UNIT_SIZE;
			break;
		}
		if(grid[a/UNIT_SIZE][b/UNIT_SIZE].isTarget()) {
			grid[a/UNIT_SIZE][b/UNIT_SIZE].notTarget();
			grid[a/UNIT_SIZE][b/UNIT_SIZE].isDirt();
			batteryDP = batteryDP-25;
			targetCount--;
			dirtCount++;
		}
		if((temp - targetCount) == 1) {
			newDirt(a,b);
		}
		batteryDP--;
	}
	public void checkTarget() {
		if((a == targetX) && (b == targetY)) {		
			//running = true;
			if(targetCount>=1) {
				newTarget();
			}else {
				if(a != ((int)Math.sqrt(GAME_UNITS)-1) * UNIT_SIZE || b != ((int)Math.sqrt(GAME_UNITS)-1) * UNIT_SIZE)
				{
					System.out.println("New target ya baby");
					newTarget();
				}else {
				if(numDirections2 != -1) {
				System.out.println("Dirtying is done!");
				System.out.println("Steps taken = "+ stepCount);
				System.out.println("Stop dirtying!");
				}
				}
			}
				
		}
	}
	public void checkDirt() {
		if((x == dirtX) && (y == dirtY)) {		
			//running = true;
			if(dirtCount>=1) {
//				System.out.println("New Dirt");
				newDirt(a, b);
			}else {
				if(targetCount != 0) {
//					System.out.println("New Dirt");
					newDirt(a, b);
				}else {
					if( x!=0 && y != 0) {
						newDirt(0,0);
					}
					else {
//				System.out.println("Cleaning is done!");
//				System.out.println("Steps taken = "+ stepCount);
				running = false;
					}
				}
			}
		}
	}
	
//	public void checkDirt() {
//		if((x == dirtX) && (y == dirtY)) {		
//			//running = true;
//			if(dirtCount>=1) {
//				System.out.println("New Dirt");
//				newDirt();
//				System.out.println("New Dirt");
//			}else {
//				System.out.println("Cleaning is done!");
//				System.out.println("Steps taken = "+ stepCount);
//				running = false;
//			}
//		}
//	}

	public void actionPerformed(ActionEvent event) {
		if(running) {
				if(targetCount != 0) {
				if (numDirections == -1) {
					chooseDirection();
					return;
				}
				else {
					move();
					checkDirt();
				}
				if (numDirections2 == -1) {
					chooseDirection2();
					return;
				}
				else {
					move2();
					checkTarget();
				}
				checkCollisions();
			}else {
				if(a != ((int)Math.sqrt(GAME_UNITS)-1) * UNIT_SIZE || b != ((int)Math.sqrt(GAME_UNITS)-1) * UNIT_SIZE) {
					move2();
					checkTarget();
				}
				if (numDirections == -1) {
					chooseDirection();
					return;
				}else {
					if(x!= 0 || y!= 0) {
					move();
					checkDirt();
					}
				}
			}
			
			repaint();
	}
	}

	
	public void chooseDirection() {
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
		List<Node> path = aStar(dirtX, dirtY);
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

	//chooses direction when blocked for the dirt producer-Done
	public void chooseDirection2() {
		switch (direction2) {
		case 'U':
			direction2 = 'D';
			break;
		case 'D':
			direction2 = 'U';
			break;
		case 'R':
			direction2 = 'L';
			break;
		case 'L':
			direction2 = 'R';
			break;
		}
		List<Node> path2 = aStarD(targetX, targetY);
		if (path2 == null) {
			numDirections2 = -1;
			return;
		}
		numDirections2 = path2.size();
		directions2 = new char[numDirections2];
		for (int i = 0; i < numDirections2; i++) {
			directions2[i] = path2.get(i).getDirection();
		}
	}

	public boolean isBlocked( char d, int x, int y) { 
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
//			if(x == a || y == b) {
//				return true;
//			}

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
//			if(x == a || y == b) {
//				return true;
//			}

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
//			if(x == a || y == b) {
//				return true;
//			}
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
//			if(x == a || y == b) {
//				return true;
//			}
		}
		return false;
	}

	//checks if dirt producer has a blocked node in its path


	public List<Node> aStar(int v, int z) {
		List<Node> parents = new ArrayList<Node>();
		PriorityQueue<Node> open = new PriorityQueue<Node>();
		List<Node> closed = new ArrayList<Node>();
		
		gCost = 0;
		int gCost2 ;
		Node startNode = new Node(x, y, gCost, findHCost(x, y));
		startNode.setDirection(direction);
		Node goalNode = new Node(v, z, findHCost(x, y), 0);

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

	//the path decider for the dirt producer-not fully done
	public List<Node> aStarD(int v, int z){
		
		List<Node> parents = new ArrayList<Node>();
		PriorityQueue<Node> open = new PriorityQueue<Node>();
		List<Node> closed = new ArrayList<Node>();
		
		gCost = 0;
		int gCost2 ;
		Node startNode = new Node(a, b, gCost, findHCost(a, b));
		startNode.setDirection(direction);
		Node goalNode = new Node(v, z, findHCost(a, b), 0);

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
					gCost = 1; // if current direction
					gCost2= 10;
				} else {
					gCost = 1; // if change direction, costs more
					gCost2 = 10;
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
							if(grid[(current.getxAxis()+UNIT_SIZE)/UNIT_SIZE][current.getyAxis()/UNIT_SIZE].isTarget()) {
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
							if(grid[(current.getxAxis()-UNIT_SIZE)/UNIT_SIZE][current.getyAxis()/UNIT_SIZE].isTarget()) {
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
							if(grid[(current.getxAxis())/UNIT_SIZE][(current.getyAxis()+UNIT_SIZE)/UNIT_SIZE].isTarget()) {
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
							if(grid[(current.getxAxis())/UNIT_SIZE][(current.getyAxis()-UNIT_SIZE)/UNIT_SIZE].isTarget()) {	
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
							if(grid[(current.getxAxis())/UNIT_SIZE][(current.getyAxis()+UNIT_SIZE)/UNIT_SIZE].isTarget()) {
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
							if(grid[(current.getxAxis())/UNIT_SIZE][(current.getyAxis()-UNIT_SIZE)/UNIT_SIZE].isTarget()) {	
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
							if(grid[(current.getxAxis()-UNIT_SIZE)/UNIT_SIZE][current.getyAxis()/UNIT_SIZE].isTarget()) {
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
							if(grid[(current.getxAxis()+UNIT_SIZE)/UNIT_SIZE][current.getyAxis()/UNIT_SIZE].isTarget()) {
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
							if(grid[(current.getxAxis())/UNIT_SIZE][(current.getyAxis()-UNIT_SIZE)/UNIT_SIZE].isTarget()) {	
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
							if(grid[(current.getxAxis())/UNIT_SIZE][(current.getyAxis()+UNIT_SIZE)/UNIT_SIZE].isTarget()) {
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
							if(grid[(current.getxAxis()+UNIT_SIZE)/UNIT_SIZE][current.getyAxis()/UNIT_SIZE].isTarget()) {
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
							if(grid[(current.getxAxis()-UNIT_SIZE)/UNIT_SIZE][current.getyAxis()/UNIT_SIZE].isTarget()) {
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
		xDistance = Math.abs((targetX - xAxis) / UNIT_SIZE);
		yDistance = Math.abs((targetY - yAxis) / UNIT_SIZE);
		hCost += (xDistance * 10) + (yDistance * 10);
		return hCost;
	}
	private int getDistance(int x, int y, int z, int b) {
		int distance =  Math.abs(x - z) + Math.abs(y - b);
		return distance;
	}

	
}
