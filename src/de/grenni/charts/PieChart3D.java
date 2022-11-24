package de.grenni.charts;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class PieChart3D extends JPanel implements MouseListener, MouseMotionListener{
	private int width;
	private int height;
	private float arcZ;
	private float radius;
	private Point3D center;
	
	private HashMap<String,PieSlice3D> slices;
	private HashMap<String,JLabel> slicesLabels;
	private TreeMap<Double,String> slicesSort;
	
	/**
	 * 
	 * @param width
	 * @param height
	 * @param arc
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public PieChart3D(int width, int height, float arcZ) {
		super();
		slices = new HashMap();
		slicesLabels = new HashMap();
		this.width = width;
		this.height = height;
		this.arcZ = arcZ;
		this.radius = width / 2f;
		
		setOpaque(false);
		
		addMouseListener(this);
	}
	/**
	 * 
	 * @param key
	 * @return
	 */
	public PieSlice3D getSlice(String key) {
		if ((key==null) || (key.length()==0))
			return null;
		else
			return slices.get(key);
	}
	/**
	 * 
	 * @param labels
	 * @param colors
	 * @param values
	 */
	public void setValues(String[] labels, Color[] colors, float[] values) {
		float sum = 0;
		float arc = 0;
		double arcPerStep = 1;
		
		int i;
		double arcAngle;
		double arcStart = 0;
		
		slices.clear();
		slicesLabels.clear();
		
		for (i=0; i<labels.length; i++) {
			sum += values[i];
		}
		
		arcPerStep = 360 / sum;
		
		for (i=0; i<labels.length; i++) {
			arcAngle = values[i] * arcPerStep;
			
			slices.put(labels[i],new PieSlice3D(arcZ,width,height,new Insets(10,10,10,10),arcStart,arcAngle,colors[i],labels[i],this));
			slicesLabels.put(labels[i],new JLabel(labels[i]));
			
			arcStart += arcAngle;
		}
		sortSlices();
	}
	
	/**
	 * 
	 */
	private void sortSlices() {
		
		slicesSort = new TreeMap<Double, String>();
		
		Iterator<PieSlice3D> pieces = slices.values().iterator();
		
		PieSlice3D slice;
		
		while (pieces.hasNext()) {
			slice = pieces.next();
			slicesSort.put(-Math.sin(slice.getDirection() * Math.PI / 180),
						   slice.getLabel());
		}
	}
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(width,height);
	}
	@Override
	public void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D)g;
		
		PieSlice3D pieSlice;
		GeneralPath gp = new GeneralPath();
		
		Point2D center;
		float x;
		float y;
		
		Iterator<String> pieces = slicesSort.values().iterator();
		//g2d.setColor(new Color(0,0,0,0));	
		
		//g.fillRect(0, 0, width, height);
		
		while (pieces.hasNext()) {
			pieSlice = slices.get(pieces.next());
			
			g2d.drawImage(pieSlice.get(), 0, 0, null);
			
			center = pieSlice.getCenter();
			
			x = (float)center.getX();
			y = (float)center.getY();
			
			gp.reset();
			gp.moveTo(x,y);
			gp.lineTo(((x>(width/2)) ? x+10 : x-10) ,y - 10);
			gp.lineTo(((x>(width/2)) ? x+50 : x-50) ,y - 10);
			g2d.fillOval((int)Math.round(center.getX()), (int)Math.round(center.getY()), 6, 6);
			g2d.draw(gp);
		}
		
		//g.drawImage(slices[0].get(), 0, 0, null);
	}
	@Override
	public void mouseClicked(MouseEvent e) {
		System.out.println("click"+e);
		Point location = e.getPoint();
		
		/*
		Iterator<PieSlice3D> pieSlice = slices.values().iterator();
		while (pieSlice.hasNext()) {
			pieSlice.next().disengage(0,0,true);
		}
		*/
		
		Iterator<String> pieces = slicesSort.values().iterator();

		PieSlice3D piece;
		
		while (pieces.hasNext()) {
			piece = slices.get(pieces.next());
		
			if (piece.hit(location.x,location.y)) {
				piece.disengage(10, 10,true);
			} else {
				piece.disengage(0,0,true);
			}
		}
		
	}
	
	@Override
	public void mouseMoved(MouseEvent e) {

		
	}
	@Override public void mouseDragged(MouseEvent arg0) { /*empty*/ }
	@Override public void mouseEntered(MouseEvent arg0) { /*empty*/ }
	@Override public void mouseExited(MouseEvent arg0) { /*empty*/ }
	@Override public void mousePressed(MouseEvent arg0) { /*empty*/ }
	@Override public void mouseReleased(MouseEvent arg0) { /*empty*/ }
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		JFrame main = new JFrame("3d test");
		main.setLayout(new GridLayout(1,1));
		main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		PieChart3D pc = new PieChart3D(320,200,45);
		pc.setValues(new String[]{"Fett","Protein","Kohlenhydrate","xyz"},
					 new Color[] {new Color(0xff,0xff,0x00,0x85),new Color(00,0xff,0x00,0x85),new Color(0xff,0x00,0x00,0x85),new Color(0x00,0xFF,0xFF,0x85)},
					 new float[]{1f, 1f, 1f, 1f});
		PieSlice3D ps = pc.getSlice("Fett");
		//ps.disengage(20,20,true);
		ps.invalidate();
		main.add(pc);
		main.pack();
		main.setVisible(true);
	}


}
