package models.cfg;

import iterator.CFGIterator;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

import analyzer.Analyzer;

import patternMatcher.attackreporter.Report;
import configuration.Config;

public class ClassObj extends CFGComponent implements Iterable<CFGComponent> {

	private String type = "";
	private ArrayList<Report> reportList;

	private ClassObj parent;
	private String parentName = "";
	private ArrayList<ClassObj> children;
	private ArrayList<String> layoutMethods;

	public ClassObj() {
		compCollection = new ArrayList<CFGComponent>();
		setReportList(new ArrayList<Report>());
		setLayoutMethods(new ArrayList<String>());
	}

	public ClassObj(String className) {
		this.key = className;
		compCollection = new ArrayList<CFGComponent>();
	}

	public void addItem(CFGComponent comp) {
		compCollection.add(comp);
	}

	public boolean removeItem(CFGComponent comp) {
		compCollection.remove(comp);
		return true;
	}

	@Override
	public Iterator iterator() {
		return (Iterator) new CFGIterator(this);
		//TODO have to do renaming of Iterators.
	}

	public CFGComponent findCFGByKey(String key) {

		for (CFGComponent comp : compCollection) {
			if (comp.getKey().equalsIgnoreCase(key)) {
				return comp;
			}
		}
		return null;
	}

	public CFGComponent findCFGByKeyAndParams(String key, String[] params) {

		for (CFGComponent comp : compCollection) {
			if (comp.getKey().equalsIgnoreCase(key)) {
				CFG cfg = (CFG) comp;
				if (cfg.getSignature().getParams() == null || cfg.getSignature().getParams().size() == 0)
					return comp;
				else {
					if (cfg.getSignature().getParams().size() == params.length) {
						int i = 0;
						boolean paramMatch = true;
						for (Parameter param : cfg.getSignature().getParams()) {
							String type = param.getType();
							if (!type.trim().equalsIgnoreCase(params[i])) {
								paramMatch = false;
							}
							i++;
						}
						if (paramMatch)
							return comp;
					}
				}
				//				return comp;
			}
		}
		return null;
	}

	public void accept(Analyzer a) {

		a.analyze(this);

	}

	public boolean isClassBlacklisted() {
		String clsKey = this.getCurrPkgClassName();

		Hashtable ht = Config.getInstance().getBlackListedAPIs();

		Enumeration<String> APIs = ht.keys();
		while (APIs.hasMoreElements()) {
			String apiName = APIs.nextElement();

			if (clsKey.startsWith(apiName))
				return true;
		}

		return false;

	}

	public void setItem(int index, CFGComponent comp) {
		compCollection.set(index, comp);
	}

	public void setAnalayzeType() {

	}

	public String getKey() {
		return this.key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public ArrayList<Report> getReportList() {
		return reportList;
	}

	public void setReportList(ArrayList<Report> reportList) {
		this.reportList = reportList;
	}

	public ClassObj getParent() {
		return parent;
	}

	public void setParent(ClassObj parent) {
		this.parent = parent;
	}

	public ArrayList<ClassObj> getChildren() {
		return children;
	}

	public void setChildren(ArrayList<ClassObj> children) {
		this.children = children;
	}

	public String getParentName() {
		return parentName;
	}

	public void setParentName(String parentName) {
		this.parentName = parentName;
	}

	public ArrayList<String> getLayoutMethods() {
		return layoutMethods;
	}

	public void setLayoutMethods(ArrayList<String> layoutMethods) {
		this.layoutMethods = layoutMethods;
	}

}
