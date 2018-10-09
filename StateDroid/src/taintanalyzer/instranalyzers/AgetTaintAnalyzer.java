package taintanalyzer.instranalyzers;

import java.util.ArrayList;

import models.cfg.InstructionResponse;
import models.cfg.Register;
import models.symboltable.EntryDetails;
import models.symboltable.SourceInfo;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;

import org.apache.log4j.Logger;

import taintanalyzer.TaintAnalyzer;
import configuration.Config;

public class AgetTaintAnalyzer extends BaseTaintAnalyzer {

	private InstructionResponse ir;
	private SymbolSpace localSymSpace;
	boolean tainted = false;
	String[] used;
	String changed;

	/*
	 * 0xc4 aget-char v0, v9, v8
	 * Value stored at index v8 of array v9 is obtained and stored in v0.
	 * 
	 * This class handles all primitive types only. A new entry shoud/must be
	 * created to store the result.
	 * 
	 * (non-Javadoc)
	 * 
	 * @see handler.BaseHandler#execute()
	 */
	public AgetTaintAnalyzer(TaintAnalyzer ta) {
		this.ir = ta.getIr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();
		logger = Logger.getLogger(AgetTaintAnalyzer.class);
	}

	public Object analyzeInstruction() {
		Register destReg = ir.getInvolvedRegisters().get(0);
		Register srcReg = ir.getInvolvedRegisters().get(1);
		Register indexReg = ir.getInvolvedRegisters().get(2);

		//       SymbolTableEntry destEntry=localSymSpace.find(destReg.getName()); //Since new entry will replace the existing one anyway.
		SymbolTableEntry srcEntry = localSymSpace.find(srcReg.getName());
		SymbolTableEntry indexEntry = localSymSpace.find(indexReg.getName());

		SymbolTableEntry destEntry = new SymbolTableEntry();

		EntryDetails destEntryDetails = destEntry.getEntryDetails();

		destEntryDetails.setType(ir.getReturnType());

		destEntry.setName(destReg.getName());
		destEntry.setLineNumber(ir.getLineNumber());
		destEntry.setInstrInfo("");

		if (srcEntry != null) {
			destEntryDetails.setType(srcEntry.getEntryDetails().getType());
			destEntryDetails.setConstant(srcEntry.getEntryDetails().isConstant());
		} else {
			destEntryDetails.setType(destReg.getType());
			destEntryDetails.setConstant(false);
		}

		destEntryDetails.setTainted(false);
		ArrayList<SourceInfo> srcInfoList = null; // new ArrayList<SourceInfo>();
		if (indexEntry != null && indexEntry.getEntryDetails().isTainted()) {
			destEntryDetails.setTainted(true);
			ArrayList<SourceInfo> existingSilist = indexEntry.getEntryDetails().getSourceInfoList();

			if (existingSilist != null && existingSilist.size() > 0) {
				srcInfoList = new ArrayList<SourceInfo>();
				for (SourceInfo si : existingSilist) {
					if (!srcInfoList.contains(si)) {
						srcInfoList.add(si);
					}
				}
			}
		}

		if (srcEntry != null && srcEntry.getEntryDetails().isTainted()) {
			destEntryDetails.setTainted(true);
			ArrayList<SourceInfo> existingSilist = srcEntry.getEntryDetails().getSourceInfoList();

			if (existingSilist != null && existingSilist.size() > 0) {
				if (srcInfoList == null) {
					srcInfoList = new ArrayList<SourceInfo>();
				}
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

		localSymSpace.addEntry(destEntry);

		logger.debug("\n AgetTaintAnalyzer");
		localSymSpace.logInfoSymbolSpace();

		return null;
	}
}
