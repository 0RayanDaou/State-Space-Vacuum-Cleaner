package com.rayandaou;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

import javax.swing.JFrame;

public class MultiAgentListener implements ActionListener {

	JFrame frame;
	
	public MultiAgentListener(JFrame frame) {
		this.frame = frame;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Thread thread = new Thread(){
		    public void run(){
				((Frames) frame).multiAgent();
		    }
		  };

		  thread.start();
	}

}
