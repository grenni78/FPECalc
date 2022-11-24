package de.grenni.gui;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LinearGradientPaint;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import de.grenni.gui.RoundedRect.FrameStyle;
import de.grenni.utils.GraphicEffects;

import com.jhlabs.image.GaussianFilter;

public class RoundedButton extends JButton implements ComponentListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6210690622947858812L;
	private int radius  = 0;
	private LinearGradientPaint background;
	private Color backgroundStart;
	private Color backgroundEnd;
	private Font font;
	private float fontSize = 12f;
	private int fontStyle = 0;
	private Color fontColor = new Color(0,0,0);
	
	private boolean shadowed = false;
	private Point shadowOffset = new Point(0,0);
	private int shadowBlur = 3;
	private float shadowOpacity = .3f;
	
	private boolean textShadowed = false;
	private Point textShadowOffset = new Point(0,0);
	private int textShadowBlur = 3;
	private float textShadowOpacity = .3f;
	
	public RoundedButton() {
		super();
	}
	public RoundedButton(String text) {
		super(text);
		initFont();
		addComponentListener(this);
		setOpaque(false);
		setDoubleBuffered(true);
	}
	public RoundedButton(String text, Icon icon) {
		this(text);
		setIcon(icon);
	}
	public RoundedButton(int radius, Color start, Color end, String text) {
		this(text);
		setBackground(start,end);
		this.radius = radius;
	}
	public void setShadow(boolean enabled,int offx, int offy, int blur_radius, float opacity) {
		this.shadowed = enabled;
		this.shadowOffset.x = offx;
		this.shadowOffset.y = offy;
		this.shadowBlur = blur_radius;
		this.shadowOpacity  = opacity;	
		calculateMinSize();
	}
	public void setTextShadow(boolean enabled,int offx, int offy, int blur_radius, float opacity) {
		this.textShadowed = enabled;
		this.textShadowOffset.x = offx;
		this.textShadowOffset.y = offy;
		this.textShadowBlur = blur_radius;
		this.textShadowOpacity  = opacity;	
		calculateMinSize();
	}
	private void calculateMinSize() {
		FontMetrics fm = getFontMetrics(getFont());
		int h = fm.getHeight() + shadowOffset.y + shadowBlur;
		int w = fm.stringWidth(getText()) + shadowOffset.x + shadowBlur;
		setMinimumSize(new Dimension(w,h));
		setBorder(new EmptyBorder(10,10+radius,10,10+radius));
	}
	private void initFont() {
		LinkedFonts lf = new LinkedFonts();
		font = lf.getFont("Vera",fontSize,fontStyle);
		System.out.println(font.getSize());
		if (font == null)
			font = new Font(Font.SANS_SERIF, fontStyle, (int)fontSize);
		setFont(font);
		calculateMinSize();
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
		int h = getHeight()-shadowOffset.y - shadowBlur;
		if (h>0)
			this.background = new LinearGradientPaint(0,
													  0,
													  0,
													  h,
													  new float[]{0.3f,0.7f},
													  new Color[]{backgroundStart,backgroundEnd});
	}
	@Override
	protected void paintComponent(Graphics g) {
		
		Graphics2D g2d = (Graphics2D)g;
		int width  = getWidth();
		int height = getHeight();
		
		if (shadowed) {
			width = width - Math.abs(shadowOffset.x) - shadowBlur;
			height = height - Math.abs(shadowOffset.y) - shadowBlur;
		}
		Shape s = Shapes.roundRect(0, 0, width, height, radius, radius, radius, radius);
		
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1.0f));
		adjustGraphics(g2d);
		
		if (shadowed) {
			int w = width+Math.abs(shadowOffset.x)+shadowBlur;
			int h = height+Math.abs(shadowOffset.y)+shadowBlur;
			BufferedImage shadow = new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB);
			Graphics2D gs = shadow.createGraphics();
			adjustGraphics(gs);
			gs.setBackground(new Color(0,0,0,0));
			gs.clearRect(0, 0, w, h);
			gs.setColor(new Color(0,0,0,shadowOpacity));
			gs.translate(shadowOffset.x,shadowOffset.y);
			gs.fill(s);
			GaussianFilter filter = new GaussianFilter(shadowBlur); 
			BufferedImage blurred = filter.filter(shadow, null);
			g2d.drawImage(blurred, 0, 0, null);
		}
		
		// Grafik mit Hintergrundfarbe initialisieren
				
		if (background != null)
			g2d.setPaint(background);
		else
			g2d.setColor(new Color(100,100,255));
		
		g2d.fill(s);
		
		RoundedRect.drawFrame(g2d,0,0,width,height,radius,radius,radius,radius,1,FrameStyle.OUTSET,false);
			
		drawText(g2d, width,height);
		//if ()
		//super.paintComponent(g);
		
	}
	private void drawText(Graphics2D g2d, int width, int height) {
		TextLayout textLayout = new TextLayout(getText(), getFont(), g2d.getFontRenderContext());
		Rectangle2D bounds = textLayout.getBounds();
		Insets insets = getBorder().getBorderInsets(null);

		int left = insets.left;
		int top = 0;
		switch (getHorizontalAlignment()) {
			case SwingConstants.CENTER:
				left = (int) (Math.round((width - insets.left - insets.right) / 2) - bounds.getWidth()/2);
				break;
			default:
			case SwingConstants.LEFT:
				break;
			case SwingConstants.RIGHT:
				left = (int) (width - insets.right - bounds.getWidth());
				break;
		}
		switch (getVerticalAlignment()) {
			case SwingConstants.BOTTOM:
				top = (int)(height - insets.bottom - bounds.getHeight());
				break;
			default:
			case SwingConstants.CENTER:
				top = (int)(height / 2 + bounds.getHeight() / 2);
				break;
			case SwingConstants.TOP:
				top = insets.top;
				break;				
		}
		if (textShadowed) {
			int w = width+Math.abs(shadowOffset.x)+shadowBlur;
			int h = height+Math.abs(shadowOffset.y)+shadowBlur;
			
			BufferedImage shadow = new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB);
			Graphics2D gs = shadow.createGraphics();
			adjustGraphics(gs);
			gs.setBackground(new Color(0,0,0,0));
			gs.clearRect(0, 0, w, h);
			gs.setColor(new Color(0,0,0,textShadowOpacity));
			
			textLayout.draw(gs,
				    left + textShadowOffset.x,
				    top + textShadowOffset.y );
			
			GaussianFilter filter = new GaussianFilter(textShadowBlur); 
			BufferedImage blurred = filter.filter(shadow, null);
			g2d.drawImage(blurred, 0, 0, null);
			
			
			
		}
		g2d.setPaint(getForeground());
		textLayout.draw(g2d,
					    left,
					    top);
	}
	private void adjustGraphics(Graphics2D g) {
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,RenderingHints.VALUE_FRACTIONALMETRICS_ON);
	}
	@Override
	protected void paintBorder(Graphics g) {
		
	}
	@Override public void componentResized(ComponentEvent e) {
		updateBackgroundGradient();
	}
	@Override public void componentHidden(ComponentEvent arg0) {}
	@Override public void componentMoved(ComponentEvent arg0) {}
	@Override public void componentShown(ComponentEvent arg0) {}
}
