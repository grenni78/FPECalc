package de.grenni.fpec;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.text.NumberFormatter;

import de.grenni.gui.SlidingToggler;
import de.grenni.utils.MyProperties;

public class FPECPanelSettings extends FPECPanel implements ActionListener{
	private JPanel contentPane;
	
	private SlidingToggler toggle;
	private JLabel toggleLabel;
	private JLabel ke_beLabel;
	private JFormattedTextField ke_be;
	private JLabel fpeLabel;
	private JFormattedTextField fpe;
	private JLabel userDataLocationLabel;
	private JRadioButton userDataLocationAppDir;
	private JRadioButton userDataLocationHomeDir;

	
	private final static SlidingToggler.POSITION KE = SlidingToggler.POSITION.RIGHT;
	private final static SlidingToggler.POSITION BE = SlidingToggler.POSITION.LEFT;
	
	private String ke_color;
	private final static String ke_unit = "KE";
	private String be_color;

	private final static String be_unit = "BE";
	
	private final static String KH_UNIT_LABEL_TEXT = "Einheit für die Kohlenhydrat-Berechnung:";
	private final static String KE_BE_LABEL_TEXT = "<html><span color=\"{COLOR}\"><b>{UNIT}</b></span>-Faktor:</html>";
	private final static String FPE_LABEL_TEXT = "<html><b>FPE</b>-Faktor:</html>";
	private static final String USER_DATA_LOCATION_LABEL_TEXT = "Speicherort der Benutzerdaten:";
	private static final String USER_DATA_LOCATION_APPDIR_LABEL_TEXT = "Verzeichnis, in dem sich diese Applikation befindet";
	private static final String USER_DATA_LOCATION_HOMEDIR_LABEL_TEXT = "Heimatverzeichnis des aktuellen Benutzers";
	/**
	 * 
	 * @param properties
	 * @param padding
	 */
	public FPECPanelSettings(Insets padding) {
		super(padding);
		this.title="Einstellungen";

		ke_color = properties.getString("appearance.panels.FPECPanelSettings.keColor", "#2222FF");
		be_color = properties.getString("appearance.panels.FPECPanelSettings.beColor", "#22FF22");
		
		setupUI();
	}
	private void addFieldAndLabel(JComponent c, JLabel l, GridBagConstraints gbc) {
		addFieldAndLabel(c,l,gbc,GridBagConstraints.EAST);
	}
	
	/**
	 * 
	 * @param c
	 * @param l
	 * @param gbc
	 */
	private void addFieldAndLabel(JComponent c, JLabel l, GridBagConstraints gbc, int labelAlign ) {
		gbc.gridy++;
				
		gbc.gridx = 0;
		gbc.anchor = labelAlign;
		contentPane.add(l,gbc);
		
		gbc.gridx = 1;
		gbc.anchor = GridBagConstraints.WEST;
		contentPane.add(c,gbc);
	}
	/**
	 * 
	 */
	private void setupUI() {
		contentPane = new JPanel();
		
		JScrollPane scrollPane = new JScrollPane(contentPane);
		setLayout(new BorderLayout());
		add(scrollPane,BorderLayout.CENTER);
		
		GridBagLayout gbl = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();
		
		gbc.insets = new Insets(0 , 5 , 15 ,5);
		
		contentPane.setLayout(gbl);
		
		EmptyBorder labelPadding = new EmptyBorder(0, 0, 0, 10);

		toggle = new SlidingToggler("/img/ke-be-switch.png",4,60);
		toggle.setPosition(getKHMode());
		
		toggle.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				toggleKE_BE();
			}
		});

		toggleLabel = new JLabel(KH_UNIT_LABEL_TEXT);
		toggleLabel.setLabelFor(toggle);
		toggleLabel.setName("PanelLabel");
		
		addFieldAndLabel(toggle,toggleLabel,gbc);
		
		NumberFormatter decimalFormatter = new NumberFormatter(new DecimalFormat("0.00"));

		ke_be = new JFormattedTextField(decimalFormatter);
		ke_be.setColumns(3);
		ke_be.setMaximumSize(ke_be.getPreferredSize());
		ke_be.setValue(getKE_BEValue());
		ke_be.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent fe) {
                ke_beChanged();
            }
            public void focusGained(FocusEvent fe) {}
		});
		ke_be.addKeyListener(new KeyListener(){
			@Override
			public void keyPressed(KeyEvent arg0) {
				if (arg0.getKeyCode() == 10)
					ke_beChanged();
			}
			@Override public void keyReleased(KeyEvent arg0) {/*empty*/}
			@Override public void keyTyped(KeyEvent arg0) {/*empty*/}		
		});
		
		ke_beLabel = new JLabel();
		setKe_beLabelText();
		ke_beLabel.setLabelFor(ke_be);
		ke_beLabel.setName("PanelLabel");
		
		addFieldAndLabel(ke_be,ke_beLabel,gbc);
		
		fpe = new JFormattedTextField(decimalFormatter);
		fpe.setColumns(3);
		fpe.setMaximumSize(fpe.getPreferredSize());
		fpe.setValue(getFPEValue());
		fpe.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent fe) {
                fpeChanged();
            }
            public void focusGained(FocusEvent fe) {}
		});
		fpe.addKeyListener(new KeyListener(){
			@Override
			public void keyPressed(KeyEvent arg0) {
				if (arg0.getKeyCode() == 10)
					fpeChanged();
			}
			@Override public void keyReleased(KeyEvent arg0) {/*empty*/}
			@Override public void keyTyped(KeyEvent arg0) {/*empty*/}		
		});
		
		fpeLabel = new JLabel(FPE_LABEL_TEXT);
		fpeLabel.setLabelFor(fpe);
		fpeLabel.setName("PanelLabel");
		
		addFieldAndLabel(fpe,fpeLabel,gbc);
		
		JPanel userDataLocationPanel = new JPanel(new GridLayout(2,1,0,5)); 
		ButtonGroup userDataLocation = new ButtonGroup();
		
		userDataLocationAppDir = new JRadioButton(USER_DATA_LOCATION_APPDIR_LABEL_TEXT);
		userDataLocationAppDir.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				setDataDir(FPECConstants.DATADIR_APPDIR);
			}	
		});
		
		userDataLocationPanel.add(userDataLocationAppDir);
		
		userDataLocationHomeDir = new JRadioButton(USER_DATA_LOCATION_HOMEDIR_LABEL_TEXT);
		userDataLocationHomeDir.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				setDataDir(FPECConstants.DATADIR_HOME);
			}	
		});
		
		userDataLocationPanel.add(userDataLocationHomeDir);
		
		userDataLocation.add(userDataLocationAppDir);
		userDataLocation.add(userDataLocationHomeDir);
		
		userDataLocationLabel = new JLabel(USER_DATA_LOCATION_LABEL_TEXT); 
		userDataLocationLabel.setName("PanelLabel");
		userDataLocationLabel.setAlignmentY(TOP_ALIGNMENT);

		getDataDir();
		
		addFieldAndLabel(userDataLocationPanel,userDataLocationLabel,gbc,GridBagConstraints.NORTHEAST);

	}
	/**
	 * 
	 * @param sourceFile
	 * @param targetDir
	 */
	private void moveFile(String sourceFile, String targetDir) {
		
		File file = new File(sourceFile);
		
		File dir = new File(targetDir);
		
		file.renameTo(new File(dir, file.getName())); 
	}
	/**
	 * 
	 * @param directory
	 * @param mask
	 * @return
	 */
	private File[] findFiles(String directory, final String mask) {
		File fileDir = new File(directory);
		File[] arrFile = fileDir.listFiles(new FilenameFilter()
		{
			public boolean accept(File dir, String name)
			{
				return (name.toLowerCase().matches(mask));
			}
		});
		return arrFile;
	}
	/**
	 * 
	 * @param sourceDir
	 * @param destDir
	 */
	protected void moveDataFiles(File sourceDir, File destDir) {
		File[] targetFiles = findFiles(sourceDir.getAbsolutePath(),"config\\..*");
		for (int i=0; i<targetFiles.length; i++)
			moveFile(targetFiles[i].getAbsolutePath(),destDir.getAbsolutePath());
	}
	/**
	 * 
	 * @param datadirAppdir
	 */
	protected void setDataDir(FPECConstants datadir) {
		File configDir = new File(global.properties.getUsersHomeDirectory()
		 						+ File.separator
		 						+ ".fpec");
		
		File appDir = new File(global.properties.getApplicationDirectory());
		
		switch (datadir) {
		case DATADIR_HOME:
			
			if (!configDir.exists())
				configDir.mkdir();
			
			moveDataFiles(appDir,configDir);
			
			try {
				new File(appDir.getAbsolutePath()+File.separator+global.DATADIR_REDIRECT_FILE_NAME).createNewFile();
			} catch (IOException e) {
				if (global.debug)
					e.printStackTrace();
			}
			
			global.userDataLocation = FPECConstants.DATADIR_HOME;
			properties.put("settings.userDataLocation", "HOME_DIR");
			break;
		case DATADIR_APPDIR:
			
			global.userDataLocation = FPECConstants.DATADIR_APPDIR;
			properties.put("settings.userDataLocation", "APP_DIR");	
			
			if (configDir.exists()) {
				moveDataFiles(configDir,appDir);
			}

			new File(appDir.getAbsolutePath()+File.separator+global.DATADIR_REDIRECT_FILE_NAME).delete();

			break;
		}
		
	}
	/**
	 * 
	 * @return
	 */
	protected FPECConstants getDataDir() {
		switch (global.userDataLocation) {
		case DATADIR_HOME:
			userDataLocationHomeDir.setSelected(true);
			break;
		case DATADIR_APPDIR:
			userDataLocationAppDir.setSelected(true);
			break;
		}
		return global.userDataLocation;
	}
	/**
	 * 
	 */
	private void setKe_beLabelText() {
		if (ke_beLabel==null) return;
		
		String result = KE_BE_LABEL_TEXT;
		
		if (properties.getString("settings.kh.mode","KE").equals("KE")) {
			result = result.replace("{COLOR}",ke_color);		
			result = result.replace("{UNIT}",ke_unit);
		} else {
			result = result.replace("{COLOR}",be_color);
			result = result.replace("{UNIT}",be_unit);
		}
		
		ke_beLabel.setText(result);
		ke_beLabel.invalidate();
	}
	/**
	 * 
	 * @return
	 */
	private SlidingToggler.POSITION getKHMode() {
		String mode = properties.getString("settings.kh.mode","KE");
		
		setKe_beLabelText();
		
		if (mode.equals("BE"))
			return BE;
		else
			return KE;
	}
	/**
	 * 
	 * @param mode
	 */
	protected void setKHMode(SlidingToggler.POSITION mode) {
		properties.put("settings.kh.mode",(mode==KE) ? "KE" : "BE");
		setKe_beLabelText();
	}
	/**
	 * 
	 */
	private void setKE_BEValue(double value) {
		properties.put("settings.kh.factor", value);
	}
	/**
	 * 
	 * @return
	 */
	private double getKE_BEValue() {
		return Double.parseDouble(properties.getString("settings.kh.factor", "0.5"));
	}
	private void setFPEValue(double value) {
		properties.put("settings.fp.factor", value);
	}
	/**
	 * 
	 * @return
	 */
	private double getFPEValue() {
		return Double.parseDouble(properties.getString("settings.fp.factor", "0.5"));
	}
	/**
	 * 
	 */
	protected void fpeChanged() {
		try {
			fpe.commitEdit();
		} catch (ParseException e) {
			if (global.debug)
				e.printStackTrace();
		}
		setFPEValue(((Double) fpe.getValue()));
		properties.saveParts();
		global.communicator.fireFPECCommunicatorEvent(FPECEvents.SETTINGS_CHANGED);
	}
	/**
	 * 
	 */
	protected void ke_beChanged() {
		try {
			ke_be.commitEdit();
		} catch (ParseException e) {
			if (global.debug)
				e.printStackTrace();
		}
		setKE_BEValue(((Double)ke_be.getValue()));
		properties.saveParts();
		global.communicator.fireFPECCommunicatorEvent(FPECEvents.SETTINGS_CHANGED);		
	}
	/**
	 * 
	 */
	protected void toggleKE_BE() {
		SlidingToggler.POSITION mode = toggle.getPosition();
		
		// umgekehr, weil das Event vor Verarbeitung gesended wird
		if (mode==KE)
			setKHMode(BE);
		else
			setKHMode(KE);

		properties.saveParts();
		global.communicator.fireFPECCommunicatorEvent(FPECEvents.SETTINGS_CHANGED);		
	}
	/**
	 * 
	 */
	public Dimension getMaximumSize() {
		return getPreferredSize();
	}
	/**
	 * 
	 */
	public Dimension getMinimumSize() {
		return getPreferredSize();
	}
	@Override
	public void actionPerformed(ActionEvent arg0) {
		System.out.println("command: "+arg0.getActionCommand());
		System.out.println("source: "+arg0.getSource());
		System.out.println("class: "+arg0.getClass());
		
	}

}
