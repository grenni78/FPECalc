package de.grenni.fpec;

import java.awt.AWTEvent;

public class FPECCommunicatorEvent extends AWTEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7952806813347774418L;
	private static final int EVENT_ID = AWTEvent.RESERVED_ID_MAX + 101;
	
	public FPECCommunicatorEvent(Object source, FPECEvents id) {
		super(source, EVENT_ID);
	}

}
