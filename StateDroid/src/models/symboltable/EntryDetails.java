package models.symboltable;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import patternMatcher.statemachines.State;
import configuration.Config;

public class EntryDetails implements Cloneable {

	private String type = "";
	private String value = "";

	private boolean tainted = false;
	private boolean constant = false;
	private boolean record = false;
	private boolean field = false;
	private State state;

	private ArrayList<SourceInfo> sourceInfoList;
	private Hashtable recordFieldList = null;

	public EntryDetails() {
	}

	public EntryDetails(EntryDetails arg) {
		type = arg.getType();
		value = arg.getValue();
		tainted = arg.isTainted();
		constant = arg.isConstant();
		record = arg.isRecord();
		field = arg.isField();
		state = arg.state;

		//TODO: Use this carefully. The analysis may get stuck in deep-copying all entries.
		if (Config.getInstance().isDeepCopyForRecordFieldList()) {
			recordFieldList = deepCopyHT(arg.getRecordFieldList());
		}

		sourceInfoList = deepCopySrcInfo(arg.getSourceInfoList());

	}

	Hashtable deepCopyHT(Hashtable ht) {
		if (ht == null)
			return ht;

		Hashtable returnHT = null;

		if (ht.size() > 0) {
			returnHT = new Hashtable();
			Enumeration<String> keys = ht.keys();
			while (keys.hasMoreElements()) {
				String key = (String) keys.nextElement();

				SymbolTableEntry origEntry = (SymbolTableEntry) ht.get(key);

				SymbolTableEntry copiedEntry = new SymbolTableEntry(origEntry);

				returnHT.put(copiedEntry.getName(), copiedEntry);
			}

		}
		return returnHT;
	}

	ArrayList<SourceInfo> deepCopySrcInfo(ArrayList<SourceInfo> arg) {
		if (arg == null)
			return arg;

		ArrayList<SourceInfo> returnList = new ArrayList<SourceInfo>();

		for (SourceInfo si : arg) {
			SourceInfo newSI = new SourceInfo(si);
			returnList.add(newSI);
		}

		return returnList;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isTainted() {
		return tainted;
	}

	public void setTainted(boolean tainted) {
		this.tainted = tainted;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public boolean isConstant() {
		return constant;
	}

	public void setConstant(boolean constant) {
		this.constant = constant;
	}

	public boolean isRecord() {
		return record;
	}

	public void setRecord(boolean record) {
		this.record = record;
	}

	public boolean isField() {
		return field;
	}

	public void setField(boolean field) {
		this.field = field;
	}

	public Hashtable getRecordFieldList() {
		return recordFieldList;
	}

	public void setRecordFieldList(Hashtable recordFieldList) {
		this.recordFieldList = recordFieldList;
	}

	public ArrayList<SourceInfo> getSourceInfoList() {
		return sourceInfoList;
	}

	public void setSourceInfoList(ArrayList<SourceInfo> sourceInfoList) {
		this.sourceInfoList = sourceInfoList;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

}
