package com.rayandaou;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class MenuPanel extends JPanel{
	
	int width;
	int height;
	JFrame frame;
	 
	public MenuPanel(int width, int height, JFrame frame) {
		this.width = width;
		this.height = height;
		this.frame = frame;
		//create menu frame
		setPreferredSize(new Dimension(width, height));
		setBackground(Color.WHITE);
		setLayout(null);
		//create button that will take us to the load room frame
		JButton b1 = new JButton("A* Vacuum AI");
		b1.setFont(new Font("Arial", Font.PLAIN, 20));
		b1.setLayout(null);
		b1.setBounds((width/2) - 100, (height/4) + 300 - 30, 200, 60);
		b1.setBackground(Color.GREEN);
		b1.setBorder(BorderFactory.createBevelBorder(0));
		b1.addActionListener(new AStarListener(frame));
		add(b1);
		JButton b2 = new JButton("Multi Agent");
		b2.setFont(new Font("Arial", Font.PLAIN, 20));
		b2.setLayout(null);
		b2.setBounds((width/2) - 100, (height/4) + 400 - 30, 200, 60);
		b2.setBackground(Color.GREEN);
		b2.setBorder(BorderFactory.createBevelBorder(0));
		b2.addActionListener(new MultiAgentListener(frame));
		add(b2);

		
		
		try {
		BufferedImage wPic = ImageIO.read(this.getClass().getResource("VacuumIcon.png"));
		JLabel picLabel = new JLabel(new ImageIcon(wPic));
		picLabel.setBounds((width/2) - 200, (height/2) - 300, 400, 400);
		picLabel.setLayout(null);
		add(picLabel);
		}catch (Exception e) {
			System.err.println("Couldn't load image!");
		}

	}
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		draw(g);
	}
	public void draw(Graphics g) {
		g.setColor(Color.BLACK);
		g.setFont(new Font("Ink Free", Font.BOLD, 75));
		FontMetrics metrics = getFontMetrics(g.getFont());
		g.drawString("IEA Project", (width - metrics.stringWidth("IEA Project"))/2, (int)(height/7.5));
	}
	
	
}
