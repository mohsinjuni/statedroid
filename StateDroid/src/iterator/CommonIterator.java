package iterator;

import java.util.ArrayList;
import java.util.Iterator;

import models.cfg.CFGComponent;

//public class CommonIterator implements Iterator{
public class CommonIterator implements Iterator<Object> {

	private CFGComponent currComp;
	private int currPosition;
	private ArrayList<CFGComponent> currCompCollection;

	public CommonIterator(CFGComponent myComp) {
		currComp = myComp;
		currCompCollection = myComp.getCompCollection();
		currPosition = 0;
	}

	public boolean hasNext() {
		if (currPosition < currCompCollection.size())
			return true;
		return false;
	}

	public Object next() {
		Object obj = currCompCollection.get(currPosition);
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
		// TODO Auto-generated method stub

	}

}
