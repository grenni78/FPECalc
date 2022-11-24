package de.grenni.gui;

import java.awt.AlphaComposite;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

public abstract class ShapedWindow extends JFrame implements WindowListener, MouseListener, MouseMotionListener {

	/**
	 * Displays a shaped Window
	 */
	private static final long serialVersionUID = 3365270201279064932L;
	protected GraphicsConfiguration translucencyCapableGC;
	protected boolean isShapingSupported;
	protected boolean isOpacityControlSupported;
	protected boolean isTranslucencySupported;
	private Point sp;
	private int compStartHeight;
	private int compStartWidth;
	
	protected int minHeight;
	protected int minWidth;	
	protected int cornerResizeOffset = 40;
	


	public ShapedWindow() {
		super();
		isShapingSupported = AWTUtilitiesWrapper.isTranslucencySupported(AWTUtilitiesWrapper.PERPIXEL_TRANSPARENT);
        isOpacityControlSupported = AWTUtilitiesWrapper.isTranslucencySupported(AWTUtilitiesWrapper.TRANSLUCENT);
        isTranslucencySupported = AWTUtilitiesWrapper.isTranslucencySupported(AWTUtilitiesWrapper.PERPIXEL_TRANSLUCENT);

        translucencyCapableGC = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
        if (!AWTUtilitiesWrapper.isTranslucencyCapable(translucencyCapableGC)) {
            translucencyCapableGC = null;

            GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
            GraphicsDevice[] devices = env.getScreenDevices();

            for (int i = 0; i < devices.length && translucencyCapableGC == null; i++) {
                GraphicsConfiguration[] configs = devices[i].getConfigurations();
                for (int j = 0; j < configs.length && translucencyCapableGC == null; j++) {
                    if (AWTUtilitiesWrapper.isTranslucencyCapable(configs[j])) {
                        translucencyCapableGC = configs[j];
                    }
                }
            }
                        
        }
        if (translucencyCapableGC == null) {
            isTranslucencySupported = false;
        } else {
        	this.setUndecorated(true);
            AWTUtilitiesWrapper.setWindowOpaque(this,false);

        	
        }
		
		addWindowListener( this );
		addMouseListener( this );
		addMouseMotionListener( this );

	}
	
	// ermittelt die Mausposition
	protected Point getCursorLocation(MouseEvent e) {
	    Point cursor = e.getPoint();
	    Point target_location = this.getLocationOnScreen();
	    return new Point((int) (target_location.getX() + cursor.getX()),
	        (int) (target_location.getY() + cursor.getY()));
	}
	//  --- Event Listener ---
	@Override
	public void mousePressed(MouseEvent e) {
		sp = e.getPoint(); 
		compStartHeight = getSize().height;
		compStartWidth  = getSize().width;
	}
	@Override
	public void mouseDragged(MouseEvent e) {
		Point p = e.getPoint();
		int compWidth = getSize().width;
		int compHeight = getSize().width;
		
		if (getCursor().getType() == Cursor.NW_RESIZE_CURSOR)
		{
			int nextHeight = compStartHeight+p.y-sp.y;
			int nextWidth  = compStartWidth+p.x-sp.x;
			
			if ((nextHeight > minHeight)&&(nextWidth > minWidth))
			{
				setSize(nextWidth,nextHeight);
				setVisible(true);	
			}
		}
		else
		{
			int x = getX()+p.x-sp.x;	
			int y = getY()+p.y-sp.y;	
			setLocation(x,y); 
		}
	  }
	@Override public void windowClosing( WindowEvent event ) { 
		System.exit( 0 ); 
	}
	@Override 
	public void mouseMoved(MouseEvent e) 
	{
 		Point p = e.getPoint(); 
 
		if ((p.y > e.getComponent().getSize().height - cornerResizeOffset )
			&& (p.x > e.getComponent().getSize().width - cornerResizeOffset )){
			setCursor( Cursor.getPredefinedCursor( Cursor.NW_RESIZE_CURSOR )); 
		} else {
			setCursor( Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR)); 
		}
 	}
	@Override public void mouseClicked(MouseEvent e) { /*Empty*/ }
	@Override public void mouseEntered(MouseEvent e) { /*Empty*/ }
	@Override public void mouseExited(MouseEvent e) { /*Empty*/ }
	@Override public void mouseReleased(MouseEvent e) { /*Empty*/ }
	
	@Override public void windowClosed( WindowEvent event ) { /*Empty*/ } 
	@Override public void windowDeiconified( WindowEvent event ) { /*Empty*/ } 
	@Override public void windowIconified( WindowEvent event ) { /*Empty*/ } 
	@Override public void windowActivated( WindowEvent event ) { /*Empty*/ } 
	@Override public void windowDeactivated( WindowEvent event ) { /*Empty*/ } 
	@Override public void windowOpened( WindowEvent event ) { /*Empty*/ } 
	
}
