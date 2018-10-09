package iterator;

import java.util.ArrayList;

import java.util.Iterator;

import models.cfg.BasicBlock;
import models.cfg.CFGComponent;

//public class PackageIterator implements Iterator{
public class PackageIterator implements Iterator<Object> {

	private CFGComponent currComp;
	private int currPosition;
	private ArrayList<CFGComponent> currPackageCollection;

	public PackageIterator(CFGComponent myComp) {
		currComp = myComp;
		currPackageCollection = myComp.getCompCollection();
		currPosition = 0;
	}

	public boolean hasNext() {

		if (currPosition < currPackageCollection.size())
			return true;
		return false;
	}

	public Object next() {
		Object obj = currComp.getItem(currPosition);
		currPosition += 1;
		return obj;
	}

	public boolean remove(Object obj) {
		if (currComp.removeItem((CFGComponent) obj)) {
			currPosition -= 1;
			return true;
		}
		return false;
	}

	@Override
	public String toString() {
		return currComp.getItem(currPosition).toString();
	}

	@Override
	public void remove() {
	}

}
