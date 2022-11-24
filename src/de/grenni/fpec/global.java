package de.grenni.fpec;

import de.grenni.utils.MyProperties;

public class global {
	public static FPECCommunicator communicator = new FPECCommunicator();
	public static MyProperties properties;

	public static FPECConstants userDataLocation; 
	
	public static final String DATADIR_REDIRECT_FILE_NAME = ".datadir";
	public static final String VERSION = "0.2";
	public static final String CONFIG_FILE_NAME = "config.xml";
	
	public static boolean debug = false;
	
}
