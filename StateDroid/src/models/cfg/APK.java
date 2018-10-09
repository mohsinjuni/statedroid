package models.cfg;

import iterator.CommonIterator;
import iterator.PackageIterator;
import java.util.Iterator;
//import iterator.Iterator;

import java.util.*;

import models.manifest.AndroidManifest;

import org.apache.log4j.Logger;

import analyzer.Analyzer;

import taintanalyzer.instranalyzers.AgetTaintAnalyzer;

public class APK extends CFGComponent implements Iterable<CFGComponent> {

	private static Hashtable componentCollection; //Package collection
	private AndroidManifest androidManifest;

	public APK() {
		compCollection = new ArrayList<CFGComponent>();
		componentCollection = new Hashtable();
		androidManifest = new AndroidManifest();
		logger = Logger.getLogger(APK.class);
	}

	public Iterator iterator() {
		return (Iterator) new PackageIterator(this);
	}

	public void accept(Analyzer a) {
		a.analyze(this);
	}

	public ArrayList<CFGComponent> getPkgCollection() {
		return compCollection;
	}

	public void setPkgCollection(ArrayList<CFGComponent> pkgCollection) {
		this.compCollection = pkgCollection;
	}

	public void logInfoAPKInfo() {

		//		APK apk = myAPK;
		int packageCount = 0;
		int totalClassCount = 0;
		int totalActivityCallbackCount = 0;

		for (CFGComponent pkg : compCollection) {
			logger.debug("===========>>" + pkg.getKey());
			//			int currentClassCount = 0;

			Package packge = new Package();
			packge = (Package) pkg;
			packageCount++;
			for (CFGComponent tempCls : packge.getCompCollection()) {
				//				currentClassCount++;
				totalClassCount++;
				ClassObj cls = new ClassObj();
				cls = (ClassObj) tempCls;

				logger.debug(" ==> -- " + cls.getKey());

				for (CFGComponent m : cls.getCompCollection()) {
					CFG mthd = new CFG();
					mthd = (CFG) m;

					for (CFGComponent b : mthd.getCompCollection()) {
						logger.debug("bbName ==> " + b.getKey());

						BasicBlock bb = new BasicBlock();
						bb = (BasicBlock) b;
						for (CFGComponent inst : bb.getCompCollection()) {
							logger.debug(inst.getText());
						}
					}
				}
			}
		}
	}

	public void logParentChildRelationships() {

		int packageCount = 0;
		int totalClassCount = 0;
		int totalActivityCallbackCount = 0;

		for (CFGComponent pkg : compCollection) {
			logger.fatal("===========>>" + pkg.getKey());

			Package packge = new Package();
			packge = (Package) pkg;
			packageCount++;
			for (CFGComponent tempCls : packge.getCompCollection()) {
				totalClassCount++;
				ClassObj cls = new ClassObj();
				cls = (ClassObj) tempCls;

				ClassObj parentObj = cls.getParent();

				if (parentObj != null) {
					logger.fatal("Class Key=" + cls.getKey() + ", parent key=" + parentObj.getCurrPkgClassName());

					if (parentObj.getChildren() != null) {
						for (ClassObj child : parentObj.getChildren()) {
							logger.fatal("\t\t childKey=" + child.getCurrPkgClassName());
						}
					}
				}
			}
		}
	}

	public ClassObj findClassByKey(String qualifiedKey) {
		String clsPkgSplit[] = qualifiedKey.split("[/]");

		//Lcom/Class;
		String pkgName = "";
		if (clsPkgSplit != null) {
			if (clsPkgSplit.length >= 2) {
				for (int i = 0; i < clsPkgSplit.length - 2; i++) {
					pkgName += clsPkgSplit[i].concat("/");
				}
				pkgName += clsPkgSplit[clsPkgSplit.length - 2];
			}
		}
		String sigClssName = clsPkgSplit[clsPkgSplit.length - 1];

		if (sigClssName.endsWith(";"))
			sigClssName = sigClssName.substring(0, sigClssName.length() - 1);

		for (CFGComponent pkgComp : compCollection) {
			Package pkg = (Package) pkgComp;

			//TODO though, you can use this method to get and set parent-child relationships but I guess, at instruction level, 
			// we may need to have access to these relationships. we can set these relationships for each instruction or just set 
			// currCls variable in TA and use that variable to access them. Later option seems more reasonable.
			if (pkgName.equalsIgnoreCase(pkg.getKey())) {
				for (CFGComponent clsComp : pkg.getCompCollection()) {
					ClassObj cls = (ClassObj) clsComp;

					if (sigClssName.equalsIgnoreCase(cls.getKey())) {
						return cls;
					}
				}
			}
		}

		return null;
	}

	public CFG findMethodBySignature(MethodSignature ms) {
		CFG returnCFG = null;
		for (CFGComponent pkgComp : compCollection) {
			Package pkg = (Package) pkgComp;

			String clsPkgSplit[] = ms.getPkgClsName().split("[/]");

			//Lcom/Class;
			String pkgName = "";
			if (clsPkgSplit != null) {
				if (clsPkgSplit.length >= 2) {
					for (int i = 0; i < clsPkgSplit.length - 2; i++) {
						pkgName += clsPkgSplit[i].concat("/");
					}
					pkgName += clsPkgSplit[clsPkgSplit.length - 2];
				}
			}
			String sigClssName = clsPkgSplit[clsPkgSplit.length - 1];
			//			System.out.println(sigClssName);
			if (sigClssName.endsWith(";")) {
				sigClssName = sigClssName.substring(0, sigClssName.length() - 1);
			}
			if (pkgName.equalsIgnoreCase(pkg.getKey())) {
				for (CFGComponent clsComp : pkg.getCompCollection()) {
					ClassObj cls = (ClassObj) clsComp;

					if (sigClssName.equalsIgnoreCase(cls.getKey())) {
						for (CFGComponent cfgComp : cls.getCompCollection()) {
							CFG cfg = (CFG) cfgComp;
							MethodSignature cfgMS = (MethodSignature) cfg.getSignature();
							if (cfgMS.equals(ms)) {
								returnCFG = cfg;
							}
						}
					}
				}
			}
		}

		return returnCFG;
	}

	public CFG findMethodByKey(String qualifiedClassPath, String cfgKey) {
		CFG returnCFG = null;
		for (CFGComponent pkgComp : compCollection) {
			Package pkg = (Package) pkgComp;

			String clsPkgSplit[] = qualifiedClassPath.split("[/]");

			//Lcom/Class;
			String pkgName = "";
			if (clsPkgSplit != null) {
				if (clsPkgSplit.length >= 2) {
					for (int i = 0; i < clsPkgSplit.length - 2; i++) {
						pkgName += clsPkgSplit[i].concat("/");
					}
					pkgName += clsPkgSplit[clsPkgSplit.length - 2];
				}
			}
			String sigClssName = clsPkgSplit[clsPkgSplit.length - 1];
			sigClssName = sigClssName.substring(0, sigClssName.length() - 1);

			if (pkgName.equalsIgnoreCase(pkg.getKey())) {
				for (CFGComponent clsComp : pkg.getCompCollection()) {
					ClassObj cls = (ClassObj) clsComp;

					if (sigClssName.equalsIgnoreCase(cls.getKey())) {
						for (CFGComponent cfgComp : cls.getCompCollection()) {
							CFG cfg = (CFG) cfgComp;
							MethodSignature cfgMS = (MethodSignature) cfg.getSignature();
							if (cfgKey.equals(cfg.getKey())) {
								returnCFG = cfg;
							}
						}
					}
				}
			}
		}

		return returnCFG;
	}

	public void printCFGSignatures(CFGComponent cls) {
		for (CFGComponent cfgComp : cls.getCompCollection()) {
			CFG cfg = (CFG) cfgComp;
			MethodSignature cfgMS = (MethodSignature) cfg.getSignature();

			logger.fatal("cfgName :: " + cfg.getKey() + " , cfgParamsCount:: " + cfgMS.getParams().size());

		}
		System.exit(0);

	}

	public static boolean isComponent(String query) {
		if (query.endsWith(";"))
			query = query.substring(0, query.length() - 1);
		return componentCollection.containsKey(query);
	}

	public String getKey() {
		return this.key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public AndroidManifest getAndroidManifest() {
		return androidManifest;
	}

	public void setAndroidManifest(AndroidManifest androidManifest) {
		this.androidManifest = androidManifest;
	}

	public void addItem(CFGComponent comp) {
		compCollection.add(comp);
	}

	public void setItem(int index, CFGComponent comp) {
		compCollection.set(index, comp);
	}

	public CFGComponent getItem(int index) {
		return compCollection.get(index);
	}

	public boolean removeItem(CFGComponent comp) {
		compCollection.remove(comp);
		return true;
	}

}
