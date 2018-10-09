package models.cfg;

import iterator.InstructionIterator;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

import analyzer.Analyzer;

import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;

import configuration.Config;

public class BasicBlock extends CFGComponent implements Iterable<CFGComponent> {

	private ArrayList<String> predecessors; //store predecessor and successor keys. So these keys have to be unique.
	private ArrayList<String> successors;
	private String bbPosition = "";
	private boolean visited = false;
	private ArrayList<String> dominators;

	private Hashtable OUT;
	private Hashtable shadowCopyOfOut;
	private Hashtable shadowCopyOfGlobalSymTable;

	public BasicBlock() {
		compCollection = new ArrayList<CFGComponent>();
		predecessors = new ArrayList<String>();
		successors = new ArrayList<String>();
		setDominators(new ArrayList<String>());
	}

	public void addItem(CFGComponent comp) {
		compCollection.add(comp);
	}

	public boolean removeItem(CFGComponent comp) {
		compCollection.remove(comp);
		return true;
	}

	public void setItem(int index, CFGComponent comp) {
		compCollection.set(index, comp);
	}

	public CFGComponent getItem(int index) {
		return compCollection.get(index);
	}

	//	@Override
	public Iterator iterator() {
		//		Iterator iterator = instrList.iterator();
		return (Iterator) new InstructionIterator(this);
	}

	public void accept(Analyzer a) {
		a.analyze(this);
	}

	public void setAnalayzeType() {

	}

	public String getKey() {
		return this.key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Hashtable getOUT() {
		return OUT;
	}

	public void setOUT(Hashtable oUT) {
		OUT = oUT;
	}

	public ArrayList<String> getPredecessors() {
		return predecessors;
	}

	public void setPredecessors(ArrayList<String> predecessors) {
		this.predecessors = predecessors;
	}

	public ArrayList<String> getSuccessors() {
		return successors;
	}

	public void setSuccessors(ArrayList<String> successors) {
		this.successors = successors;
	}

	public String getBbPosition() {
		return bbPosition;
	}

	public void setBbPosition(String bbPosition) {
		this.bbPosition = bbPosition;
	}

	public boolean isVisited() {
		return visited;
	}

	public void setVisited(boolean visited) {
		this.visited = visited;
	}

	public ArrayList<String> getDominators() {
		return dominators;
	}

	public void setDominators(ArrayList<String> dominators) {
		this.dominators = dominators;
	}

	public Hashtable getShadowCopyOfOut() {
		return shadowCopyOfOut;
	}

	public void setShadowCopyOfOut(Hashtable originalOut) {
		//Lets make deep copy up to two levels

		if (originalOut != null) {
			Hashtable shadowCopy = new Hashtable();
			Enumeration<String> firstLevelKeys = originalOut.keys();

			while (firstLevelKeys.hasMoreElements()) {
				String fKey = firstLevelKeys.nextElement();
				SymbolTableEntry ent = (SymbolTableEntry) originalOut.get(fKey);

				if (!ent.getEntryDetails().isRecord()) {
					//this is easy.
					SymbolTableEntry newEnt = new SymbolTableEntry(ent); //it makes first level deep copy
					shadowCopy.put(newEnt.getName(), newEnt);
				} else {
					Hashtable myRecordFieldList = ent.getEntryDetails().getRecordFieldList();

					if (myRecordFieldList != null) {
						Enumeration<String> secondLevelKeys = myRecordFieldList.keys();
						SymbolTableEntry secondLevelEnt = new SymbolTableEntry();
						secondLevelEnt.setName(ent.getName());

						Hashtable recordFieldList = new Hashtable();

						while (secondLevelKeys.hasMoreElements()) {
							String sKey = secondLevelKeys.nextElement();
							SymbolTableEntry sEnt = (SymbolTableEntry) ent.getEntryDetails().getRecordFieldList().get(sKey);

							SymbolTableEntry newSEnt = new SymbolTableEntry(sEnt); //it makes first level deep copy
							recordFieldList.put(newSEnt.getName(), newSEnt);
						}
						secondLevelEnt.getEntryDetails().setRecord(true);
						secondLevelEnt.getEntryDetails().setRecordFieldList(recordFieldList);
						shadowCopy.put(secondLevelEnt.getName(), secondLevelEnt);
					} else {
						//rare case
						SymbolTableEntry newEnt = new SymbolTableEntry(ent); //it makes first level deep copy
						shadowCopy.put(newEnt.getName(), newEnt);
					}
				}
			}
			this.shadowCopyOfOut = shadowCopy;
		} else
			this.shadowCopyOfOut = originalOut;

	}

	public Hashtable getShadowCopyOfGlobalSymTable() {
		return shadowCopyOfGlobalSymTable;
	}

	public void setShadowCopyOfGlobalSymTable() {
		//Just make deep copy of objects

		SymbolSpace globalSymCopy = Config.getInstance().getGlobalSymbolSpace();
		Hashtable originalSymTable = (Hashtable) globalSymCopy.getEntries().get(0);

		Hashtable deepCopy = new Hashtable();
		Enumeration<String> firstLevelKeys = originalSymTable.keys();

		while (firstLevelKeys.hasMoreElements()) {
			String fKey = firstLevelKeys.nextElement();
			SymbolTableEntry ent = (SymbolTableEntry) originalSymTable.get(fKey);

			SymbolTableEntry newEnt = new SymbolTableEntry(ent); //it makes first level deep copy
			deepCopy.put(newEnt.getName(), newEnt);

		}
		this.shadowCopyOfGlobalSymTable = deepCopy;

	}

	public void setNulltoShadowCopyOfGlobalSymTable() {
		//Just make deep copy of objects
		this.shadowCopyOfGlobalSymTable = null;

	}

}
