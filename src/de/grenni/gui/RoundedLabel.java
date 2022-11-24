package de.grenni.gui;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.Icon;
import javax.swing.JLabel;

import de.grenni.gui.RoundedRect.FrameStyle;

public class RoundedLabel extends JLabel implements ComponentListener{
	private int radius_top_left     = 0;
	private int radius_top_right    = 0;
	private int radius_bottom_right = 0;
	private int radius_bottom_left  = 0;
	private LinearGradientPaint background;
	private Color backgroundStart;
	private Color backgroundEnd;
	private Font font;
	private float fontSize = 12f;
	private int fontStyle = 0;
	private Color fontColor = new Color(0,0,0);
	// -- Konstruktoren ---
	public RoundedLabel (){
		super();
	}
	public RoundedLabel(Icon image) {
		super(image);
	}
	public RoundedLabel(Icon image, int horizontalAlignment)  {
		super(image,horizontalAlignment);
	}
	public RoundedLabel(String text) {
		super(text);
	}
	public RoundedLabel(String text, Icon icon, int horizontalAlignment) {
		super(text,icon,horizontalAlignment);
	}
	public RoundedLabel(String text, int horizontalAlignment) {
		super(text, horizontalAlignment);
	}
	public RoundedLabel(int radius, String text) {
		this(radius,radius,radius,radius,new Color(200,200,200),new Color(180,180,180),text);
	}
	public RoundedLabel(int r1, int r2, int r3, int r4, Color start, Color end, String text) {
		super(text);
		radius_top_left     = r1;
		radius_top_right    = r2;
		radius_bottom_right = r3;
		radius_bottom_left  = r4;
		setBackground(start,end);
		initFont();
		addComponentListener(this);
		setOpaque(false);
	}
	private void initFont() {
		LinkedFonts lf = new LinkedFonts();
		font = lf.getFont("Vera",fontSize,fontStyle);
		if (font == null)
			font = new Font(Font.SANS_SERIF, fontStyle, (int)fontSize);
		setFont(font);
	}
	// --- Setter / Getter ---
	public void setFont(int size,int style,Color color){
		fontSize = size;
		fontStyle = style;
		fontColor = color;
		setForeground(color);
		initFont();
	}
	public void setBackground(Color start, Color end) {
		
		this.backgroundStart = start;
		this.backgroundEnd = end;
		
		updateBackgroundGradient();
		
	}
	private void updateBackgroundGradient() {
		int h = getHeight();
		if (h>0)
			this.background = new LinearGradientPaint(0,
													  0,
													  0,
													  h,
													  new float[]{0.1f,0.9f},
													  new Color[]{backgroundStart,backgroundEnd});
	}
	@Override
	protected void paintComponent(Graphics g) {
		
		Graphics2D g2d = (Graphics2D)g;
		int width  = getWidth();
		int height = getHeight();
		
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		
		// Grafik mit Hintergrundfarbe initialisieren
				
		Shape s = Shapes.roundRect(0, 0, width, height, radius_top_left, radius_top_right, radius_bottom_right, radius_bottom_left);
		if (background != null)
			g2d.setPaint(background);
		else
			g2d.setColor(new Color(100,100,255));
		
		g2d.fill(s);
		
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1));
		super.paintComponent(g);
		
	}
	@Override
	protected void paintBorder(Graphics g) {
		RoundedRect.drawFrame((Graphics2D)g,0,0,getWidth(),getHeight(),radius_top_left,radius_top_right,radius_bottom_right,radius_bottom_left,1,FrameStyle.OUTSET,false);
	}
	@Override
	public void componentResized(ComponentEvent e) {
		updateBackgroundGradient();
	}

	@Override public void componentHidden(ComponentEvent e) { /* empty */ }
	@Override public void componentMoved(ComponentEvent e) { /* empty */ }
	@Override public void componentShown(ComponentEvent e) { /* empty */ }
}
