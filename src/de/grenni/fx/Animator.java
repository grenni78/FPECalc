package de.grenni.fx;

import java.awt.geom.Point2D;

public interface Animator {
	public enum TYPE {QUADRATIC};
	public void setup(double sx, double sy, double ex, double ey, int steps, TYPE type);
	public Point2D step();
}
