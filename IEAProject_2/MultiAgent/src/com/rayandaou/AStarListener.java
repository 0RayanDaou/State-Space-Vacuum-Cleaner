package com.rayandaou;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

import javax.swing.JFrame;

public class AStarListener implements ActionListener {

	JFrame frame;
	
	public AStarListener(JFrame frame) {
		this.frame = frame;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Thread thread = new Thread(){
		    public void run(){
				((Frames) frame).aStarAI();
		    }
		  };

		  thread.start();
	}

}
