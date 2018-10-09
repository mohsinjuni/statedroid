package models.symboltable;

import java.util.Hashtable;
import java.util.Map.Entry;

public class SymbolTableEntry implements Cloneable {

	private String name = "";
	private String lineNumber = "";
	private String instrInfo = "";

	EntryDetails details;

	private SymbolTableEntry(EntryDetails details) {
		this.details = details;
	}

	public SymbolTableEntry() {
		this(new EntryDetails());
	}

	public SymbolTableEntry(SymbolTableEntry arg) { //deep copy

		name = arg.getName();
		lineNumber = arg.getLineNumber();
		instrInfo = arg.getInstrInfo();

		details = new EntryDetails(arg.getEntryDetails());
	}

	public SymbolTableEntry copy() { //shallow copy

		SymbolTableEntry ste = new SymbolTableEntry();
		ste.setName(this.getName());
		ste.setLineNumber(this.getLineNumber());
		ste.setInstrInfo(this.getInstrInfo());

		ste.details = details;
		return ste;
	}

	public SymbolTableEntry copyWithoutRecordFieldList() { //shallow copy

		SymbolTableEntry ste = new SymbolTableEntry();
		ste.setName(this.getName());
		ste.setLineNumber(this.getLineNumber());
		ste.setInstrInfo(this.getInstrInfo());

		EntryDetails ed = new EntryDetails();

		ed.setConstant(this.details.isConstant());
		ed.setField(this.details.isField());
		ed.setRecord(false);
		ed.setTainted(this.details.isTainted());
		ed.setType(this.details.getType());
		ed.setValue(this.details.getValue());
		ed.setState(this.details.getState());

		ste.setEntryDetails(ed);
		return ste;
	}

	public SymbolTableEntry copyEverythingFromInputEntry(SymbolTableEntry ste) { //shallow copy
	//	    	this.setName(ste.getName());
		this.setLineNumber(ste.getLineNumber());
		this.setInstrInfo(ste.getInstrInfo());

		EntryDetails ed = this.getEntryDetails();
		ed.setConstant(ste.getEntryDetails().isConstant());
		ed.setField(ste.getEntryDetails().isField());
		ed.setRecord(ste.getEntryDetails().isRecord());
		ed.setTainted(ste.getEntryDetails().isTainted());
		ed.setType(ste.getEntryDetails().getType());
		ed.setValue(ste.getEntryDetails().getValue());
		ed.setState(ste.getEntryDetails().getState());

		Hashtable steRecordFieldList = ste.getEntryDetails().getRecordFieldList();
		if (steRecordFieldList != null) {
			Hashtable recordFieldList = ed.getRecordFieldList();
			if (recordFieldList == null) {
				recordFieldList = new Hashtable();
			}
			for (Object o : steRecordFieldList.keySet()) {
				String key = (String) o;
				SymbolTableEntry entry = (SymbolTableEntry) steRecordFieldList.get(key);
				recordFieldList.put(key, entry);
			}
			ed.setRecordFieldList(recordFieldList);
		}
		return this;
	}

	@Override
	public Object clone() {
		//shallow copy
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((details == null) ? 0 : details.hashCode());
		result = prime * result + ((instrInfo == null) ? 0 : instrInfo.hashCode());
		result = prime * result + ((lineNumber == null) ? 0 : lineNumber.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof SymbolTableEntry))
			return false;
		SymbolTableEntry other = (SymbolTableEntry) obj;
		if (details == null) {
			if (other.details != null)
				return false;
		} else if (!details.equals(other.details))
			return false;
		if (instrInfo == null) {
			if (other.instrInfo != null)
				return false;
		} else if (!instrInfo.equals(other.instrInfo))
			return false;
		if (lineNumber == null) {
			if (other.lineNumber != null)
				return false;
		} else if (!lineNumber.equals(other.lineNumber))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public String getLineNumber() {
		return lineNumber;
	}

	public void setLineNumber(String lineNumber) {
		this.lineNumber = lineNumber;
	}

	public String getInstrInfo() {
		return instrInfo;
	}

	public void setInstrInfo(String instrInfo) {
		this.instrInfo = instrInfo;
	}

	public EntryDetails getEntryDetails() {
		return details;
	}

	public void setEntryDetails(EntryDetails details) {
		this.details = details;
	}
}