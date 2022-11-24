package de.grenni.gui;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.jhlabs.image.GaussianFilter;

import de.grenni.gui.RoundedRect.FrameStyle;

public class RoundedPanel extends JPanel implements ComponentListener{

	private int radius;
	private boolean shadowed;
	private Point shadowOffset;
	private int shadowBlur;
	private float shadowOpacity;
	
	public RoundedPanel(int radius, boolean shadowed, Point shadowOffset, int shadowBlur, float shadowOpacity) {
		this(radius,shadowed,shadowOffset,shadowBlur,shadowOpacity,null,true);
	}

	public RoundedPanel(int radius, boolean shadowed, Point shadowOffset, int shadowBlur, float shadowOpacity,LayoutManager layout,boolean isDoubleBuffered) {
		super(layout);
		this.radius = radius * 2;
		this.shadowed = shadowed;
		this.shadowOffset = shadowOffset;
		this.shadowBlur = shadowBlur;
		this.shadowOpacity = shadowOpacity;
		addComponentListener(this);
		setDoubleBuffered(true);
		setBorder(new EmptyBorder(radius,radius,radius+shadowBlur+3,radius+shadowBlur+3));
	}

	@Override
	public void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D)g;

		adjustGraphics(g2d);
		
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1.0f));
		
		int width = getWidth() - Math.abs(shadowOffset.x) - shadowBlur * 2;
		int height = getHeight() - Math.abs(shadowOffset.y) - shadowBlur * 2;

		if (shadowed) {
			int w = width + Math.abs(shadowOffset.x) + shadowBlur * 2;
			int h = height  + Math.abs(shadowOffset.y) + shadowBlur * 2;
			BufferedImage shadow = new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB);
			Graphics2D gs = shadow.createGraphics();
			adjustGraphics(gs);
			gs.setBackground(new Color(0,0,0,0));
			gs.clearRect(0, 0, w, h);
			gs.setColor(new Color(0,0,0,shadowOpacity));

			gs.fillRoundRect(shadowOffset.x,shadowOffset.y,width,height,radius,radius);

			GaussianFilter filter = new GaussianFilter(shadowBlur); 
			BufferedImage blurred = filter.filter(shadow, null);
			g2d.drawImage(blurred, 0, 0, null);
		}

		g2d.setColor(getBackground());
		g2d.fillRoundRect(1,1,width-2,height-2,radius,radius);
		RoundedRect.drawFrame(g2d, 0, 0, width, height, radius/2 , 2, FrameStyle.OUTSET, false);

	}
	private void adjustGraphics(Graphics2D g) {
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,RenderingHints.VALUE_FRACTIONALMETRICS_ON);
	}
	@Override
	public void componentResized(ComponentEvent arg0) {
		
	}

	@Override public void componentHidden(ComponentEvent arg0) {}
	@Override public void componentMoved(ComponentEvent arg0) {}
	@Override public void componentShown(ComponentEvent arg0) {}

}
