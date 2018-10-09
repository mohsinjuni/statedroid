package models.symboltable;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Stack;

import org.apache.log4j.Logger;

public class SymbolSpace {

	private Stack entries = new Stack();

	private static Logger logger = Logger.getLogger("SymbolSpace");

	private int level = 0; // level 0 for Activity/File/Class level variables.

	public void push() {
		Hashtable curHash = new Hashtable();
		entries.push(curHash);
		level++;

		logger.debug(" [push() ]symbolSpace level after push => " + level + " <itemsCount> is: " + entries.size());
	}

	public void push(Hashtable curHash) {
		entries.push(curHash);
		level++;
		logger.debug(" [push(Hashtable) ]symbolSpace level after push=> " + level + " <itemsCount> is: " + entries.size());

	}

	public Hashtable pop() {
		Hashtable returnItem = (Hashtable) entries.pop();
		level--;
		logger.debug(" [pop() ]symbolSpace level after pop=> " + level + " <itemsCount> is: " + entries.size());
		return returnItem;

	}

	public boolean removeEntriesUptoInclusiveLevel(int levelParam) {
		logger.debug("____ Current level -> " + level + " <itemsCount> is: " + entries.size());
		int currLevel = level;
		int size = entries.size();

		for (int k = size - 1; k >= levelParam; k--) {
			entries.pop();
			level--;
		}
		logger.debug("____ entriedRemovedupto level " + levelParam);
		return true;
	}

	public boolean addEntry(SymbolTableEntry entry) {
		Hashtable ht = (Hashtable) entries.peek();
		SymbolTableEntry e = (SymbolTableEntry) ht.get(entry.getName());
		ht.put(entry.getName(), entry);
		return true;
	}

	public boolean addEntry(SymbolTableEntry entry, int level) {
		Hashtable ht = (Hashtable) entries.get(level);

		SymbolTableEntry e = (SymbolTableEntry) ht.get(entry.getName());
		ht.put(entry.getName(), entry);
		return true;
	}

	public boolean removeEntry(String entryName, int level) {
		Hashtable ht = (Hashtable) entries.get(level);

		ht.remove(entryName);
		return true;

	}

	public boolean removeEntry(String entryName) {
		Hashtable ht = (Hashtable) entries.peek();

		ht.remove(entryName);
		return true;

	}

	public SymbolTableEntry find(String entryName) {
		return findRecursive(entryName);
	}

	public SymbolTableEntry findRecursive(String entryName) {
		for (int i = entries.size() - 1; i >= 0; i--) {
			Hashtable ht = (Hashtable) entries.get(i);
			if (ht != null && entryName != null) {
				// First check if it is available directly in this table, else look into each entry's record field list.
				SymbolTableEntry entry = (SymbolTableEntry) ht.get(entryName);
				if (entry != null) {
					return entry;
				} else {
					Iterator it = ht.keySet().iterator();
					while (it.hasNext()) {
						String key = (String) it.next();
						SymbolTableEntry tempEnt = (SymbolTableEntry) ht.get(key);

						if (tempEnt.getEntryDetails().isRecord()) {
							SymbolTableEntry retEntry = recursiveFindEntryFromRecord(tempEnt, entryName);

							if (retEntry != null)
								return retEntry;
						}
					}
				}
			}

		}
		return null;
	}

	public SymbolTableEntry recursiveFindEntryFromRecord(SymbolTableEntry entry, String searchEntry) {
		// 	 if(!entry.isRecord())
		// 		 return null;
		if (entry.getName().equals(searchEntry)) {
			return entry;
		} else {
			Hashtable htable = (Hashtable) entry.getEntryDetails().getRecordFieldList();
			if (htable != null) {
				Enumeration<String> enumKey = htable.keys();
				while (enumKey.hasMoreElements()) {
					String key = enumKey.nextElement().toString();
					SymbolTableEntry tempEnt = (SymbolTableEntry) htable.get(key);

					if (tempEnt.getName().equalsIgnoreCase(searchEntry)) {
						return tempEnt;
					}
				}
			}
			return null;
		}

	}

	public SymbolTableEntry find(String recordName, String fieldName) {
		return findRecursive(recordName, fieldName);
	}

	public SymbolTableEntry findRecursive(String recordName, String entryName) {
		SymbolTableEntry resultEntry = null;
		for (int i = entries.size() - 1; i >= 0; i--) {
			Hashtable ht = (Hashtable) entries.get(i);
			if (ht != null && recordName != null) {
				SymbolTableEntry recordEntry = (SymbolTableEntry) ht.get(recordName);
				if (recordEntry != null) {
					resultEntry = findEntryFromGivenRecord(recordName, recordEntry, entryName);

					if (resultEntry != null)
						return resultEntry;
					else {
						Iterator it = ht.keySet().iterator();
						while (it.hasNext()) {
							String key = (String) it.next();
							SymbolTableEntry tempEnt = (SymbolTableEntry) ht.get(key);

							if (tempEnt.getEntryDetails().isRecord()) {
								resultEntry = findEntryFromGivenRecord(recordName, tempEnt, entryName);
							}
						}
					}
				}
			}
		}
		return resultEntry;
	}

	public SymbolTableEntry findEntryFromGivenRecord(String recordName, SymbolTableEntry record, String searchEntry) {
		//	 if(!entry.isRecord())
		//		 return null;
		logger.debug("<< findEntryFromGivenRecord >> recordName" + recordName + ", searchEntry " + searchEntry);

		if (record.getName().equals(searchEntry) && record.getName().equals(recordName)) {
			return record;
		} else {
			Hashtable htable = (Hashtable) record.getEntryDetails().getRecordFieldList();
			if (htable != null) {
				Enumeration<String> enumKey = htable.keys();
				while (enumKey.hasMoreElements()) {
					String key = enumKey.nextElement().toString();
					SymbolTableEntry tempEnt = (SymbolTableEntry) htable.get(key);

					if (tempEnt.getName().equals(searchEntry) && record.getName().equals(recordName)) {
						return tempEnt;
					}
				}
			}
			return null;
		}

	}

	public void logInfoSymbolSpace() {
		logger.debug(" --> printing SymbolSpace start, size " + entries.size());
		if (entries != null) {
			for (int i = entries.size() - 1; i >= 0; i--) {
				logger.debug("LEVEL = " + i);
				Hashtable htable = (Hashtable) entries.get(i);
				if (htable != null) {
					Enumeration<String> enumKey = htable.keys();
					while (enumKey.hasMoreElements()) {
						String key = enumKey.nextElement().toString();
						SymbolTableEntry ent = (SymbolTableEntry) htable.get(key);
						//      			 
						if (ent.getEntryDetails().isRecord()) {
							printRecursiveRecord(ent);
						} else {
							logger.debug(" key ->" + ent.getName() + ", TYPE -> " + ent.getEntryDetails().getType() + " , Tainted? "
									+ ent.getEntryDetails().isTainted() + ",  record = " + ent.getEntryDetails().isRecord() + " , value = "
									+ ent.getEntryDetails().getValue() + ", const>> " + ent.getEntryDetails().isConstant()
							//		      					  + ", sourceAPI = ," + ent.getEntryDetails().getSourceInfo().getSrcAPI()
							);
						}

					}
				}
			}
		}
		logger.debug(" <-- printing SymbolSpace end");

	}

	public void printRecursiveRecord(SymbolTableEntry ent) {
		logger.debug(" record >>>>>>>> " + ent.getName() + ", recordType>>>" + ent.getEntryDetails().getType() + ", recordValue>>>"
				+ ent.getEntryDetails().getValue());

		Hashtable htable = (Hashtable) ent.getEntryDetails().getRecordFieldList();
		if (htable != null) {
			Enumeration<String> enumKey = htable.keys();
			while (enumKey.hasMoreElements()) {
				String key = enumKey.nextElement().toString();

				logger.debug("** fieldKey ==" + key);
				SymbolTableEntry tempEnt = (SymbolTableEntry) htable.get(key);

				logger.debug("<< field Data >>  fieldName, " + tempEnt.getName() + ", Type -> " + tempEnt.getEntryDetails().getType()
						+ " , " + tempEnt.getEntryDetails().isTainted() + ",  record = " + tempEnt.getEntryDetails().isRecord()
						+ ", value >>" + tempEnt.getEntryDetails().getValue() + ", const>> " + tempEnt.getEntryDetails().isConstant()
				//      					  + ", sourceAPI = ," + ent.getSourceAPI()
				);
			}

		}

	}

	public SymbolTableEntry findByType(String entryType) {
		return findRecursiveByType(entryType);
	}

	public SymbolTableEntry findRecursiveByType(String entryType) {
		for (int i = 0; i < entries.size(); i++) {
			Hashtable ht = (Hashtable) entries.get(i);
			if (ht != null && entryType != null) {
				Iterator it = ht.keySet().iterator();
				while (it.hasNext()) {
					String key = (String) it.next();
					SymbolTableEntry tempEnt = (SymbolTableEntry) ht.get(key);

					if (tempEnt != null && tempEnt.getEntryDetails().getType() != null) {
						if (tempEnt.getEntryDetails().getType().equalsIgnoreCase(entryType)) {
							return tempEnt;
						} else if (tempEnt.getEntryDetails().isRecord()) {
							SymbolTableEntry retEntry = recursiveFindEntryFromRecordByType(tempEnt, entryType);

							if (retEntry != null && retEntry.getEntryDetails().getType().equalsIgnoreCase(entryType)) {
								return retEntry;
							}
						}
					}
				}
			}

		}
		return null;
	}

	public SymbolTableEntry recursiveFindEntryFromRecordByType(SymbolTableEntry entry, String searchEntry) {
		if (entry.getEntryDetails().getType().equals(searchEntry)) {
			return entry;
		} else {
			Hashtable htable = (Hashtable) entry.getEntryDetails().getRecordFieldList();
			if (htable != null) {
				Enumeration<String> enumKey = htable.keys();
				while (enumKey.hasMoreElements()) {
					String key = enumKey.nextElement().toString();
					SymbolTableEntry tempEnt = (SymbolTableEntry) htable.get(key);

					if (tempEnt != null && tempEnt.getEntryDetails().getType() != null) {
						if (tempEnt.getEntryDetails().getType().equalsIgnoreCase(searchEntry)) {
							return tempEnt;
						}
					}
				}
			}
			return null;
		}

	}

	public void printOneEntry(SymbolTableEntry entryParam) {

		String msg = "PrintingOneEntry";

		if (entryParam != null) {

			msg += "\n name >> " + entryParam.getName();
			msg += "type >> " + entryParam.getEntryDetails().getType();

			Hashtable fieldList = entryParam.getEntryDetails().getRecordFieldList();
			if (fieldList != null && fieldList.size() > 0) {
				Enumeration<String> enumKeys = fieldList.keys();
				while (enumKeys.hasMoreElements()) {
					String key = enumKeys.nextElement();
					SymbolTableEntry entry = (SymbolTableEntry) fieldList.get(key);
					msg += "\n field >>> name =" + entry.getName() + ", type = " + entry.getEntryDetails().getType();
				}
			}
		}

		logger.debug(msg);

	}

	public Stack getEntries() {
		return entries;
	}

	public void setEntries(Stack entries) {
		this.entries = entries;
	}

	public Hashtable getItem(int index) {
		Hashtable ht = null;

		Hashtable returnHT = new Hashtable();

		//    	if(index < entries.size())
		//    		return (Hashtable) entries.remove(index);

		if (index < entries.size())
			ht = (Hashtable) entries.get(index);
		return ht;

	}

	public Hashtable exitScope() {
		Hashtable out = (Hashtable) entries.pop();
		setLevel(getLevel() - 1);
		return out;
	}

	public void enterScope() {
		Hashtable curHash = new Hashtable();
		entries.push(curHash);
		setLevel(getLevel() + 1);
	}

	public void enterScope(Hashtable curHash) {
		entries.push(curHash);
		setLevel(getLevel() + 1);

	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}
}
