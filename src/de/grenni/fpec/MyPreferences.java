package de.grenni.fpec;

import java.util.prefs.Preferences;

@SuppressWarnings("nls")
class MyPreferences {
	private Preferences prefs;
	
	private String bgGradientStart; 
	private String bgGradientEnd;
	private String language;
	private int mainWindowShadowOffsetX;
	private int mainWindowShadowOffsetY;
	private int mainWindowCornerRadius;
	private int mainWindowShadowBlur;
	// -- Getters und Setters
	public String getBgGradientStart() {
		return bgGradientStart;
	}

	public void setBgGradientStart(String bgGradientStart) {
		this.bgGradientStart = bgGradientStart;
		prefs.put("bgGradientStart",bgGradientStart);
	}
	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
		prefs.put("language",language);
	}
	public String getBgGradientEnd() {
		return bgGradientEnd;
	}

	public void setBgGradientEnd(String bgGradientEnd) {
		this.bgGradientEnd = bgGradientEnd;
		prefs.put("bgGradientEnd",bgGradientEnd);
	}

	public int getMainWindowShadowOffsetX() {
		return mainWindowShadowOffsetX;
	}

	public void setMainWindowShadowOffsetX(int mainWindowShadowOffsetX) {
		this.mainWindowShadowOffsetX = mainWindowShadowOffsetX;
		prefs.putInt("mainWindowShadowOffsetX",mainWindowShadowOffsetX);
	}

	public int getMainWindowShadowOffsetY() {
		return mainWindowShadowOffsetY;
	}

	public void setMainWindowShadowOffsetY(int mainWindowShadowOffsetY) {
		this.mainWindowShadowOffsetY = mainWindowShadowOffsetY;
		prefs.putInt("mainWindowShadowOffsetY",mainWindowShadowOffsetY);
	}

	public int getMainWindowCornerRadius() {
		return mainWindowCornerRadius;
	}

	public void setMainWindowCornerRadius(int mainWindowCornerRadius) {
		this.mainWindowCornerRadius = mainWindowCornerRadius;
		prefs.putInt("mainWindowCornerRadius",mainWindowCornerRadius);
	}
	public int getMainWindowShadowBlur() {
		return mainWindowShadowBlur;
	}

	public void setMainWindowShadowBlur(int mainWindowShadowBlur) {
		this.mainWindowShadowBlur = mainWindowShadowBlur;
		prefs.putInt("mainWindowShadowBlur",mainWindowShadowBlur);
	}

	// -- Konstruktor --
	MyPreferences(String owner) {
		prefs = Preferences.userRoot().node(owner);
		bgGradientStart = prefs.get("bgGradientStart","#D4D4D4"); 
		bgGradientEnd = prefs.get("bgGradientEnd","#B3B3B3");
		language = prefs.get("language","de");
		mainWindowShadowOffsetX = prefs.getInt("mainWindowShadowOffsetX",6);
		mainWindowShadowOffsetY = prefs.getInt("mainWindowShadowOffsetY",7);
		mainWindowCornerRadius  = prefs.getInt("mainWindowCornerRadius",25);
		mainWindowShadowBlur    = prefs.getInt("mainWindowShadowBlur",5);
	}
	
}
