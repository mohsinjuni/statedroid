package models.manifest;

import java.util.ArrayList;

import models.cfg.CFGComponent;

public class Application {

	private ArrayList<ComponentManifest> componentsManifest;
	private ArrayList<String> componentList;

	public Application() {
		setComponentsManifest(new ArrayList<ComponentManifest>());
	}

	public ArrayList<ComponentManifest> getComponentsManifest() {
		return componentsManifest;
	}

	public void setComponentsManifest(ArrayList<ComponentManifest> componentsManifest) {
		this.componentsManifest = componentsManifest;
	}

	public ArrayList<String> getComponentList() {
		return componentList;
	}

	public void setComponentList(ArrayList<String> componentList) {
		this.componentList = componentList;
	}

	public void setComponentList() {
		if (componentsManifest != null && componentsManifest.size() > 0) {
			componentList = new ArrayList<String>();
			for (ComponentManifest comp : componentsManifest) {
				componentList.add(comp.getName());
			}
		}
	}

	public ComponentManifest getComponent(String compName) {
		ComponentManifest returnComp = null;
		for (ComponentManifest comp : componentsManifest) {
			if (comp.getName().equalsIgnoreCase(compName)) {
				returnComp = comp;
				break;
			}
		}
		return returnComp;
	}

}
