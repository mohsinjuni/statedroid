package models.symboltable;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Stack;

import org.apache.log4j.Logger;

public class Context implements Cloneable {

	private Stack symTabContextStack;
	private static Logger logger;
	private String key;

	public Context() {
		symTabContextStack = new Stack();
		logger = Logger.getLogger(Context.class);
	}

	public void copyData(Context contParam) {
		Stack stck = new Stack();

	}

	public Stack getSymTabContextStack() {
		return symTabContextStack;
	}

	public void setSymTabContextStack(Stack symTabContextStack) {
		this.symTabContextStack = symTabContextStack;
	}

	public void addItem(Hashtable ht) {
		symTabContextStack.push(ht);
		logger.debug("contextSize after addItem" + symTabContextStack.size());
	}

	public Hashtable popItem() {
		Hashtable ht = (Hashtable) symTabContextStack.pop();
		return ht;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		Context cloned = (Context) super.clone();
		cloned.setSymTabContextStack((Stack) cloned.getSymTabContextStack().clone());
		return cloned;
	}

	public Hashtable peekTopItem() {
		if (symTabContextStack.size() > 0) {
			Hashtable ht = (Hashtable) symTabContextStack.peek();

			// ht contains only reference to the same item. So we want to add new item in the memory for each scenario.
			Hashtable returnHT = new Hashtable();

			return ht;
		}
		return null;
	}

	public int getStackSize() {
		if (symTabContextStack != null)
			return symTabContextStack.size();
		else
			return 0;
	}

	public void printContext() {
		logger.debug(" --> printing Context start, size " + symTabContextStack.size());
		for (int i = symTabContextStack.size() - 1; i >= 0; i--) {
			Hashtable htable = (Hashtable) symTabContextStack.get(i);
			if (htable != null) {
				Enumeration<String> enumKey = htable.keys();
				while (enumKey.hasMoreElements()) {
					String key = enumKey.nextElement().toString();
					SymbolTableEntry ent = (SymbolTableEntry) htable.get(key);

					if (ent.getEntryDetails().isRecord()) {
						printRecursiveRecord(ent);
					} else {
						logger.debug("@@ NOT RECORD @@@ , key, " + ent.getName() + ", Type -> " + ent.getEntryDetails().getType()
								+ " , tainted?: " + ent.getEntryDetails().isTainted() + ", value = " + ent.getEntryDetails().getValue());

					}
				}

			}
		}

		logger.debug(" <-- printing Context end");
	}

	public void printRecursiveRecord(SymbolTableEntry ent) {
		if (!ent.getEntryDetails().isRecord())
			return;
		else {
			logger.debug(" @@record @@" + ent.getName() + ", <<< Type = " + ent.getEntryDetails().getType() + ", value = "
					+ ent.getEntryDetails().getValue());

			Hashtable htable = (Hashtable) ent.getEntryDetails().getRecordFieldList();
			if (htable != null) {
				Enumeration<String> enumKey = htable.keys();
				while (enumKey.hasMoreElements()) {
					String key = enumKey.nextElement().toString();
					SymbolTableEntry tempEnt = (SymbolTableEntry) htable.get(key);
					//     			  
					if (tempEnt != null) {
						logger.debug("<< field Data >>  fieldName, " + tempEnt.getName() + ", Type -> "
								+ tempEnt.getEntryDetails().getType() + " , " + tempEnt.getEntryDetails().isTainted() + ",  record = "
								+ tempEnt.getEntryDetails().isRecord() + ", value >>" + tempEnt.getEntryDetails().getValue() + ", const>> "
								+ tempEnt.getEntryDetails().isConstant());
					}

				}
			}

		}

	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

}
