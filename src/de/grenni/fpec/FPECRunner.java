package de.grenni.fpec;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.synth.SynthLookAndFeel;

import de.grenni.utils.MyProperties;
import de.grenni.utils.MyProperties.MODE;

public class FPECRunner {
	// -- Main method ---
	public static void main(String[] args) {
		System.out.println("FPECalc version "+global.VERSION);
		System.out.println("loading...");
		
		global.properties = new MyProperties(MODE.FILE,"fpec");
		
		String configFile = "";
		
		String appDir = global.properties.getApplicationDirectory();
		
		File redirFile = new File(appDir + File.separator + global.DATADIR_REDIRECT_FILE_NAME);
		
		// schauen, ob das Homeverzeichniss, oder das Programmverzeichniss genutzt werden soll
		if (!redirFile.exists()) {
			
			global.userDataLocation = FPECConstants.DATADIR_APPDIR;
			
			configFile = appDir 
			           + File.separator 
			           + global.CONFIG_FILE_NAME;
			
		} else {
			
			global.userDataLocation = FPECConstants.DATADIR_HOME;
			
			configFile = global.properties.getUsersHomeDirectory()
					   + File.separator
					   + ".fpec"
					   + File.separator
					   + global.CONFIG_FILE_NAME;
			
		}
		
		// Kommandozeile verarbeiten
		for (int i=0; i<args.length; i++) {
			if (args[i].equals("--debug")|args[i].equals("-d")) {
				System.out.println("Debugging enabled.");
				global.debug = true;
			} else if (args[i].equals("--config-dir")|args[i].equals("-cd")) {
				configFile = args[++i];
			} else {
				String help = "FPE Rechner Version "+global.VERSION+"\n"
							 +"\n"
							 +"Mögliche Kommandozeilenoptionen:\n"
							 +"\t--config-dir\t-cd\tPfad zur Konfigurationsdatei\n"
							 +"\t--debug\t-d\tDebug-Ausgabe aktivieren\n";
							 
				System.out.println(help);
			}
		}

		global.properties.setConfigFile(configFile);
		
		SynthLookAndFeel laf = new SynthLookAndFeel();
		try {
			laf.load(FPECRunner.class.getResourceAsStream("/styles/rich/laf.xml"), FPECMain.class);
		} catch (ParseException e1) {
			if (global.debug)
				e1.printStackTrace();
		}
		
		try {
			UIManager.setLookAndFeel(laf);
		} catch (UnsupportedLookAndFeelException e) {
			if (global.debug)
				e.printStackTrace();
		}
		
		// Hauptklasse ausfrufen
		FPECMain main = new FPECMain();

	}

}
