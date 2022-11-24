/*
 * Class MyProperties.java
 * 
 * Verwaltet die Einstellungen des FPE-Calculators
 * 
 * @copyright 2010 Holger Genth
 * @author Holger Genth (http://www.grennis-welt.de)
 * 
 */
package de.grenni.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.stream.FileImageInputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.grenni.utils.MyProperties.STATUS;
/*
 * Diese Klasse bietet Funktionen, um Programmeinstellungen zu verwalten
 * Die 	Einstellungen können in einer XML-Datei, einem Cookie, oder einer
 * Datenbank gespeichert werden. 
 *  
 */
public class MyProperties {

	private static final int XML_LINE_INDENT = 4;
	
	public enum STATUS {OK,
		
						ERR_XML_FAULTY,
						ERR_FILE_NOT_FOUND,
						ERR_FILE_WRITE,
						ERR_TRANSFORM_DOM,
						ERR_JSON_OPERATION,
						ERR_OPERATION_MODE_UNKNOWN,
						ERR_INVALID_INDEX,
						
						INFO_NODE_NOT_FOUND,
						INFO_OBJECT_IS_NO_NODE
	}
	
	public enum MODE {COOKIE, FILE, DATABASE}
	
	@SuppressWarnings("nls")
	private String defaultsFile = "/conf/default.xml";
	private String root;
	private DocumentBuilder xmlDocBuilder;
	private Document xmlDoc;
	private Element propertiesDOM;
	private String configFile;
	private MODE mode;
	private JSONObject parts;
	private STATUS lastErrorID;
	private int lastInsertedArrayIndex;
	
	
	/*
	 * Funktion removeAllChildNodes
	 * Hilfsfunktion - entfernt alle Kind-Knoten eines DOM-Elements
	 * 
	 * @param node NODE       Knoten, dessen Kinder entfernt werden sollen
	 * @param nodeType short  Knotentyp
	 * @param name String     Knotenname (tag)
	 * 
	 */
	public static void removeAllChildNodes(Node node, String name) {
		// Knoten selbst wird gelöscht
		if (name == null || node.getNodeName().equals(name)) {
			node.getParentNode().removeChild(node);
	    } else {
	    	// Kindknoten werden gelöscht
	    	NodeList list = node.getChildNodes();
	    	for (int i = 0; i < list.getLength(); i++) {
	    		removeAllChildNodes(list.item(i), name);
	    	}
	    }
	}
	/*
	 * Konstruktor
	 * 
	 * @param m     Der Modus, siehe enum "MODE"
	 * @param root  Name der Elternelements in der XML-Datei
	 * 
	 */
	public MyProperties(MODE m, String root) {
		try {
			this.root = root;
			this.mode = m;
			
			DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
			xmlDocBuilder = dbfac.newDocumentBuilder();
			
			initFromXMLFile(null);
	
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	/**
	 * 
	 * @return
	 */
	public String getApplicationDirectory() {
		// TODO Auto-generated method stub
		return MyProperties.class.getProtectionDomain().getCodeSource().getLocation().getPath();
	}
	/**
	 * 
	 * @return
	 */
	public String getUsersHomeDirectory() {
		return System.getProperty("user.home", ".");
	}
	/**
	 * 
	 * @param file
	 */
	public void setConfigFile(String file) {
		File f = new File(file);
		if (!f.exists()) {
			System.err.println("Konfigurationsdatei '"+file+"' nicht gefunden!");
		}
		this.configFile = file;
		if (mode == MODE.FILE)
			initFromXMLFile(file);
	}
	/**
	 * 
	 * @return
	 */
	public String getConfigFile() {
		return configFile;
	}
	/*
	 * Funktion getLastErrorID
	 * Gibt die Nummer des letzten Fehlers zurück
	 * 
	 * @return STATUS
	 */
	public STATUS getLastErrorID() {
		return this.lastErrorID;
	}
	/*
	 * Funktion getLastInsertedArrayIndex
	 * Gibt den Index des zuletzt eingefügten Array Elements zurück
	 * 
	 * @return int
	 */
	public int getLastInsertedArrayIndex() {
		return lastInsertedArrayIndex;
	}
	/*
	 * get-Overrides
	 */
	public int getInt(String path) {
		Object obj = get(path,null,false);
		
		if (obj == null) return -1;
		
		String intValue = (String)obj;
		
		return Integer.parseInt(intValue);
	}
	public int getInt(String path,int def) {
		int ret = getInt(path);
		if (ret==-1)
			return def;
		else
			return ret;
	}
	public String getString(String path) {
		String stringValue = (String)get(path,null,false);
		
		return stringValue;
	}
	public String getString(String path,String def) {
		String stringValue = (String)get(path,def,false);
		
		return stringValue;
	}
	public Object get(String path) {
		return get(path,null,false);
	}	
	public Object get(String path, Object def) {
		return get(path,def,false);
	}
	/*
	 * Funktion get
	 * Gibt die durch "path" angeforderte Eigenschaft aus. Wenn nichts gefunden
	 * wurde, dann "def".
	 * 
	 * @param path   String		angeforderte Eigenschaft (z.B. "settings.mainWindow.offsetX")
	 * @param def    Object		wird als Standard zurückgegeben, fall unter "path" nichts gefunden wurde
	 * @param create boolean	Falls nicht gefunden wurde, soll die Eigenschaft neu angelegt werden
	 * 
	 * @return Object
	 */
	public Object get(String path, Object def, boolean create) {
		Object obj = navigatePath(path,create);

		if (obj==null)
			return def;
		else
			return obj;
	}
	/*
	 * Funktion put
	 * Platziert eine Eigenschaft im internen JSONObject.
	 * Der Pfad wird der "path"-Variable entnommen
	 * (z.B. "settings.mainWindow.offsetX")
	 * 
	 *  @param path String	der Pfad zur Eigenschaft
	 *  @param value Object	die Eigenschaft, die gespeichert
	 *  					werden soll
	 *  
	 *  @return STATUS
	 */
	public STATUS put(String path, Object value) {
		
		int indexOfLastFullStop = path.lastIndexOf(".");
		
		String pathToObject = path.substring(0,indexOfLastFullStop);
		
		String key = path.substring(indexOfLastFullStop+1,path.length());
		
		Object obj = navigatePath(pathToObject,true);
		
		if (obj != null) {
			if (obj instanceof JSONObject) {
				try {
					((JSONObject) obj).put(key, value);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return STATUS.ERR_JSON_OPERATION;
				}
			} else if (obj instanceof JSONArray) {
				((JSONArray) obj).put(value);
				this.lastInsertedArrayIndex = ((JSONArray) obj).length()-1; 
			}
		}	
		return STATUS.OK;
	}
	/*
	 * Funktion navigatePath
	 * versucht den unter "path" angegebenen Knoten zu finden
	 * und gibt ihn dann zurück.
	 * Falls der Knoten nicht gefunden werden konnte,
	 * wird bei "create" = true ein neues Element angelegt
	 * 
	 * @param path   String		Pfad zur Eigenschaft
	 * @param create boolean	Sollen fehlende Knoten erzeugt werden
	 * 
	 * @return Object
	 */
	private Object navigatePath(String path, boolean create) {
		String[] components = path.split("\\.");
		String actual;
		JSONArray ja;
		JSONObject jo;
		Object obj = parts;
		int i = 0;

		for (i=0; i < components.length; i++) {

			actual = components[i];

			if (obj instanceof JSONArray) {

				// testen, ob "actual" ein Format entsprechend "[0-9+]" hat
				Pattern pattern = Pattern.compile("\\[([0-9]+)\\]");
				Matcher matcher = pattern.matcher(actual);
				
				if (matcher.find()) {
					try {
						int idx = Integer.parseInt(matcher.group(matcher.groupCount()));
						
						ja = (JSONArray)obj;
						
						obj = ja.get(idx);
						
					} catch (Exception e) {
						// ungültiges Format
						// TODO: Handling einer create-Möglichkeit
						// ( Was soll passieren, wenn der gesuchte index nicht vorhanden ist
						e.printStackTrace();
						this.lastErrorID = STATUS.ERR_INVALID_INDEX;
						break;
					}
				}
				
				ja = (JSONArray)obj;
				
			} else if (obj instanceof JSONObject){
				
				jo = (JSONObject)obj;
				
				if (jo.has(actual)) {
					try {
						obj = jo.get(actual);
					} catch (Exception e) {
						e.printStackTrace();
						break;
					}
				} else {
					if (create) {
						try {
							obj = new JSONObject();
							jo.put(actual, obj);
						} catch (Exception e) {
							e.printStackTrace();
							this.lastErrorID = STATUS.ERR_JSON_OPERATION;
							break;
						}
					} else {
						// Knoten nicht gefunden
						this.lastErrorID = STATUS.INFO_NODE_NOT_FOUND;
						break;
					}
				}
				
			} else {
					// gefundenes Objekt ist kein Knoten
					this.lastErrorID = STATUS.INFO_OBJECT_IS_NO_NODE;
					break;
			}
		}

		if (i==components.length) {
			return obj;
		}else{
			return null;
		}
	}
	/*
	 * Gibt dem Pfad der Standard-Konfigurationsdatei zurück
	 * 
	 * @return String
	 */
	public String getDefaultsFile() {
		return defaultsFile;
	}
	/*
	 * Setzt den Pfad der Standard-Konfigurationsdatei und
	 * initialisiert im Anschluss die Standardkonfiguration
	 * 
	 * @param String Pfad zur Konfigurationsdatei
	 * 
	 */
	public void setDefaultsFile(String defaultsFile) {
		this.defaultsFile = defaultsFile;
		if (mode == MODE.FILE)
			this.initFromXMLFile(defaultsFile);
	}
	/*
	 * Speichert die einzelnen Konfigurationsabschnitte
	 * 
	 * @return STATUS
	 */
	public STATUS saveParts() {
		try {		
			Iterator keys = parts.keys();

			xmlDoc = xmlDocBuilder.newDocument();
			propertiesDOM = null;
			propertiesDOM = xmlDoc.createElement(root);
			
			while(keys.hasNext()){
				String k = (String)keys.next();
				Object v = parts.get(k);
				putJSONDataIntoDOM(v,k);
			}
			
			xmlDoc.appendChild(propertiesDOM);
			// Wie soll gespeichert werden?
			switch(this.mode) {
				case FILE:
					return saveDOMtoXMLFile(xmlDoc,configFile);
				case COOKIE:
					break;
				case DATABASE:
					break;
			}
			return STATUS.ERR_OPERATION_MODE_UNKNOWN;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return STATUS.ERR_JSON_OPERATION;
		}
	}
	/*
	 * Konvertiert einen DOM-Element Knoten in ein JSONObject
	 * 
	 * @param node Node  Der Knoten, der umgewandelt werden soll
	 * @return Object
	 */
	private Object convertDOMtoObject(Node node){
		JSONObject jo = new JSONObject();
		
		Node child;
		NamedNodeMap attributes;
		Node attribute;
		String type;
		String nodeName;
		
		Object obj;
		
		if (node.hasChildNodes() && node.getNodeType() == Node.ELEMENT_NODE) {
			NodeList children = node.getChildNodes();
			int len = children.getLength();
		
			for (int i = 0; i < len; i++) {
				
				child = children.item(i);
				if (child == null) continue;
				
				nodeName = child.getNodeName();

				if (nodeName == null) continue;

				if (nodeName.equals("array")) {
					obj = convertDOMtoJSONArray(child);
				}
				
				else if (child.getNodeType() == Node.TEXT_NODE) {

					nodeName = node.getNodeName();
					obj = convertDOMtoObject(child);
					if (obj == null) continue;

					return obj;
					
				} else
					obj = convertDOMtoObject(child);

				try {
					if (obj != null)
						jo.put(nodeName, obj);

				} catch (JSONException e1) {
					e1.printStackTrace();
				}
				/*
				if (child.hasAttributes()) {
					attributes = child.getAttributes();
					attribute = attributes.getNamedItem("type");
					if (attribute != null) {
						type = attribute.getNodeValue();
						try {
							jo.putOpt(nodeName+"_type", type);
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}
				*/
				
			}

			return jo;

		}
		else
		{
			String ret = node.getNodeValue();

			// nur dekoratives gefunden
			if (ret == null || ret.matches("\\s+"))
				ret = null;

			return ret;
			
		}
		
	}
	/*
	 * Konvertiert einen DOM-Element Knoten in ein JSONArray
	 * 
	 * @param node Node  Der Knoten, der umgewandelt werden soll
	 * @return JSONArray
	 */
	@SuppressWarnings("nls")
	private JSONArray convertDOMtoJSONArray(Node node){
		JSONArray ja = new JSONArray();
		
		if (!node.getNodeName().equals("array"))
			return null;
		
		NodeList children = node.getChildNodes();
		Node child;
		Object obj;
		
		int len = children.getLength();
		
		for (int i=0; i<len; i++) {
			child = children.item(i);
			
			if (child.getNodeName().equals("item")) {
				obj = convertDOMtoObject(child);
				ja.put(obj);
			}
		}
		return ja;
	}
	/*
	 * Liest eine Konfigurationsdatei ein
	 * und initialisiert das jsonObject Feld mit den Daten
	 * 
	 * @param  path   Pfad zur Datei
	 *                (oder null für die Standard-Konfiguration)
	 * @return STATUS
	 */
	private STATUS initFromXMLFile(String path) {
		if (path == null)
			path = defaultsFile;
		
		try {
			InputStream is = this.getClass().getResourceAsStream(path);
			if (is==null)  // eine Datei??
				is = (InputStream)new FileInputStream(path);
			
			xmlDoc = xmlDocBuilder.parse(is);
			is.close();
			
		} catch (SAXException e) {
			
			System.err.println(path + " fehlerhaft!");
			e.printStackTrace();
			
			return STATUS.ERR_XML_FAULTY;
			
		} catch (IOException e) {
			
			System.err.println(path + " nicht gefunden!");
			e.printStackTrace();
			
			return STATUS.ERR_FILE_NOT_FOUND;
		}
		propertiesDOM = (Element)xmlDoc.getElementsByTagName(this.root).item(0);
		
		if (propertiesDOM != null) {
			
			if (parts == null)
				parts = new JSONObject();
			
			Object obj =convertDOMtoObject(propertiesDOM);
			if (obj instanceof JSONObject)
				parts = (JSONObject)obj;
			
			return STATUS.OK;
		} 
		return STATUS.INFO_NODE_NOT_FOUND;
	}
	/*
	 * parst ein Object in ein DOM-Element
	 * (wird von putJSONDataIntoDOM genutzt)
	 * 
	 * @param key String   Schlüsselname
	 * @param value Object Das Objekt, das geparst werden soll
	 * 
	 * @return Element
	 */
	private Element parseJSONObject(String key, Object value) throws JSONException{
		if (value instanceof JSONObject) {
			JSONObject jo = (JSONObject)value;
			Iterator keys = jo.keys();
			Element ret = xmlDoc.createElement(key); 
			Element elem;
		
			while(keys.hasNext()){
				String k = (String)keys.next();
				Object v = jo.get(k);
	
				if (v instanceof JSONArray) {
					elem = parseJSONObject("array", v);
				} else if (v instanceof JSONObject) {
					elem = parseJSONObject(k, v);
				} else {
					elem = xmlDoc.createElement(k);
					elem.setAttribute("type", v.getClass().getName());
					elem.appendChild(xmlDoc.createTextNode(v.toString()));
				}
				ret.appendChild(elem);
			}
			return ret;
		}
		else if (value instanceof JSONArray) {
			JSONArray ja = (JSONArray)value;
			int len = ja.length();
			Object v;
			Element ret = xmlDoc.createElement("array");
			Element elem;
			
			for (int i = 0; i<len; i++) {
				v = ja.get(i);
				elem = xmlDoc.createElement("item");
				elem.setAttribute("type", v.getClass().getName());
				elem.appendChild(xmlDoc.createTextNode(v.toString()));
				ret.appendChild(elem);
			}
			
			return ret;
		} else  {
			Element ret = xmlDoc.createElement(key);
			ret.setAttribute("type",value.getClass().getName());
			ret.appendChild(xmlDoc.createTextNode(value.toString()));
			
			return ret;
		}
	}
	/*
	 * Durchwandert ein jsonObject und erzeugt eine DOM-Entsprechung
	 * 
	 * @param obj  JSONObject    Das Objekt, das gelesen werden soll
	 * @param node String        Name des Knotens, unter dem das Objekt
	 *                           eingefügt werden soll		
	 */
	private void putJSONDataIntoDOM(Object object, String node) {
		try {
			if (object instanceof JSONObject) {
				JSONObject obj = (JSONObject)object;

				Iterator keys = obj.keys();
				Element elem = xmlDoc.createElement(node);
			
				while(keys.hasNext()){
					String key = (String)keys.next();
					Object value = obj.get(key);
				
					elem.appendChild(parseJSONObject(key,value));
				}
				propertiesDOM.appendChild(elem);
			} else {
				propertiesDOM.appendChild(parseJSONObject(node,object));
			}
		} catch (Exception e) {
			e.printStackTrace();			
		}	
	}
	/*
	 * Speichert ein DOM-Document als XML Struktur
	 * 
	 * @param xmlDoc Document   DOM-Document
	 * @param file String       Zieldatei
	 * 
	 * @return STATUS
	 */
	private STATUS saveDOMtoXMLFile(Document xmlDoc, String file){
		try {
			// set up a transformer
        	TransformerFactory transfac = TransformerFactory.newInstance();
        	transfac.setAttribute("indent-number", XML_LINE_INDENT);
        	
        	Transformer trans;
			trans = transfac.newTransformer();
			
			trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
			trans.setOutputProperty(OutputKeys.STANDALONE, "yes");
			trans.setOutputProperty(OutputKeys.INDENT, "yes");
			trans.setOutputProperty(OutputKeys.METHOD, "xml");

			//create string from xml tree
			File f = new File(file);
			for (int i=4; i>=0; i--) {
				File f2 = new File(file+"."+i);
				if (f2.exists()) {
					f2.renameTo(new File(file+"."+(i+1)));
				}
			}
			f.renameTo(new File(file+".0"));

			FileWriter fw = new FileWriter(file);
			StreamResult result = new StreamResult(fw);
					
			DOMSource source = new DOMSource(xmlDoc);

			trans.transform(source, result);
			
			fw.close();
		
		} catch (TransformerConfigurationException e) {
			//System.err.println("");
			e.printStackTrace();
			return STATUS.ERR_TRANSFORM_DOM;
		} catch (TransformerException e) {
			System.err.println("Konnte Dokument nicht umwandeln");
			e.printStackTrace();
			return STATUS.ERR_TRANSFORM_DOM;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			return STATUS.ERR_FILE_WRITE;
		}
		return STATUS.OK;
	}


}
