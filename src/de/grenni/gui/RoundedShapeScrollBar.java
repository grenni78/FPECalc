package de.grenni.gui;

import java.awt.Color;

import javax.swing.JScrollBar;

public class RoundedShapeScrollBar extends JScrollBar {

	private RoundedShapeScrollbarUI myScrollBarUI;
	
	public RoundedShapeScrollBar() {
		// TODO Auto-generated constructor stub
	}

	public RoundedShapeScrollBar(String location, int arg0, Color background) {
		super(arg0);
		setBackground(background);
		myScrollBarUI = new RoundedShapeScrollbarUI(location);
		setOpaque(true);
		setupUI();
	}

	public RoundedShapeScrollBar(int arg0, int arg1, int arg2, int arg3,
			int arg4) {
		super(arg0, arg1, arg2, arg3, arg4);
		// TODO Auto-generated constructor stub
	}
	private void setupUI(){
		setUI(myScrollBarUI);
	}
	@Override
	public void updateUI() {
		setupUI();
	}
}
