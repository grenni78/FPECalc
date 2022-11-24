package de.grenni.fpec;

import java.util.EventListener;

public interface FPECCommunicatorEventListener extends EventListener {
	public void communicatorEvent(FPECCommunicatorEvent e);
}
