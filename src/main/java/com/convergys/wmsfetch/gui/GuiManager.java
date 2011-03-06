package com.convergys.wmsfetch.gui;

import javax.swing.JFrame;

import com.convergys.wmsfetch.cli.ArgumentDefinitions;

public class GuiManager {

	public GuiManager() {
		JFrame frame = new JFrame();

		frame.add(new Form(new ArgumentDefinitions().getOptions()).getPane());

		// Size the frame according to widgets
		frame.pack();

		// Show the frame
		frame.setVisible(true);
	}
}
