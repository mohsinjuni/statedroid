package iterator;

import java.util.ArrayList;

import java.util.Iterator;

import models.cfg.BasicBlock;
import models.cfg.CFGComponent;

public class ClassObjIterator implements Iterator<Object> {

	private CFGComponent currComp;
	private int currPosition;
	private ArrayList<CFGComponent> currClassObjCollection;

	public ClassObjIterator(CFGComponent myComp) {
		currComp = myComp;
		currClassObjCollection = myComp.getCompCollection();
		currPosition = 0;
	}

	public boolean hasNext() {
		if (currPosition < currClassObjCollection.size())
			return true;
		return false;
	}

	public Object next() {
		Object obj = currClassObjCollection.get(currPosition);
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
