package de.grenni.gui;


import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;

import de.grenni.gui.RoundedRect.FrameStyle;

public class RoundedShapeScrollbarUI extends BasicScrollBarUI {

	private String location;
	private int minWidth;
	private int minHeight;
	
	public RoundedShapeScrollbarUI(String location) {
		super();
		this.location = location;
	}
	private void makeOpaque(JButton button, ImageIcon icon) {
		int w = icon.getIconWidth();
		int h = icon.getIconHeight();
		
		Image src = icon.getImage();
		BufferedImage dest = new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = dest.createGraphics();
		g.setColor(button.getBackground());
		g.fillRect(0,0,w,h);
		g.drawImage(src, 0, 0, null);
		g.dispose();
		icon.setImage(dest);
		button.setIcon(icon);
	}
	protected JButton createDecreaseButton(int orientation){
		String buttonImage = "sbi_";

		JButton button;
		if (orientation == HORIZONTAL) {
			buttonImage += "hor";
		} else {
			buttonImage += "ver";
		}
		buttonImage += "_inc.png";

		ImageIcon icon = new ImageIcon(this.getClass().getResource(location + buttonImage));
		button = new JButton(icon);
		button.setBackground(scrollbar.getBackground());
		button.setOpaque(true);
		button.setContentAreaFilled(false);
		button.setBorderPainted(false);
		button.setFocusPainted(false);
		makeOpaque(button,icon);
		return button;
	}
	protected JButton createIncreaseButton(int orientation){
		String buttonImage = "sbi_";
		JButton button;
		if (orientation == HORIZONTAL) {
			buttonImage += "hor";
		} else {
			buttonImage += "ver";
		}
		buttonImage += "_dec.png";

		ImageIcon icon = new ImageIcon(this.getClass().getResource(location + buttonImage));
		minHeight = icon.getIconHeight();
		minWidth = icon.getIconWidth();
		button = new JButton(icon);
		button.setBackground(scrollbar.getBackground());
		button.setOpaque(true);
		button.setContentAreaFilled(false);
		button.setBorderPainted(false);
		button.setFocusPainted(false);
		makeOpaque(button,icon);
		return button;
	}
	public Dimension getPreferredSize(JComponent c) {
		switch (scrollbar.getOrientation()){
		case HORIZONTAL:
			return new Dimension(scrollbar.getWidth(),minHeight);
			
		case VERTICAL:
			return new Dimension(minWidth,scrollbar.getHeight());
			
		default:
			return super.getPreferredSize(c);
		}
	}
	@Override
	protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
		RoundedRect.drawWeb20GradientRect((Graphics2D)g, trackBounds.x+2, trackBounds.y, trackBounds.width-4, trackBounds.height, 5, new Color(170,170,170), new Color(190,190,190), SwingConstants.HORIZONTAL);
		// TODO: klären, warum hier trackBounds.height+30
		RoundedRect.drawFrame((Graphics2D)g, trackBounds.x+2, trackBounds.y, trackBounds.width-4, trackBounds.height, 4, 1, FrameStyle.INSET, false);
	}
	@Override
	protected  void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
		Rectangle tb = thumbBounds;
		RoundedRect.drawWeb20GradientRect((Graphics2D)g, tb.x+2, tb.y, tb.width-4, tb.height, 5, new Color(200,200,200), new Color(190,190,190), SwingConstants.HORIZONTAL);
		// TODO: klären, warum hier trackBounds.height+30
		RoundedRect.drawFrame((Graphics2D)g, tb.x+2, tb.y, tb.width-4, tb.height-1, 4, 2, FrameStyle.OUTSET, false);		
	}
}

