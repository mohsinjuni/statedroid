package taintanalyzer.instranalyzers;

import java.util.ArrayList;
import java.util.Hashtable;

import models.cfg.InstructionResponse;
import models.cfg.Register;
import models.symboltable.EntryDetails;
import models.symboltable.SourceInfo;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;

import org.apache.log4j.Logger;

import taintanalyzer.TaintAnalyzer;
import configuration.Config;

public class AgetObjectTaintAnalyzer extends BaseTaintAnalyzer {

	private InstructionResponse ir;
	private SymbolSpace localSymSpace;
	boolean tainted = false;
	String[] used;
	String changed;

	/*
	 * aget-object v3, v7, v8
	 * 
	 * Value stored at index v8 of array v7 is obtained and stored in v3.
	 * 
	 * Ideally, we should get object from the array but we don't have values
	 * stored in the array. :(
	 * (non-Javadoc)
	 * 
	 * @see handler.BaseHandler#execute()
	 */
	public AgetObjectTaintAnalyzer(TaintAnalyzer ta) {
		this.ir = ta.getIr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();
		logger = Logger.getLogger(AgetObjectTaintAnalyzer.class);
	}

	// aget-object v3, v7, v8
	public Object analyzeInstruction() {
		Register destReg = ir.getInvolvedRegisters().get(0);
		Register srcReg = ir.getInvolvedRegisters().get(1);
		Register indexReg = ir.getInvolvedRegisters().get(2);

		SymbolTableEntry srcEntry = localSymSpace.find(srcReg.getName());
		SymbolTableEntry indexEntry = localSymSpace.find(indexReg.getName());

		SymbolTableEntry destEntry = localSymSpace.find(destReg.getName());

		Hashtable<String, String> immutableTypes = Config.getInstance().getImmutableObjects();
		boolean isNewCopy = false;
		// [[Ljava/lang/String; if it's more than one dimensional, make shallow-copy; if its non-primitive type, make shallow-copy.
		// 	// aget-object v3, v7, v8
		if (srcEntry != null) {
			if (destEntry != null) {
				this.localSymSpace.removeEntry(destEntry.getName());
			}
			String entryType = srcEntry.getEntryDetails().getType();
			int dimCount = 0;
			if (entryType != null && !entryType.isEmpty()) {
				dimCount = getDimensionCount(entryType);
			}
			if (dimCount > 1) { //fore more than one-dimensional array
				destEntry = (SymbolTableEntry) srcEntry.clone();
			} else if (dimCount <= 1) {
				if (immutableTypes.containsKey(entryType)) {
					destEntry = new SymbolTableEntry(srcEntry);
				} else {
					destEntry = (SymbolTableEntry) srcEntry.clone();
				}
			}
			EntryDetails destEntryDetails = destEntry.getEntryDetails();
			ArrayList<SourceInfo> srcInfoList = destEntryDetails.getSourceInfoList();

			if (indexEntry != null && indexEntry.getEntryDetails().isTainted()) {
				destEntryDetails.setTainted(true);
				ArrayList<SourceInfo> existingSilist = indexEntry.getEntryDetails().getSourceInfoList();

				if (existingSilist != null && existingSilist.size() > 0) {
					if (srcInfoList == null)
						srcInfoList = new ArrayList<SourceInfo>();
					for (SourceInfo si : existingSilist) {
						if (!srcInfoList.contains(si)) {
							srcInfoList.add(si);
						}
					}
				}
			}

			destEntryDetails.setSourceInfoList(srcInfoList); // in case,it is untainted, arraylist is empty.

			destEntryDetails.setField(false);
			destEntryDetails.setRecord(false);
			destEntryDetails.setValue("");
			destEntry.setEntryDetails(destEntryDetails);
			destEntry.setName(destReg.getName());

			//		   if(isNewCopy){
			localSymSpace.addEntry(destEntry);
			//		   }
		} else {
			destEntry = new SymbolTableEntry();
			EntryDetails destEntryDetails = destEntry.getEntryDetails();
			destEntryDetails.setType(ir.getReturnType());
			destEntry.setName(destReg.getName());
			destEntry.setLineNumber(ir.getLineNumber());
			destEntry.setInstrInfo("");
			destEntryDetails.setType(destReg.getType());
			destEntryDetails.setConstant(false);

			ArrayList<SourceInfo> srcInfoList = destEntryDetails.getSourceInfoList();

			if (indexEntry != null && indexEntry.getEntryDetails().isTainted()) {
				destEntryDetails.setTainted(true);
				ArrayList<SourceInfo> existingSilist = indexEntry.getEntryDetails().getSourceInfoList();

				if (existingSilist != null && existingSilist.size() > 0) {
					if (srcInfoList == null)
						srcInfoList = new ArrayList<SourceInfo>();
					for (SourceInfo si : existingSilist) {
						if (!srcInfoList.contains(si)) {
							srcInfoList.add(si);
						}
					}
				}
			}
			destEntryDetails.setSourceInfoList(srcInfoList);
			destEntryDetails.setField(false);
			destEntryDetails.setRecord(false);
			destEntryDetails.setValue("");
			destEntry.setEntryDetails(destEntryDetails);
			localSymSpace.addEntry(destEntry);
		}
		logger.debug("\n AgetTaintAnalyzer");
		localSymSpace.logInfoSymbolSpace();

		return null;
	}

	int getDimensionCount(String type) {
		int count = 0;
		char[] typeArr = type.toCharArray();
		for (char c : typeArr) {
			if (c == '[') {
				count++;
			}
		}
		return count++;
	}
}
