package de.grenni.fpec;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import de.grenni.utils.MyProperties;

public class FPECPanelNutritions extends FPECPanel {
	private JPanel contentPane;
	
	public FPECPanelNutritions(Insets padding) {
		super(padding);
		this.title="Nährwertangaben";
		
		
		//bl = new BoxLayout(this,BoxLayout.X_AXIS);
		//setLayout(bl);
		
		setupUI();
	}
	/**
	 * 
	 * @param c
	 * @param l
	 * @param gbc
	 */
	private void addFieldAndLabel(JPanel target, JComponent c, JLabel l, GridBagConstraints gbc, int labelAlign ) {
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
		
		int rows = 1;
		GridLayout gl = new GridLayout(rows,1,0,15);
		contentPane.setLayout(gl);
		
		GridBagLayout gbl = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();
		
		// Nahrungsmittelauswahl
		JPanel foodPanel = new JPanel();
		foodPanel.setName("NutritionPanel");
		foodPanel.setLayout(new FlowLayout());
		
		
		
		gl.setRows(++rows);
		contentPane.add(foodPanel);
		
		// Nährstoffübersicht
		JPanel nutririonPanel = new JPanel();
		nutririonPanel.setName("NutritionPanel");
		
		gl.setRows(++rows);
		contentPane.add(nutririonPanel);
		
	}
}
