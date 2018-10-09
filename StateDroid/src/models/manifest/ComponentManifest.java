package models.manifest;

import java.util.ArrayList;

public class ComponentManifest {

	private String name = "";
	private String type = "";
	private boolean enabled = true;
	private ArrayList<IntentFilter> intentFilters;

	public ComponentManifest() {
		intentFilters = new ArrayList<IntentFilter>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<IntentFilter> getIntentFilters() {
		return intentFilters;
	}

	public void setIntentFilters(ArrayList<IntentFilter> intentFilters) {
		this.intentFilters = intentFilters;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

}
