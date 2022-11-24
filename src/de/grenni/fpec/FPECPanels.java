package de.grenni.fpec;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.TreeMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import  de.grenni.utils.MyProperties;

import de.grenni.utils.MyProperties;

public class FPECPanels {
	String[] panels2load;// = {"FPECPanelIntro" };
	final static String CONFIG_PATH = "panels";
	
	LinkedHashMap<String, FPECPanel> panels;
	
	/*
	 * Konstruktor
	 * Lädt alle verfügbaren Panels und speichert sie für einen späteren Abruf
	 * 
	 * @param properties MyProperties	Klasse aus der die Einstellungen bezogen werden
	 * @param padding	 Insets			Padding für den Inhalt
	 * @param font		 Font		    In dem Panel zu verwendender Font
	 */
	public FPECPanels(MyProperties properties, Insets padding) {
		JSONArray ja;
		JSONObject jo;
		Object o = properties.get(CONFIG_PATH);

		try {
			if (o instanceof JSONObject) {
				jo = (JSONObject)o;
				ja = jo.getJSONArray("array");
			
				int len = ja.length();
				panels2load = new String[len];
				for (int idx=0; idx < len; idx++) {
					try {
						panels2load[idx] = ja.getString(idx);
					} catch (JSONException e) {
						System.err.println("Überspringe '"+panels2load[idx]+"'!");
						if (global.debug) e.printStackTrace();
					}
				}
			}
		} catch (JSONException e) {
			if (global.debug) e.printStackTrace();
		}
		
		panels = new LinkedHashMap();
		Class<FPECPanel> c;
		FPECPanel panel;
		String actual;
		// Panels laden und speichern
		for (int i = 0; i < panels2load.length; i++) {
			actual = panels2load[i];

			try {
				// Klasse laden
				c = (Class<FPECPanel>)ClassLoader.getSystemClassLoader().loadClass("de.grenni.fpec."+actual);
				// Konstruktor vorbereiten
				Constructor<FPECPanel> constructor = c.getConstructor(new Class[]{Insets.class});
				// Klasse instanzieren und Konstruktor aufrufen
				panel = constructor.newInstance(new Object[]{padding});
				panel.setName("FPECPanel");
				// Klasse speichern
				panels.put(actual, panel);
				
			} catch (ClassNotFoundException e) {
				System.err.println("Das Panel '"+actual+"' wurde nicht gefunden!");
				if (global.debug) e.printStackTrace();
			} catch (SecurityException e) {
				System.err.println("Keine Erlaubnis '"+actual+"' zu laden!");
				if (global.debug) e.printStackTrace();
			} catch (NoSuchMethodException e) {
				System.err.println("Das Panel '"+actual+"' hat den falschen Typ");
				if (global.debug) e.printStackTrace();
			} catch (IllegalArgumentException e) {
				if (global.debug) e.printStackTrace();
			} catch (InstantiationException e) {
				if (global.debug) e.printStackTrace();
			} catch (IllegalAccessException e) {
				if (global.debug) e.printStackTrace();
			} catch (InvocationTargetException e) {
				if (global.debug) e.printStackTrace();
			}
		}
	}
	/*
	 * Funktion get
	 * Gibt ein Panel zurück
	 * 
	 * @param name String	Der Name des Panels
	 * 
	 * @return FPECPanel
	 */
	public FPECPanel get(String name) {
		return panels.get(name);
	}
	/*
	 * Funktion getAll
	 * Gibt alle Panels als Array zurück
	 * 
	 * @return FPECPanel[]
	 */
	public FPECPanel[] getAll() {
		Iterator<FPECPanel> v = panels.values().iterator();
		FPECPanel[] ret = new FPECPanel[panels.size()];
		
		int i = 0;
		while (v.hasNext())
			ret[i++] = v.next();
		
		return ret;
	}
}
