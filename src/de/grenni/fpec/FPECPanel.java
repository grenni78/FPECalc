package de.grenni.fpec;

//import java.awt.Color;
//import java.awt.Font;
import java.awt.Insets;

import javax.swing.JPanel;

import de.grenni.utils.MyProperties;

public class FPECPanel extends JPanel implements FPECCommunicatorEventListener{

	protected Insets padding;
	protected MyProperties properties = global.properties;
	protected String title;
	
	public FPECPanel(Insets padding) {
		super();
		this.padding = padding;
		global.communicator.addFPECCommunicatorEventListener(this);
	}
	public String getTitle() {
		return this.title;
	}
	@Override public void communicatorEvent(FPECCommunicatorEvent e) {/*empty*/}
}
