package de.grenni.fpec;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.LookAndFeel;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.synth.SynthLookAndFeel;

import de.grenni.gui.JAccordion;
import de.grenni.gui.LinkedFonts;
import de.grenni.gui.RoundRectWindow;
import de.grenni.utils.MyProperties;
//import de.grenni.utils.MyProperties.MODE;

public class FPECMain extends RoundRectWindow implements ActionListener{

	/**
	 * @param args
	 */
	private JPanel contentPane;
	
	private JLabel titleBar;
	private JButton exitButton;
	private JButton minimizeButton;
	private JAccordion accordion;
	
	protected MyProperties properties = global.properties;
	
	private FPECPanels panels;
	
	private Image icon;
	
	private Font fontNormal;
	
	// -- Konstruktor --
	@SuppressWarnings("nls")
	public FPECMain() {
		super();
		System.setProperty("file.encoding", "UTF-8");
		setContentPane(new JPanel());
		contentPane = (JPanel)getContentPane();

		contentPane.setLayout(new BorderLayout());

		setName("mainframe");
		
		LookAndFeel laf = UIManager.getLookAndFeel();
		if (laf instanceof SynthLookAndFeel) {
			UIDefaults def = ((SynthLookAndFeel)laf).getDefaults();
			cornerResizeOffset = def.getInt("cornerResizeOffset");
		}
		
		
		//  Appliction Icon setzen
		try {
			this.icon = ImageIO.read(this.getClass().getResourceAsStream("/img/fpec.png"));
			this.setIconImage(this.icon);
		} catch (IOException e1) {
			System.err.println("Application Icon could not be set!");
			e1.printStackTrace();
		}
		
		// Schriftarten setzen
		fontNormal = LinkedFonts.getFont("Vera",10,0);
		setFont(fontNormal);
		setForeground(Color.decode(properties.getString("appearance.mainWindow.foregroundColor","#FFFFFF")));
	
		setupUI();
		
		setSize( 800, 600 );
		//pack();
		
		setLocationRelativeTo( null );
	    setVisible( true );

	}
	// initialisiert alles grafischen Komponenten
	private void setupUI(){
		
		//contentPane.setLayout(null);
		
		//int cornerRadius = properties.getInt("appearance.mainWindow.cornerRadius",10) - 2;
		
		JPanel titleBarContainer = new JPanel();
		titleBarContainer.setName("titlebarContainer");
		titleBarContainer.updateUI();
		titleBarContainer.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

	
		titleBar = new JLabel("<html>FPE-Rechner <small><i>"+global.VERSION+"</i></small></html>");
		titleBar.setName("titlebar");
		titleBar.updateUI();
	
		c.weightx = 1;
		c.anchor = GridBagConstraints.WEST;
		titleBarContainer.add(titleBar,c);
		//titleBar.setPreferredSize(new Dimension(titleBar.getWidth(),70));
		
		if (this.icon != null)
			titleBar.setIcon(new ImageIcon(this.icon));
		titleBar.setIconTextGap(20);
		
		exitButton = new JButton();
		exitButton.setName("exitButton");
		exitButton.updateUI();
		exitButton.setActionCommand("exit");
		exitButton.addActionListener(this);
		
		minimizeButton = new JButton();
		minimizeButton.setName("minimizeButton");
		minimizeButton.updateUI();
		minimizeButton.setActionCommand("minimize");
		minimizeButton.addActionListener(this);
		
		c.anchor = GridBagConstraints.EAST;
		
		titleBarContainer.add(minimizeButton);
			
		titleBarContainer.add(exitButton);
		
		contentPane.add(titleBarContainer, BorderLayout.NORTH);
		
		
		// Accordion
		accordion = new JAccordion();
		
		accordion.setOpaque(false);

		// Panels
		
		panels = new FPECPanels(properties, new Insets(0,0,5,3));
		FPECPanel[] panelArray = panels.getAll();
		
		JPanel padding;
		
		for (int i=0; i< panelArray.length; i++) {
			// Set the optical Padding 
			padding = new JPanel(new BorderLayout());
			padding.setName("roundedEdgePanel");

			padding.add(panelArray[i]);
			// Add Panel to Accordion
			accordion.addBar(panelArray[i].getTitle(),padding);

		}
		
		JPanel accordionContainer = new JPanel();
		accordionContainer.setBorder(new EmptyBorder(10,20,10,20));
		accordionContainer.setLayout(new BorderLayout());
		accordionContainer.add(accordion,BorderLayout.CENTER);
		
		contentPane.add(accordionContainer, BorderLayout.CENTER);
		
		Dimension dim = titleBar.getPreferredSize();
		dim.width = 320;
		setMinimumSize(dim);
	}
	// Fenster minimieren
	public void iconify() {
		int state = getExtendedState();
		// Set the iconified bit
		state |= Frame.ICONIFIED;
		// Iconify the frame
		setExtendedState(state); 
	}	// Fenster wieder herstellen
	public void deiconify() {
		int state = getExtendedState();
		// Clear the iconified bit
		state &= ~Frame.ICONIFIED;
		// Deiconify the frame
		setExtendedState(state); 
	}
	@Override
	public void componentResized(ComponentEvent arg0) {
		//layMeOut();
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		String action = e.getActionCommand();
	
		if (action=="exit")
			System.exit(0);
		else if (action=="minimize")
			iconify();
		
	}
}
