package de.grenni.utils;

import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

public class GraphicEffects {
	public static BufferedImage blurImage(BufferedImage img, int radius) {
		int result = radius*radius;
		float nth= 1f / new Float(result);
		float[] kernel = new float[result];
		
		for (int i=0; i<result; i++)
			kernel[i] = nth;
		
        BufferedImage dst = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
	    
		ConvolveOp op = new ConvolveOp(new Kernel(radius, radius, kernel), ConvolveOp.EDGE_ZERO_FILL, null);
        op.filter(img,dst);
        
        return dst;
	}
}
