package de.grenni.laf.rich;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LinearGradientPaint;
import java.awt.Graphics;
import java.awt.LinearGradientPaint;
import java.awt.Point;

import javax.swing.border.EmptyBorder;
import javax.swing.plaf.synth.SynthContext;
import javax.swing.plaf.synth.SynthPainter;
import javax.swing.plaf.synth.SynthStyle;

public class GradientPainter extends SynthPainter {
	private LinearGradientPaint gp;
	private Font font;
	private SynthStyle style;
	
	@Override
	public void paintRootPaneBackground(SynthContext context,Graphics g, int x, int y,int w, int h) {
		Graphics2D g2d = (Graphics2D)g;
		
		style = context.getStyle();
		
		Insets insets = (Insets)style.get(context, "insets");
		
		x = x + insets.left;
		y = y + insets.top;
		w = w - insets.right;
		h = h - insets.bottom;
		
		font = style.getFont(context);
		
		g2d.setFont(font);
		

		String[] colorsStr = style.getString(context,"gradientColors","").split(",");
		String[] fractionsStr = style.getString(context,"gradientFractions","").split(",");
	
		float[] fractions = new float[colorsStr.length];
		Color[] colors = new Color[colorsStr.length];
			
		for (int i=0; i<colorsStr.length; i++)
			try {
				fractions[i] = Integer.parseInt(fractionsStr[i]) / 100f;
				colors[i] = Color.decode(colorsStr[i]);
			} catch (NumberFormatException e) {
				
			}
			
		gp = new LinearGradientPaint(new Point(x,y),
									 new Point(x,y+h),
									 fractions,
									 colors);

		int cornerRadius = style.getInt(context, "cornerRadius", 0);
		
		g2d.setPaint(gp);
		
		g2d.fillRoundRect(x, y, w, h, cornerRadius, cornerRadius);
		
	}

}
