package de.grenni.utils;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Insets;
import java.awt.Rectangle;

import javax.swing.Icon;


public class Dimensions {

	/**
	 * @param text	text to be outlined
	 * @param fm	the FontMetrics of the text
	 * 
	 * @return outline Rectangle
	 */
	public static Rectangle getTextRect(String text, FontMetrics fm) {

		if (text!=null && text.length()>0) {
			Dimension stringDim = getStringSize(text, fm);
			Dimension size = new Dimension(stringDim.width,
										   getTextY(fm, new Rectangle(stringDim)));

			return new Rectangle(size);
		}
		return new Rectangle();
	}

	public static int getTextY(FontMetrics fm, Rectangle rectangle) {
		return rectangle.y + fm.getAscent();
	}

	public static boolean isEmptyString(String str){
		if (str != null && str.length()>0)
			return true;
		else
			return false;
	}
	 public static Dimension getStringSize(String str, FontMetrics fm){
		 
		 return !isEmptyString(str) ? new Dimension(fm.stringWidth(str), fm.getHeight()) : new Dimension();
	 }
	/**
	 * @param icon	Icon to be outlined
	 * @return outline Rectangle
	 */
	public static Rectangle getIconRect(Icon icon) {

		if (icon != null) {
			return new Rectangle(icon.getIconWidth(), icon.getIconHeight());
		}
		return new Rectangle();
	}
	/**
	 * 
	 * @param rect
	 * @param insets
	 * @return rectangle with subtracted Insets
	 */
	public static Rectangle subtractInsets(Rectangle rect,Insets insets) {
		 if (insets == null) {
			 return rect;
		 }
		 rect.setBounds(rect.x + insets.left,
				 	    rect.y + insets.top,
				 	    rect.width - insets.left - insets.right,
				 	    rect.height - insets.top - insets.bottom);
		 return rect;
	}

}
