package de.grenni.fx;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;

import javax.swing.Timer;

public class TranslationAnimator implements Animator, ActionListener {
	AnimatorCallback manager;
	
	private Timer animTimer;
	
	private float stepSize;
	private double fact;
	private int steps;
	private int currentStep;
	
	private Point2D destination;
	private Point2D currentPos;
	
	/**
	 * 
	 * @param manager
	 */
	public TranslationAnimator(AnimatorCallback manager) {
		this.manager = manager;
		this.destination = new Point2D.Float();
		animTimer = new javax.swing.Timer(50,this);
		animTimer.setInitialDelay(0);
	}
	/**
	 * Initialisiert die Animation
	 */
	@Override
	public void setup(double sx, double sy, double ex, double ey, int steps, TYPE type) {
		if (animTimer.isRunning()) return;
		
		if (type == TYPE.QUADRATIC) {
			fact = ( (ey - sy) / Math.pow((ex-sx), 2) );
			stepSize = (float) ((ex-sx) / steps);
			this.destination.setLocation(ex,ey);
			this.currentStep = 0;			
		}
		
	}
	/**
	 * Gibt die aktuelle Position zurück
	 * @return
	 */
	public Point2D getCurrentPosition() {
		return currentPos;
	}
	/**
	 * Startet die Animation
	 */
	public void start() {
		animTimer.start();
		manager.animationStart(null);
	}
	/**
	 * 
	 */
	@Override
	public Point2D step() {
		float x = stepSize * currentStep++;
		float y = (float) (fact * Math.pow(x,2));
		return new Point2D.Float(x,y);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (currentStep < steps) {
			currentPos = step();
			manager.animationStep(currentPos);
		} else {
			animTimer.stop();
			manager.animationStep(destination);
			manager.animationDone(null);
		}
		
	}

}
