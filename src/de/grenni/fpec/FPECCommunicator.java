package de.grenni.fpec;

import javax.swing.event.EventListenerList;


public class FPECCommunicator {
	EventListenerList listenerList;
	FPECCommunicatorEvent communicatorEvent;

	
	public FPECCommunicator() {
		 listenerList = new EventListenerList();
	}
	public void addFPECCommunicatorEventListener(FPECCommunicatorEventListener l) {
	     listenerList.add(FPECCommunicatorEventListener.class, l);
	 }
	
	public void removeFooListener(FPECCommunicatorEventListener l) {
	     listenerList.remove(FPECCommunicatorEventListener.class, l);
	}

	protected void fireFPECCommunicatorEvent(FPECEvents id) {
	     // 
	     Object[] listeners = listenerList.getListenerList();
	     
	     for (int i = listeners.length-2; i>=0; i-=2) {
	         if (listeners[i]==FPECCommunicatorEventListener.class) {
	             if (communicatorEvent == null)
	            	 communicatorEvent = new FPECCommunicatorEvent(this,id);
	             ((FPECCommunicatorEventListener)listeners[i+1]).communicatorEvent(communicatorEvent);
	         }
	     }
	 }

}
