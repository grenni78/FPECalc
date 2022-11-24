package de.grenni.gui;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;

import javax.swing.SwingConstants;

public class RoundedRect {
	final static int DEFAULT_RADIUS = 5;
	public enum FrameStyle {INSET, OUTSET};
	
	// -- Konstruktor --
	public RoundedRect(int radius, Point shadowOffset){
	}
	// -- Zeichnet ein Rechteck mit der typischen Web 2.0 Optik
	public static void drawWeb20GradientRect(Graphics2D g, int width, int height, Color startColor, Color endColor, int orientation) {
		drawWeb20GradientRect( g,0, 0, width, height, DEFAULT_RADIUS, startColor, endColor, orientation);
	}
	public static void drawWeb20GradientRect(Graphics2D g, int width, int height, Color startColor, Color endColor){
		drawWeb20GradientRect( g,0, 0, width, height, DEFAULT_RADIUS, startColor, endColor, SwingConstants.VERTICAL);			
	}
	public static void drawWeb20GradientRect(Graphics2D g, int left, int top, int width, int height, int radius,Color startColor, Color endColor){
		drawWeb20GradientRect( g,left, top, width, height, radius,startColor,endColor, SwingConstants.VERTICAL);
	}
	public static void drawWeb20GradientRect(Graphics2D g, int left, int top, int width, int height, int radius,Color startColor, Color endColor, int orientation){
		int r = radius * 2;
		LinearGradientPaint p;
		if (orientation == SwingConstants.VERTICAL)
			p = new LinearGradientPaint(new Point(0,0), new Point(0,height), new float[]{0.4f,0.6f}, new Color[]{startColor,endColor});
		else
			p = new LinearGradientPaint(new Point(0,0), new Point(width,0), new float[]{0.4f,0.6f}, new Color[]{startColor,endColor});
		
		Shape rect = new RoundRectangle2D.Float(new Float(left),new Float(top),new Float(width),new Float(height),new Float(r),new Float(r));
		
		g.setPaint(p);
		g.fill(rect);
		
	}
	// -- zeichnet einen Rahmen --
	public static void drawFrame(Graphics2D g, int left, int top, int width, int height, int radius, int borderWidth, FrameStyle style, boolean transparent) {
		drawFrame(g,left,top,width,height,radius,radius,radius,radius,borderWidth,style,transparent);
	}
	public static void drawFrame(Graphics2D g, int left, int top, int width, int height, int r1, int r2, int r3, int r4, int borderWidth, FrameStyle style, boolean transparent) {
		Stroke oldStroke = g.getStroke();
		Object oldRH = g.getRenderingHint(RenderingHints.KEY_ANTIALIASING); 
		g.setStroke( new BasicStroke( 2,BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND ) );
		
		g.setRenderingHint( RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		
		Composite oldComposite = g.getComposite();
		if (transparent)
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC, .5f));
		else
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .33f));
		
		Color colorTop;
		Color colorLeft;
		Color colorBottom;
		Color colorRight;
		
		switch (style) {
		case INSET:
			colorBottom = new Color(255,255,255,178);
			colorRight  = new Color(255,255,255,130);
			colorTop    = new Color(0,0,0,178);
			colorLeft   = new Color(0,0,0,130);
			break;
		default:
		case OUTSET:
			colorTop    = new Color(255,255,255,128);
			colorLeft   = new Color(255,255,255, 80);
			colorRight  = new Color(0,0,0,128);
			colorBottom = new Color(0,0,0, 80);
			break;
		}
		
		GeneralPath p = new GeneralPath();
		int x = left;
		int y = top;
		int w = width + x;
		int h = height + y;
		

		class drawHelper {
			// Zeichnet einen Pfad und setzt ihn dann zurück
			void drawSubPath(Graphics2D g, GeneralPath p, Color c) {
				g.setColor(c);
				g.draw(p);
				Point2D old = p.getCurrentPoint();
				p.reset();
				p.moveTo(old.getX(), old.getY());
			};
			void drawCorner(Graphics2D g, GeneralPath p, float x2, float y2, double cx, double cy, Color c1, Color c2) {
				float x1 = (float)p.getCurrentPoint().getX();
				float y1 = (float)p.getCurrentPoint().getY();
				
				GeneralPath p2 = new GeneralPath();
				p2.moveTo(x1, y1);
				
				GradientPaint gp = new GradientPaint(x1,y1,c1,x2,y2,c2);
				
				p2.quadTo(cx, cy, x2, y2);
				g.setPaint(gp);
				g.draw(p2);
				p.moveTo(x2,y2);
			}
		}
		
		drawHelper dh = new drawHelper();
		
		for (int step = borderWidth; step > 0; step--) {
			
			p.moveTo(x + r1 + step, y + step);
			// oben
			p.lineTo(w- r2 - step, y + step);
			// malen
			dh.drawSubPath(g,p,colorTop);
			
			// oben rechts
			//p.quadTo(w-step,y,w-step,y+radius+step);
			dh.drawCorner(g,
						  p,
						  w-step, y+r2+step,
						  w-step, y + step,
						  colorTop, colorRight);
			
			
			// rechts
			p.lineTo(w-step, h - r3 - step);
			// malen
			dh.drawSubPath(g,p,colorRight);	
			
			// unten rechts
			//p.quadTo(w-step, h - step, w-step-radius, h-step);
			dh.drawCorner(g,
						  p,
						  w-step-r3, h-step,
						  w-step, h - step,
						  colorRight, colorBottom);
					
			// unten
			p.lineTo(x + step + r4, h - step);
			//malen
			dh.drawSubPath(g,p,colorBottom);			

			// unten links
			// p.quadTo(x+step,h-step,x+step,h -step-radius);
			dh.drawCorner(g,
						  p,
						  x+step, h -step-r4,
						  x+step, h-step,
						  colorBottom,
						  colorLeft);
			
			// links
			p.lineTo(x+step,y + r1 + step);
			//malen
			dh.drawSubPath(g,p,colorLeft);
			
			// links oben
			//p.quadTo(x+step, y+step, radius + step, y);
			dh.drawCorner(g,
					      p,
					      x + r1 + step, y + step,
					      x+step, y+step,
					      colorLeft,colorTop);
			

		}
		
		g.setComposite(oldComposite);
		
		g.setRenderingHint( RenderingHints.KEY_ANTIALIASING,oldRH);
		
		g.setStroke(oldStroke);
	}
	// malt Schatten
	public static Rect drawShadowAlpha(Graphics2D g, int width, int height) {
		return drawShadowAlpha(g,width,height,DEFAULT_RADIUS,new Point(3,5),4);
	}
	public static Rect drawShadowAlpha(Graphics2D g, int width, int height, int radius) {
		return drawShadowAlpha(g,width,height,radius,new Point(3,5),4);
	}
	public static Rect drawShadowAlpha(Graphics2D g, int width, int height, int radius, Point shadowOffset) {
		return drawShadowAlpha(g,width,height,radius,shadowOffset,4);
	}
	public static Rect drawShadowAlpha(Graphics2D g, int width, int height, int radius, Point shadowOffset, int shadowBlur) {
		// Schatten malen mit dem Zwiebel-Prinzip
		
		float maxAlpha = .65f;
		float stepAlpha = .01f;
		//float stepAlpha = maxAlpha - shadowBlur * 0.1f;
		
		Point so = (Point) shadowOffset.clone();
		

		int w  = width  - Math.abs(so.x) - 2 * shadowBlur;
		int h  = height - Math.abs(so.y) - 2 * shadowBlur;
		
		int top = 0;
		if (so.y<0) {
			top = Math.abs(so.y);
			so.y = 0;
		}
		int offset_y = 0;
		if (so.y-shadowBlur < 0) {
			offset_y = Math.abs(so.y-shadowBlur);
			top += offset_y;
			
		}
		int left = 0;
		if (so.x<0) {
			left = Math.abs(so.x);
			so.x = 0;
		}
		int offset_x = 0;
		if (so.x-shadowBlur < 0) {
			offset_x = Math.abs(so.x-shadowBlur);
			left += offset_x;
		}
		int r = 2 * radius;
		//float alpha = maxAlpha - (x * stepAlpha);
		
		
		Composite oldComposite = g.getComposite();
		
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .2f));

		for (int x=0; x<=shadowBlur; x++) {
			float alpha = maxAlpha - (x * stepAlpha);
			g.setColor(new Color(0f,0f,0f,alpha));
			g.fillRoundRect(offset_x + so.x - x ,
						    offset_y + so.y - x ,
						    w + (x*2) - so.x,
						    h + (x*2) - so.y,
						    r + (shadowBlur - x),
						    r + (shadowBlur - x));	
		}
		
		g.setComposite(oldComposite);
		
		return new Rect(left,
						top,
						w,
						h);
	}
}
