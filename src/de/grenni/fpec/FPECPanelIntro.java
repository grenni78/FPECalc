package de.grenni.fpec;

import java.awt.BorderLayout;
//import java.awt.Color;
//import java.awt.Dimension;
//import java.awt.Font;
import java.awt.Insets;
//import java.awt.Rectangle;
import java.io.IOException;

import javax.swing.JEditorPane;
//import javax.swing.JPanel;
import javax.swing.JScrollPane;
//import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.text.html.HTMLEditorKit;


//import de.grenni.gui.RoundedShapeScrollBar;
//import de.grenni.utils.MyProperties;

public class FPECPanelIntro extends FPECPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4378234066975006772L;
	private JEditorPane intro;


	public FPECPanelIntro(Insets padding) {
		super(padding);
		
		this.title = "Theorie zu Fett- und Eiweißberechnung in der Diabetologie";

		setBorder(new EmptyBorder(padding));
		setLayout(new BorderLayout());

		initControls();
	}
	private void initControls() {
		intro = new JEditorPane();
		intro.setEditable(false);
		intro.setEditorKit(new HTMLEditorKit());

		try {

			intro.setContentType("text/html; charset=UTF-8");
			intro.read(this.getClass().getResourceAsStream("/res/intro.htm"), "intro.htm");
			

		} catch (IOException e) {
			e.printStackTrace();
		}
		
		JScrollPane introScrollPane = new JScrollPane(intro);
		
		add(introScrollPane);
	}
}
