package de.grenni.gui;

import java.awt.Color;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.SwingConstants;

/**
 * Internal class that maintains information about individual Outlook bars;
 * specifically it maintains the following information:
 * 
 * name The name of the bar button The associated JButton for the bar
 * component The component maintained in the Outlook bar
 */
public class JAccordionBarInfo {
	/**
	 * The name of this bar
	 */
	private String name;

	/**
	 * The JButton that implements the Outlook bar itself
	 */
	private JButton button;

	/**
	 * The component that is the body of the Outlook bar
	 */
	private JComponent component;

	/**
	 * Creates a new BarInfo
	 * 
	 * @param name
	 *            The name of the bar
	 * @param component
	 *            The component that is the body of the Outlook Bar
	 */
	public JAccordionBarInfo(String name, JComponent component) {
		this.name = name;
		this.component = component;
		this.button = new JButton(name);
		this.button.setName("JAccordionBarButton");
		this.button.updateUI();
	}

	/**
	 * Creates a new BarInfo
	 * 
	 * @param name
	 *            The name of the bar
	 * @param icon
	 *            JButton icon
	 * @param component
	 *            The component that is the body of the Outlook Bar
	 */
	public JAccordionBarInfo(String name, Icon icon, JComponent component) {
		this.name = name;
		this.component = component;
		this.button = new JButton(name, icon);
		this.button.setName("JAccordionBarButton");
	}

	/**
	 * Returns the name of the bar
	 * 
	 * @return The name of the bar
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Sets the name of the bar
	 * 
	 * @param The
	 *            name of the bar
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns the outlook bar JButton implementation
	 * 
	 * @return The Outlook Bar JButton implementation
	 */
	public JButton getButton() {
		return this.button;
	}
	/**
	 * Returns the component that implements the body of this Outlook Bar
	 * 
	 * @return The component that implements the body of this Outlook Bar
	 */
	public void setComponent(JComponent c) {
		this.component = c;
	}
	/**
	 * Returns the component that implements the body of this Outlook Bar
	 * 
	 * @return The component that implements the body of this Outlook Bar
	 */
	public JComponent getComponent() {
		return this.component;
	}
}