package de.grenni.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class RoundRectWindow extends ShapedWindow implements ComponentListener{

	/**
	 * Fenster in Form eines gerundeten Rechtecks
	 */
	private static final long serialVersionUID = 2774463176454942312L;
	private Rect clip;

	public Rect getClip() {
		return clip;
	}
	//-- Konstruktoren ---
	public RoundRectWindow() {
		super();
		addComponentListener(this);
	}
	public RoundRectWindow(String caption) {
		this();
		setTitle(caption);
	}
	//  --- Event Listener ---
	@Override public void componentHidden(ComponentEvent arg0) { /* empty */ }
	@Override public void componentMoved(ComponentEvent arg0) { /* empty */ }
	@Override public void componentShown(ComponentEvent arg0) { /* empty */ }
	@Override public void componentResized(ComponentEvent arg0) { /* empty */ }

}
