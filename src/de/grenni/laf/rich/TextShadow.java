package de.grenni.laf.rich;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.plaf.synth.ColorType;
import javax.swing.plaf.synth.SynthContext;
import javax.swing.plaf.synth.SynthGraphicsUtils;
import javax.swing.plaf.synth.SynthStyle;
import javax.swing.text.JTextComponent;
import javax.swing.text.View;

import com.jhlabs.image.GaussianFilter;

import de.grenni.utils.Dimensions;


public class TextShadow extends SynthGraphicsUtils {
	private JLabel offscreen; 
	/**
	 * This method calculates Font metrics from context
	 */
	private FontMetrics computeFontMetrics(SynthContext context, Font font, FontMetrics metrics) {

		if (metrics == null) {

			if (font == null) {
				font = context.getStyle().getFont(context);
			}

			metrics = context.getComponent().getFontMetrics(font);
		}

		return metrics;
	}
	/**
	 * Sets a few Paramaters to adjust the graphic context
	 * @param g
	 */
	private void adjustGraphics(Graphics2D g) {
		g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,RenderingHints.VALUE_FRACTIONALMETRICS_ON);
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
	}
	/**
	 * Paints shadowed text
	 * @param context
	 * @param g
	 * @param text
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 */
	public void paintTextShadow(SynthContext context, Graphics g, String text, int x, int y, int w, int h,FontMetrics fm) {
		if (text==null || text.length()==0) return;

		// if not exists, create offscreen Label (needed because of HTML-Text)
		if (offscreen == null) {
			offscreen = new JLabel();
			offscreen.setName("offscreen");
		}
		// Setting the text
		offscreen.setText(text);
		
		// getting components style
		SynthStyle style = context.getStyle();
		// getting styles font
		Font font = style.getFont(context);
		// and setting is to the label
		offscreen.setFont(font);
		// setting foreground color
		offscreen.setForeground(style.getColor(context, ColorType.TEXT_FOREGROUND ));
		
		// getting style properties
		Color shadowColor = Color.decode(style.getString(context,"labelStyle.shadowColor","#FF0000"));
		
		int shadowBlur = style.getInt(context,"labelStyle.shadowBlur",1);
	
		float shadowOpacity = style.getInt(context,"labelStyle.shadowOpacity",100) / 100f;
	
		int shadowOffsetX = style.getInt(context,"labelStyle.shadowOffsetX",0);
		
		int shadowOffsetY = style.getInt(context,"labelStyle.shadowOffsetY",0);
		
		// calculating the new Size with extra space needed for drop-shadow
		int width = w + shadowBlur * 2;
		int height = h + shadowBlur * 2;

		// setting sinze of the offscreen label
		offscreen.setSize(width,height);
		
		// buffer for foreground
		BufferedImage foreground = new BufferedImage(width,
													 height,
													 BufferedImage.TYPE_INT_ARGB);
	
		// buffer for the shadow
		BufferedImage shadow = new BufferedImage(width,
									             height,
									             BufferedImage.TYPE_INT_ARGB);
	
		// Paint the text into the foreground image
		Graphics2D g2d = foreground.createGraphics();
		
		adjustGraphics(g2d);
		
		g2d.translate(shadowBlur / 2, shadowBlur / 2);
	
		// eigentlicher Mal-Vorgang
		offscreen.paint(g2d);
			
		g2d.dispose();
		
		// filling shadow's background with shadowColor
		g2d = shadow.createGraphics();
		
		g2d.setColor(shadowColor);
		
		g2d.fillRect(0, 0, width, height);
		
		g2d.dispose();
		
		// Gettings foreground's alpha channel and setting it to shadow
		DataBuffer src = foreground.getAlphaRaster().getDataBuffer();
		DataBuffer dst = shadow.getAlphaRaster().getDataBuffer();
		
		for (int py = 0; py < height; py++ )
			for (int px = 0; px < width; px++) {
				int index =(py*width) + px;
				
				int value = src.getElem(index) & 0xFF000000; // nur alpha-werte
				value *= shadowOpacity;
				dst.setElem(index, value);
			}
		
		shadow.flush();
		foreground.flush();
		
		// do the blurring
		GaussianFilter filter = new GaussianFilter(shadowBlur); 
		shadow = filter.filter(shadow, null);
		
		// draw both images to the component's canvas
		g2d = (Graphics2D)g;
		adjustGraphics(g2d);
		
		double scaleX = (double) w / (width + shadowOffsetX);
		//double scaleY = (double) h / (height + shadowOffsetY);
		//double scaleXY = Math.min(scaleX, scaleY);
		
		g2d.scale(scaleX, scaleX);
		//g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,shadowOpacity / 100f));
		
		g2d.drawImage(shadow,
					  x + shadowOffsetX,
					  y + shadowOffsetY,
					  null);
		g2d.drawImage(foreground, x, y, null);
	
	}
	@Override
	public void paintText(SynthContext ss, Graphics g, String text, Icon icon, int hAlign, int vAlign, int hTextPosition, int vTextPosition, int iconTextGap, int mnemonicIndex, int textOffset) {
		FontMetrics metrics = computeFontMetrics(ss, null,null);
		  
		Rectangle textR = Dimensions.getTextRect(text, metrics);
		Rectangle iconR = Dimensions.getIconRect(icon);
			
		Insets insets = ss.getStyle().getInsets(ss, null);
		Rectangle viewR = Dimensions.subtractInsets(g.getClipBounds(),insets);
	
		layoutText(ss, metrics, text, icon, hAlign, vAlign,
				   hTextPosition, vTextPosition, viewR, iconR, textR,
				   iconTextGap);
	
		if (icon != null)
			g.drawImage(((ImageIcon)icon).getImage(),iconR.x,iconR.y,null);
			
		textR.translate(textOffset, textOffset);
			
		paintTextShadow(ss,g,text,textR.x,textR.y - 5,textR.width,textR.height,metrics);
	}
	@Override
	public void paintText(SynthContext context, Graphics g, String text, int x, int y, int mnemonicIndex) {
		System.out.println("Info: call grenni! This function is not yet implemented.");
	}
	@Override
	public void paintText(SynthContext context, Graphics g, String text, Rectangle bounds, int mnemonicIndex) {	
		paintTextShadow(context,g,text,bounds.x,bounds.y,bounds.width,bounds.height,computeFontMetrics(context, null,null));
	}
}
