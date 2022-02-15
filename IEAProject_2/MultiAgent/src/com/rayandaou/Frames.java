package com.rayandaou;

import java.awt.CardLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Frames<MinMaxAlgorithm> extends JFrame{

	static final int SCREEN_WIDTH =800;
	static final int SCREEN_HEIGHT = 750;
	private JPanel contentPane;
	private MenuPanel MPanel = new MenuPanel(SCREEN_WIDTH, SCREEN_HEIGHT, this);
	private AStarVacuumClean ASVC;
	private MultiAgentSim MAS;


	CardLayout cardLayout = new CardLayout();
	
	public Frames() {
		setTitle("VacuumCleaner Agent");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		contentPane = new JPanel();
		contentPane.setLayout(cardLayout);
		contentPane.add(MPanel, "Menu Panel");
		setResizable(true);
		setContentPane(contentPane);
		pack();
		setVisible(true);
		setLocationRelativeTo(null);
		
	}
	public void aStarAI() {
		ASVC = new AStarVacuumClean(this, 600, 600);
		contentPane.add(ASVC, "A* AI Panel");
        cardLayout.next(contentPane);
		contentPane.remove(MPanel);
		ASVC.requestFocusInWindow();
	}
	public void multiAgent() {
		MAS = new MultiAgentSim(this, 600, 600);
		contentPane.add(MAS, "Multi Agent Panel");
		cardLayout.next(contentPane);
		contentPane.remove(MPanel);
		MAS.requestFocusInWindow();
	}

}
