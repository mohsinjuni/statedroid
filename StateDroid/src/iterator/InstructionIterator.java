package iterator;

import java.util.ArrayList;

import java.util.Iterator;

import models.cfg.BasicBlock;
import models.cfg.CFGComponent;

//public class InstructionIterator implements Iterator{
public class InstructionIterator implements Iterator<Object> {

	private CFGComponent currComp;
	private int currPosition;
	private ArrayList<CFGComponent> currInstrCollection;

	public InstructionIterator(CFGComponent myComp) {
		currComp = myComp;
		currInstrCollection = myComp.getCompCollection();
		currPosition = 0;
	}

	public boolean hasNext() {
		if (currPosition < currInstrCollection.size())
			return true;
		return false;
	}

	public Object next() {
		Object obj = currInstrCollection.get(currPosition);
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
