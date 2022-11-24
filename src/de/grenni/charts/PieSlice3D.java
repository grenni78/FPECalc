package de.grenni.charts;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LinearGradientPaint;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;
import javax.swing.Timer;

import de.grenni.fx.Animator;
import de.grenni.fx.AnimatorCallback;
import de.grenni.fx.TranslationAnimator;


class PieSlice3D implements AnimatorCallback  {
	private PieChart3D parent;
	
	private TranslationAnimator animator;
	
	private double arcZ;
	private double arcStart;
	private double arcAngle;
	private double arcEnd;
	
	private int width;
	private int height;
	
	private float innerHeight;
	private float innerWidth;
	
	private float planeHeight;
	private float sliceHeight;
	
	private Point2D bp_sp;
	private Point2D bp_ep;
	private Point2D tp_sp;
	private Point2D tp_ep;
	private float c_x;
	private float tc_y;
	private float bc_y;

	private Path2D.Float hitArea;
	
	private Point2D offset;
	
	private Color color;
	private String label;
	
	private Arc2D.Float bottomPlane;
	private Arc2D.Float topPlane;
	
	private Insets insets;
	
	private BufferedImage pieImage;
	private LinearGradientPaint linGradEdge;
	private LinearGradientPaint linGradSurfTop;
	private LinearGradientPaint linGradSurfBot;
	private LinearGradientPaint linGradCyl;

	/**
	 * 
	 * @param arcZ
	 * @param width
	 * @param height
	 * @param insets
	 * @param arc1
	 * @param arc2
	 * @param color
	 * @param label
	 */
	public PieSlice3D(float arcZ,int width, int height, Insets insets, double arc1, double arc2, Color color, String label, PieChart3D parent) {
		this.insets = insets;
		this.offset = new Point2D.Double(0,0);
		this.color = color;
		
		this.parent = parent;
		
		hitArea = new Path2D.Float();
		
		animator = new TranslationAnimator(this);
		
		setArc(arc1, arc2);
		setDimensions(width,height,arcZ);
		setColor(color);
		this.label = label;
		
		drawPieImage();
	}
	/**
	 * 
	 * @param arc1
	 * @param arc2
	 * @param height
	 * @param color
	 * @param label
	 */
	public PieSlice3D(float arcZ,int width, int height, double arc1, double arc2, Color color, String label,PieChart3D parent) {
		this(arcZ,width,height,new Insets(0,0,0,0), arc1, arc2, color,label,parent);
	}
	/**
	 * Prüft, ob ein Tortenstück unter x,y liegt.
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean hit(int x, int y) {
		x = x - (int)offset.getX();
		y = y + (int)offset.getY();
		
		if (hitArea.contains(x,y))
			
			return true;
		
		return false;
	}
	/**
	 * Funktion invalidate
	 * 
	 * erzwingt ein neu-zeichnen
	 */
	public void invalidate() {
		pieImage = new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);
		drawPieImage();
		parent.repaint();
	}
	/**
	 * 
	 * @param insets
	 */
	public void setInsets(Insets insets) {
		this.insets = insets;
	}
	/**
	 * 
	 * @param width
	 * @param height
	 * @param arcZ
	 */
	public void setDimensions(int width, int height, float arcZ) {
		this.arcZ = (arcZ > 90) ? 90 : arcZ;
		
		int w = width - insets.left - insets.right;
		int h = height - insets.top - insets.bottom;

		if (w!=h) {
			if (w>h) {
				int diff = w-h;
				insets.left += diff / 2f;
				insets.right += diff / 2f;
			} else {
				int diff = h-w;
				insets.top += diff / 2f;
				insets.bottom += diff / 2f;
			}
		}
		
		this.width = width;
		this.height = height;
		
		innerHeight = this.height- insets.top - insets.bottom;
		innerWidth = this.width - insets.left - insets.right;
		
		planeHeight = ((float)(((innerWidth<innerHeight)?innerWidth:innerHeight) * Math.sin(arcZ * Math.PI / 180)));
		sliceHeight = innerHeight - 2 * planeHeight;
		
		bottomPlane = new Arc2D.Float(insets.left, insets.top + innerHeight - planeHeight,
				  					  innerWidth,planeHeight,
				  					  ((float)arcStart),((float)arcAngle),
				  					  Arc2D.PIE);

		topPlane = new Arc2D.Float(insets.left, insets.top,
								   innerWidth,planeHeight,
								   ((float)arcStart),((float)arcAngle),
								   Arc2D.PIE);

		bp_sp = bottomPlane.getStartPoint();
		bp_ep = bottomPlane.getEndPoint();

		tp_sp = topPlane.getStartPoint();
		tp_ep = topPlane.getEndPoint();

		c_x  = insets.left + innerWidth / 2;
		tc_y = insets.top + planeHeight / 2;
		bc_y = insets.top + innerHeight - planeHeight / 2;
		
		hitArea.reset();
		
		if ((arcStart>=180)&&(arcEnd<=360)) {
			hitArea.moveTo(bp_sp.getX(), bp_sp.getY());
			hitArea.lineTo(bp_ep.getX(), bp_ep.getY());
		} else {
			hitArea.moveTo(tp_sp.getX(), tp_sp.getY());
			hitArea.lineTo(tp_ep.getX(), tp_ep.getY());
		}
		hitArea.lineTo(c_x,tc_y);
		hitArea.closePath();
		
		invalidate();
	}
	/**
	 * 
	 * @param c
	 */
	public void setColor(Color c) {
		this.color = c;
		this.linGradEdge = new LinearGradientPaint(insets.left,insets.top,
				  								   ((float)this.width/4), insets.top + sliceHeight,
				  								   new float[]{0f,1f},
				  								   new Color[]{brighter(color,5000),darker(color,50)});

		this.linGradSurfTop = new LinearGradientPaint(insets.left,insets.top,
			      									  insets.left, planeHeight + insets.top,
			      									  new float[]{0f,1f},
			      									  new Color[]{darker(color,35),brighter(color,10000)});
		this.linGradSurfBot = new LinearGradientPaint(insets.left,height - insets.top - planeHeight,
			      									  insets.left, height - insets.bottom,
			      									  new float[]{0f,1f},
			      									  new Color[]{darker(color,40),brighter(color,5000)});
		this.linGradCyl = new LinearGradientPaint(insets.left,insets.top,
												  this.width-insets.left-insets.right, insets.top,
												  new float[]{0f,.6f},
												  new Color[]{darker(color,5),darker(color,35)});

	}
	/**
	 * 
	 * @param arcStart
	 * @param arcAngle
	 */
	public void setArc(double arcStart, double arcAngle) {
		this.arcStart = arcStart;
		this.arcAngle = arcAngle;
		this.arcEnd = arcStart + arcAngle;
	}
	/**
	 * 
	 * @return
	 */
	public String getLabel() {
		return this.label;
	}
	/**
	 * Gibt den flächenschwerpunkt des oberen Kreisausschnitts zurück
	 * @return
	 */
	public Point2D getCenter() {
		double dir_rad = getDirection() * Math.PI / 180;
		double arc_rad = arcAngle * Math.PI / 180;
		double arcZ_rad = arcZ * Math.PI / 180;
		
		double distance = (4 * (innerWidth / 2) * Math.sin(arc_rad / 2)) / (3*arc_rad);

		double x = 1;
		double y = 1;
		double arc;
		
		if ((arcAngle>0)&&(arcAngle<=90)) { // 1. Quadrant
			arc = dir_rad;
		} else if ((arcAngle>90)&&(arcAngle<=180)) { // 2. Quadrant
			arc = Math.PI - dir_rad;
			x = -1;
		} else if ((arcAngle>180)&&(arcAngle<=270)) { // 3. Quadrant
			arc = dir_rad - Math.PI;
			x= -1;
			y= -1;
		} else { // 4. Quadrant
			arc = 2 * Math.PI - dir_rad;
			y = -1;
		}
		
		x = x * Math.cos(arc) * distance + offset.getX() + c_x;
		y = - Math.sin(arc) * distance + offset.getY() + (height / 2);
		
		y = y*Math.sin(arcZ_rad);
		
		return new Point2D.Float((float)x,(float)y);
	}
	/**
	 * gibt den Haupt-Richtungswinkel des Tortenstücks zurück
	 * @return
	 */
	public double getDirection() {
		return arcStart + arcAngle / 2;
	}
	/**
	 * Rückt das Tortenstück animiert aus demKuchen heraus 
	 * @param offs_h
	 * @param offs_v
	 * @param animated
	 */
	public void disengage(int offs_h, int offs_v, boolean animated) {
		
		if (animated) {
			
			animator.setup(offset.getX(), offset.getY(), offs_h, offs_v, 200, Animator.TYPE.QUADRATIC);
			animator.start();
			
		}
	}
	/**
	 * Rückt das Tortenstück aus demKuchen heraus
	 * 
	 * @param offs_h	der horizontale Abstand
	 * @param offs_v	der vertikale Abstand
	 */
	public void disengage(double offs_h, double offs_v) {
		if ((offs_h==offs_v) && (offs_h==0)) {
			if ((offset.getX()!=0)&&(offset.getY()!=0)) {
				offset.setLocation(0, 0);
				invalidate();
			}
			return;
		}
		
		double arc = getDirection();

		double radius = offs_h;
		double arcZ_rad = arcZ * Math.PI / 180;
		
		double x=0;
		double y=0;
		
		if (arc==0) {
			x = radius;
			y = 0;
		}
		
		// Zielpunkt in der 2-D Ebene
		
		if ((arc>0)&&(arc<=90)) { // 1. Quadrant
			x =   radius * Math.cos(arc * Math.PI / 180);
			y = - radius * Math.sin(arc * Math.PI / 180);
		} else if ((arc>90)&&(arc<=180)) { // 2. Quadrant
			x = - radius * Math.cos((180 - arc) * Math.PI / 180);
			y = - radius * Math.sin((180 - arc) * Math.PI / 180);
		} else if ((arc>180)&&(arc<=270)) { // 3. Quadrant
			x = - radius * Math.cos((arc - 180) * Math.PI / 180);
			y =   radius * Math.sin((arc - 180) * Math.PI / 180);
		} else { // 4. Quadrant
			x =   radius * Math.cos((360 - arc) * Math.PI / 180);
			y =   radius * Math.sin((360 - arc) * Math.PI / 180);
		}
		
		// Zielpunk um den Mittelpunkt drehen, Winkel arcZ 
		
		y = y/Math.sin(arcZ_rad)-offs_v;
	
		
		offset.setLocation(x,y);
		
		invalidate();
	}
	/**
	 * 
	 * @return
	 */
	public BufferedImage get() {
		return pieImage;
	}
	/**
	 * Sets a few Paramaters to adjust the graphic context
	 * @param g
	 */
	private void adjustGraphics(Graphics2D g) {
		g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,RenderingHints.VALUE_FRACTIONALMETRICS_ON);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
	}
	/**
	 * Funktion brighter
	 * 
	 * gibt eine um amount% hellere Farbe zurück
	 * 
	 * @param col
	 * @param amount
	 * @return
	 */
	private Color brighter(Color col, int amount) {
		int r = col.getRed();
		int g = col.getGreen();
		int b = col.getBlue();
		
		amount = Math.abs(amount);
		
		r = (r==0) ? 1 : r;
		g = (g==0) ? 1 : g;
		b = (b==0) ? 1 : b;
		
		r = Math.round(r / 100f * (100 + amount));
		g = Math.round(g / 100f * (100 + amount));
		b = Math.round(b / 100f * (100 + amount));
		
		r = (r<255) ? r : 254;
		g = (g<255) ? g : 254;
		b = (b<255) ? b : 254;
		
		Color ret = new Color(r,g,b,col.getAlpha());
		return ret;
	}
	/**
	 * Funktion darker
	 * 
	 * gibt eine um amount% dunklere Farbe zurück
	 * 
	 * @param col
	 * @param amount
	 * @return
	 */
	private Color darker(Color col, int amount) {
		int r = col.getRed();
		int g = col.getGreen();
		int b = col.getBlue();
		
		if (amount>100) amount = 100;
		if (amount<0) amount = 0;
		
		r = r /100 * (100 - amount);
		g = g / 100 * (100 - amount);
		b = b / 100 * (100 - amount);
		
		r = (r>0) ? r : 0;
		g = (g>0) ? g : 0;
		b = (b>0) ? b : 0;
		
		Color ret = new Color(r,g,b,col.getAlpha());
		
		return ret;
	}
	/**
	 * 
	 * @param angle
	 * @return
	 */
	private double transformArc(double angle) {
		double x = width * Math.cos(angle * Math.PI / 180);
		double y = height * Math.sin(angle * Math.PI / 180);
		
		double ret = Math.atan2(y, x) * 180 / Math.PI;
		
		return (ret < 0) ? ret + 360 : ret;
	}
	/**
	 * 
	 */
	private void drawPieImage() {
		float sx = insets.left;
		float sy = insets.top;
		
		Graphics2D g = pieImage.createGraphics();
		adjustGraphics(g);
		g.setStroke(new BasicStroke(2,BasicStroke.CAP_SQUARE,BasicStroke.JOIN_BEVEL));
		g.setBackground(new Color(0,0,0,0));
		g.clearRect(0, 0, width, height);
		
		g.translate(offset.getX(), offset.getY());
		
		// unten malen
		
		g.setPaint(linGradSurfBot);
		g.fill(bottomPlane);
		g.setPaint(linGradEdge);
		g.draw(bottomPlane);			
		// rechte Schnittfläche
		
		GeneralPath gs = new GeneralPath();
			
		gs.moveTo(c_x, tc_y);
			
		gs.lineTo(tp_sp.getX() , tp_sp.getY());
			
		gs.lineTo(bp_sp.getX(), bp_sp.getY());
			
		gs.lineTo(c_x, bc_y);
			
		gs.closePath();
		
		g.setPaint(darker(color,10));
		g.fill(gs);
		g.setPaint(linGradEdge);
		g.draw(gs);

		// linke Schnittfläche
			
		gs.reset();
		
		gs.moveTo(c_x, tc_y);
		
		gs.lineTo(tp_ep.getX() , tp_ep.getY());
		
		gs.lineTo(bp_ep.getX(), bp_ep.getY());
			
		gs.lineTo(c_x, bc_y);
		
		gs.closePath();
		
		g.setPaint(darker(color,10));
		g.fill(gs);
		g.setPaint(linGradEdge);
		g.draw(gs);
		
		// aussenfläche
		
		gs.reset();
					
		g.setPaint(linGradCyl);
		
		// Hinterschneidung rechts
		if ((arcStart<360)&&(arcAngle>(360-arcStart))) {

			gs.append(new Arc2D.Float(
						sx, sy,
			  			innerWidth,planeHeight,
			  			((float)arcStart),((float)(360-arcStart)),
			  			Arc2D.OPEN
			  		  ).getPathIterator(null),false);
			
			gs.append(new Arc2D.Float(
						sx, sy + planeHeight,
  			 		  	innerWidth,planeHeight,
  			 		    ((float)arcStart),((float)(360-arcStart)),
  			 		    Arc2D.OPEN
  			 		  ).getPathIterator(null),true);
			
			gs.closePath();
						
			// Füllen
			g.fill(gs);
			
			// zweiter Bogenteil
			
			gs.reset();
			
			gs.append(new Arc2D.Float(
						sx, sy,
  			 		    innerWidth, planeHeight,
  			 		    0,((float)(arcAngle-(360-arcStart))),
  			 		    Arc2D.OPEN
  			 		  ).getPathIterator(null),false);
			
			gs.append(new Arc2D.Float(
						sx, sy + innerHeight - planeHeight,
  			 		  	width, planeHeight,
  			 		  	0,((float)(arcAngle-(360-arcStart))),
  			 		    Arc2D.OPEN
  			 		  ).getPathIterator(null),true);
			
			gs.closePath();
			
			// Füllen
			g.fill(gs);
			
		} else if ((arcStart<180)&&(arcEnd>180)) {

			// Hinterschneidung links
			
			gs.append(new Arc2D.Float(
						sx, sy,
  			 		  	innerWidth, planeHeight,
  			 		  	((float)arcStart),((float)(180-arcStart)),
  			 		  	Arc2D.OPEN
  			 		  ).getPathIterator(null),false);
			
			gs.append(new Arc2D.Float(
						sx, sy + innerHeight - planeHeight,
  			 		  	innerWidth, planeHeight,
  			 		  	180,((float)-(180-arcStart)),
  			 		  	Arc2D.OPEN
  			 		  ).getPathIterator(null),true);
			
			gs.closePath();
			
			// Füllen
			
			g.fill(gs);

			// zweiter Teil
	
			gs.reset();
			
			gs.append(new Arc2D.Float(
						sx, sy,
  			 		  	innerWidth, planeHeight,
  			 		  	180,((float)(arcEnd-180)),
  			 		  	Arc2D.OPEN
  			 		  ).getPathIterator(null),false);
			
			gs.append(new Arc2D.Float(
						sx, sy + innerHeight - planeHeight,
  			 		  	innerWidth, planeHeight,
  			 		  	((float)arcEnd),((float)(180-arcEnd)),
  			 		  	Arc2D.OPEN
  			 		  ).getPathIterator(null),true);
			
			gs.closePath();
			
		
			// Füllen
			
			g.draw(gs);

			
		} else {
		
			gs.append(new Arc2D.Float(
						sx, innerHeight - planeHeight + sy,
					  	innerWidth, planeHeight,
					  	((float)arcStart),((float)arcAngle),
					  	Arc2D.OPEN
					  ).getPathIterator(null),false);
			
			gs.append(new Arc2D.Float(
						sx, sy,
		  			  	innerWidth, planeHeight,
		  			  	((float)(arcEnd)),((float)-arcAngle),
		  			  	Arc2D.OPEN
		  			  ).getPathIterator(null),true);
			
			gs.closePath();
					
			// Füllen
			
			g.fill(gs);
		}

		// oben malen
		
		g.setPaint(linGradSurfTop);
		g.fill(topPlane);
		g.setPaint(linGradEdge);
		g.draw(topPlane);
		
		g.dispose();

	}

	@Override
	public void animationDone(Object options) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void animationStart(Object options) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void animationStep(Object options) {
		Point2D p = (Point2D)options;
		
		disengage(p.getX(), p.getY());
	}
}
