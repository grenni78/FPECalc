package de.grenni.gui;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class LinkedFonts {
	
	public static Font getFont(String fontName, float size, int style){
		switch (style){
			case Font.BOLD:
				fontName += "Bd";
				break;
			case Font.ITALIC:
				fontName += "It";
				break;
			case Font.BOLD+Font.ITALIC:
				fontName += "BI";
				break;
			default:
				break;
		}
		fontName += ".ttf";
		try {
			//FileInputStream is = new FileInputStream("res/"+fontName);
			InputStream is = LinkedFonts.class.getResourceAsStream("/res/"+fontName);
			Font font = Font.createFont(Font.TRUETYPE_FONT, is);
			Font finalFont = font.deriveFont(size);
			return finalFont;
		} catch(FileNotFoundException e) {
			System.err.println("Font "+fontName+" nicht gefunden!");
		} catch (FontFormatException f) {
			System.err.println("Font "+fontName+" hat falsches Format!");
		} catch (IOException g) {
			System.err.println("IO Fehler!");
			
		}
		
		return null;		
	}
}
