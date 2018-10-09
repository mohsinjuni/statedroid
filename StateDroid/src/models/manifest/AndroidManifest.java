package models.manifest;

import java.util.ArrayList;

public class AndroidManifest {

	private ArrayList<Permission> usedPermList;
	private ArrayList<String> usedPermStrList;
	private Application application;
	private String packageName = "";

	public AndroidManifest() {
		usedPermList = new ArrayList<Permission>();
		usedPermStrList = new ArrayList<String>();
		application = new Application();
	}

	public void setUsedPermAsStrings() {
		if (usedPermList.size() > 0) {
			for (Permission p : usedPermList) {
				String pName = p.getName();
				usedPermStrList.add(pName);
			}
		}
	}

	public void printAllComponents() {
		ComponentManifest resultComponent = null;
		ArrayList<ComponentManifest> componentsManifestList = application.getComponentsManifest();

		if (componentsManifestList != null) {
			for (ComponentManifest comp : componentsManifestList) {
				System.out.println(comp.getName() + " <==> ");
			}
		}
	}

	//Ideally, this should be in Application.java class.
	public ComponentManifest findComponentManifest(String qualifiedCompName) {
		ComponentManifest resultComponent = null;
		ArrayList<ComponentManifest> componentsManifest = application.getComponentsManifest();

		for (ComponentManifest comp : componentsManifest) {
			if (comp.getName().endsWith(";") && !qualifiedCompName.endsWith(";")) {
				qualifiedCompName = qualifiedCompName + ";";
			} else if (!comp.getName().endsWith(";") && qualifiedCompName.endsWith(";")) {
				qualifiedCompName = qualifiedCompName.substring(0, qualifiedCompName.length() - 1);
			}
			if (comp.getName().equalsIgnoreCase(qualifiedCompName)) {
				resultComponent = comp;
				break;
			}
		}

		return resultComponent;

	}

	public boolean isLauncherMainComponent(String qualifiedCompName) {
		//Since most of the component names are given with dash(/), instead of dot(.), therefore, we modify qualifiedCompName, if needed.

		qualifiedCompName = qualifiedCompName.replace("'", "");
		qualifiedCompName = qualifiedCompName.replace(".", "/");
		if (!qualifiedCompName.startsWith("L")) {
			qualifiedCompName = "L" + qualifiedCompName;
		}

		ComponentManifest resultComponent = null;
		boolean isMain = false;
		ArrayList<ComponentManifest> componentsManifest = application.getComponentsManifest();

		for (ComponentManifest comp : componentsManifest) {
			if (comp.getName().endsWith(";") && !qualifiedCompName.endsWith(";")) {
				qualifiedCompName = qualifiedCompName + ";";
			} else if (!comp.getName().endsWith(";") && qualifiedCompName.endsWith(";")) {
				qualifiedCompName = qualifiedCompName.substring(0, qualifiedCompName.length() - 1);
			}
			if (comp.getName().equalsIgnoreCase(qualifiedCompName)) {
				resultComponent = comp;
				break;
			}
		}
		//      <action android:name="android.intent.action.MAIN" />
		//        <category android:name="android.intent.category.LAUNCHER" />		

		if (resultComponent != null) {
			ArrayList<IntentFilter> iFilters = resultComponent.getIntentFilters();
			for (IntentFilter inf : iFilters) {
				ArrayList<String> actions = inf.getActionList();
				ArrayList<String> categories = inf.getCategoriesList();
				if (actions.contains("android.intent.action.MAIN") && categories.contains("android.intent.category.LAUNCHER")) {
					isMain = true;
					break;
				}
			}
		}
		return isMain;

	}

	public ArrayList<Permission> getUsedPermList() {
		return usedPermList;
	}

	public void setUsedPermList(ArrayList<Permission> usedPermList) {
		this.usedPermList = usedPermList;
	}

	public Application getApplication() {
		return application;
	}

	public void setApplication(Application application) {
		this.application = application;
	}

	public ArrayList<String> getUsedPermStrList() {
		return usedPermStrList;
	}

	public void setUsedPermStrList(ArrayList<String> usedPermStrList) {
		this.usedPermStrList = usedPermStrList;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

}
