package de.grenni.gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.TexturePaint;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JComponent;

import de.grenni.gui.RoundedRect.FrameStyle;

public class SlidingToggler extends JButton implements ComponentListener,
		MouseListener {

	private BufferedImage bgImage;
	private int radius;
	private int width;
	private int height;
	private Dimension windowSize;
	
	public enum POSITION { LEFT, RIGHT };
	
	private POSITION position = POSITION.LEFT;
	
	public SlidingToggler() {
		super();
		// TODO Auto-generated constructor stub
	}
	/*
	 * Erzeugt einen neuen SlidingToggler
	 * 
	 * @param image        String	Pfad zum Hintergrundbild
	 * @param cornerRadius int		Eckenradius
	 * @param width	       int		Breite des fensters, das angezeigt werden soll
	 */
	public SlidingToggler(String image, int cornerRadius, int width) {
		super();
		setImage(image, cornerRadius, width);
		addMouseListener(this);
	}
	@Override
	public Dimension getPreferredSize() {
		return windowSize;
	}
	@Override
	public Dimension getMinimumSize() {
		return windowSize;
	}
	@Override
	public Dimension getMaximumSize() {
		return windowSize;
	}
	/*
	 * Funktion getPosition
	 * 
	 * Gibt die aktuelle Schalterposition zurück
	 * 
	 * @return STATUS
	 */
	public POSITION getPosition() {
		return position;
	}
	/*
	 * Funktion setPosition
	 * 
	 * Setzt die Schalterposition
	 * 
	 * @param position POSITION	neue Position des Schalters
	 * 
	 */
	public void setPosition(POSITION position) {
		this.position = position;
		this.repaint();
	}
	/*
	 * Funktion togglePsotion
	 * 
	 * Wechselt die Position
	 * 
	 */
	public void togglePosition() {
		if (this.position == POSITION.LEFT)
			setPosition(POSITION.RIGHT);
		else
			setPosition(POSITION.LEFT);
	}
	/*
	 * Funktion setImage
	 * Setzt ein neues Hintergrundbild
	 * 
	 * @param image        String	Pfad zum Hintergrundbild
	 * @param cornerRadius int		Eckenradius
	 * @param width	       int		Breite des fensters, das angezeigt werden soll
	 */
	public void setImage(String image, int cornerRadius, int width){
		if (image.length()>0){
			try {
				bgImage = ImageIO.read(this.getClass().getResourceAsStream(image));
			} catch (IOException e) {
				System.err.println("Hintergrundbild konnte nicht geladen werden!");
				e.printStackTrace();
				return;
			}
		}
		
		this.radius     = cornerRadius;
		this.width      = bgImage.getWidth();
		this.height     = bgImage.getHeight();
		this.windowSize = new Dimension(width, this.height);
	}
	@Override
	public void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D)g;
		adjustGraphics(g2d);
		
		Rectangle2D.Float rect = new Rectangle2D.Float(0,0,width,height);
		
		switch (position) {
		case LEFT:
			rect.x = -width + windowSize.width;
			break;
		case RIGHT:
			rect.x = 0;
			break;
		default:
			return;
		}
		
		TexturePaint tp = new TexturePaint(bgImage,rect);
		
		g2d.setPaint(tp);
		
		g2d.fillRoundRect(0, 0, windowSize.width, windowSize.height, radius, radius);
		
		RoundedRect.drawFrame(g2d, 0, 0, windowSize.width, windowSize.height, radius, 1, FrameStyle.INSET, false);
	}
	private void adjustGraphics(Graphics2D g) {
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,RenderingHints.VALUE_FRACTIONALMETRICS_ON);
	}
	@Override
	public void mouseClicked(MouseEvent arg0) {
		togglePosition();
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void componentHidden(ComponentEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void componentMoved(ComponentEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void componentResized(ComponentEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void componentShown(ComponentEvent arg0) {
		// TODO Auto-generated method stub

	}

}
