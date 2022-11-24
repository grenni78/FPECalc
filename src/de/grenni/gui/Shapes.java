package de.grenni.gui;

import java.awt.Shape;
import java.awt.geom.GeneralPath;

public class Shapes {
	/*
	 * Funktion roundRect
	 * gibt ein Recheck mit beliebig gerundeten Kanten aus
	 * @param x,y Startpunkt
	 * @param width,height Endpunkt
	 * @param r1 Radius oben links
	 * @param r2 Radius oben rechts
	 * @param r3 Radius unten links
	 * @param r4 Radius unten rechts
	 * @return java.awt.Shape
	 */
	public static Shape roundRect(int x,int y,int width, int height, int r1, int r2, int r3, int r4) {
		GeneralPath gs = new GeneralPath();
		
		gs.moveTo(x + r1    , y);
		gs.lineTo(width - r2, y);
		// oben rechts
		gs.quadTo(width, y, width, y + r2);
		gs.lineTo(width, height - r3);
		// unten rechts
		gs.quadTo(width, height, width - r3, height);
		gs.lineTo(x+r4, height);
		// unten links
		gs.quadTo(x, height, x, height - r4);
		// oben links
		gs.lineTo(x, y + r1);
		gs.quadTo(x, y, x + r1, y);
		return gs;
	}
}
