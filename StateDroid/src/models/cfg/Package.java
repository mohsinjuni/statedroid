package models.cfg;

import iterator.ClassObjIterator;
import iterator.CommonIterator;
import java.util.Iterator;
//import iterator.Iterator;

import java.util.*;

import analyzer.Analyzer;

public class Package extends CFGComponent implements Iterable<CFGComponent> {

	public Package() {
		compCollection = new ArrayList<CFGComponent>();
	}

	public static enum PACKAGE_TYPES {
		MainApp, Ad
	}

	public void setItem(int index, CFGComponent comp) {
		compCollection.set(index, comp);
	}

	public void addItem(CFGComponent comp) {
		compCollection.add(comp);
	}

	public boolean removeItem(CFGComponent comp) {
		compCollection.remove(comp);
		return true;
	}

	//	@Override
	public Iterator iterator() {
		//		Iterator iterator = instrList.iterator();
		return (Iterator) new ClassObjIterator(this);
	}

	public void accept(Analyzer a) {

		a.analyze(this);

	}

	public String getKey() {
		return this.key;
	}

	public void setKey(String key) {
		this.key = key;
	}

}
