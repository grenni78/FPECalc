package de.grenni.gui;

//Import the GUI classes
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

//Import the Java classes
import java.util.*;

/**
 * A JOutlookBar provides a component that is similar to a JTabbedPane, but
 * instead of maintaining tabs, it uses Outlook-style bars to control the
 * visible component
 */
public class JAccordion extends JPanel implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2805105709669118213L;

	private Insets insets = new Insets(10,20,10,20);
	private JPanel padding = new JPanel(new GridLayout(1,1));
	/**
	 * The top panel: contains the buttons displayed on the top of the
	 * JOutlookBar
	 */
	private JPanel topPanel = new JPanel(new GridLayout(1, 1));

	/**
	 * The bottom panel: contains the buttons displayed on the bottom of the
	 * JOutlookBar
	 */
	private JPanel bottomPanel = new JPanel(new GridLayout(1, 1));

	/**
	 * A LinkedHashMap of bars: we use a linked hash map to preserve the order
	 * of the bars
	 */
	private Map<String, JAccordionBarInfo> bars = new LinkedHashMap<String, JAccordionBarInfo>();

	/**
	 * The currently visible bar (zero-based index)
	 */
	private int visibleBar = 0;

	/**
	 * A place-holder for the currently visible component
	 */
	private JComponent visibleComponent = null;

	/**
	 * Creates a new JOutlookBar; after which you should make repeated calls to
	 * addBar() for each bar
	 */
	public JAccordion() {
		this.setLayout(new BorderLayout());
		this.add(topPanel, BorderLayout.NORTH);
		this.add(bottomPanel, BorderLayout.SOUTH);
		padding.setBorder(new EmptyBorder(insets));
		this.add(padding, BorderLayout.CENTER);
		//this.topPanel.setOpaque(false);
		//this.bottomPanel.setOpaque(false);
	}

	public JAccordionBarInfo getBar(String name) {
		return bars.get(name);
	}

	/**
	 * Adds the specified component to the JOutlookBar and sets the bar's name
	 * 
	 * @param name
	 *            The name of the outlook bar
	 * @param componenet
	 *            The component to add to the bar
	 */
	public JAccordionBarInfo addBar(String name, JComponent component) {
		JAccordionBarInfo barInfo = new JAccordionBarInfo(name, component);
		barInfo.getButton().addActionListener(this);
		this.bars.put(name, barInfo);
		render();
		return barInfo;
	}

	/**
	 * Adds the specified component to the JOutlookBar and sets the bar's name
	 * 
	 * @param name
	 *            The name of the outlook bar
	 * @param icon
	 *            An icon to display in the outlook bar
	 * @param componenet
	 *            The component to add to the bar
	 */
	public void addBar(String name, Icon icon, JComponent component) {
		JAccordionBarInfo barInfo = new JAccordionBarInfo(name, icon, component);
		barInfo.getButton().addActionListener(this);
		this.bars.put(name, barInfo);
		render();
	}

	/**
	 * Removes the specified bar from the JOutlookBar
	 * 
	 * @param name
	 *            The name of the bar to remove
	 */
	public void removeBar(String name) {
		this.bars.remove(name);
		render();
	}

	/**
	 * Returns the index of the currently visible bar (zero-based)
	 * 
	 * @return The index of the currently visible bar
	 */
	public int getVisibleBar() {
		return this.visibleBar;
	}

	/**
	 * Programmatically sets the currently visible bar; the visible bar index
	 * must be in the range of 0 to size() - 1
	 * 
	 * @param visibleBar
	 *            The zero-based index of the component to make visible
	 */
	public void setVisibleBar(int visibleBar) {
		if (visibleBar > 0 && visibleBar < this.bars.size() - 1) {
			this.visibleBar = visibleBar;
			render();
		}
	}
	/*
	 * gets current Pannels preferred Size
	 */
	public int getPannelsPreferredHeight() {
		return this.visibleComponent.getPreferredSize().height;
	}
	/**
	 * Causes the outlook bar component to rebuild itself; this means that it
	 * rebuilds the top and bottom panels of bars as well as making the
	 * currently selected bar's panel visible
	 */
	public void render() {
		// Compute how many bars we are going to have where
		int totalBars = this.bars.size();
		int topBars = this.visibleBar + 1;
		int bottomBars = totalBars - topBars;

		// Get an iterator to walk through out bars with
		Iterator<String> itr = this.bars.keySet().iterator();

		// Render the top bars: remove all components, reset the GridLayout to
		// hold to correct number of bars, add the bars, and "validate" it to
		// cause it to re-layout its components
		this.topPanel.removeAll();
		GridLayout topLayout = (GridLayout) this.topPanel.getLayout();
		topLayout.setRows(topBars);
		JAccordionBarInfo barInfo = null;
		for (int i = 0; i < topBars; i++) {
			String barName = (String) itr.next();
			barInfo = (JAccordionBarInfo) this.bars.get(barName);
			this.topPanel.add(barInfo.getButton());
		}
		this.topPanel.validate();

		// Render the center component: remove the current component (if there
		// is one) and then put the visible component in the center of this
		// panel
		if (this.visibleComponent != null) {
			padding.remove(this.visibleComponent);
		}
		this.visibleComponent = barInfo.getComponent();
		padding.add(visibleComponent);

		// Render the bottom bars: remove all components, reset the GridLayout
		// to
		// hold to correct number of bars, add the bars, and "validate" it to
		// cause it to re-layout its components
		this.bottomPanel.removeAll();
		GridLayout bottomLayout = (GridLayout) this.bottomPanel.getLayout();
		bottomLayout.setRows(bottomBars);
		for (int i = 0; i < bottomBars; i++) {
			String barName = (String) itr.next();
			barInfo = (JAccordionBarInfo) this.bars.get(barName);
			this.bottomPanel.add(barInfo.getButton());
		}
		this.bottomPanel.validate();

		// Validate all of our components: cause this container to re-layout its
		// subcomponents
		validate();
	}

	/**
	 * Invoked when one of our bars is selected
	 */
	public void actionPerformed(ActionEvent e) {
		int currentBar = 0;
		for (Iterator<String> i = this.bars.keySet().iterator(); i.hasNext();) {
			String barName = (String) i.next();
			JAccordionBarInfo barInfo = (JAccordionBarInfo) this.bars.get(barName);
			if (barInfo.getButton() == e.getSource()) {
				// Found the selected button
				this.visibleBar = currentBar;
				render();
				return;
			}
			currentBar++;
		}
	}

}
