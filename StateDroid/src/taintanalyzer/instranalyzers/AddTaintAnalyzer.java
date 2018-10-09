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

public class AddTaintAnalyzer extends BaseTaintAnalyzer {

	private InstructionResponse ir;
	private SymbolSpace localSymSpace;
	boolean tainted = false;
	String[] used;
	String changed;

	/*
	 * 0xf6 add-int/lit8 v8, v8, 1
	 * 
	 * add-int/lit8 vx,vy,lit8 | Adds vy to lit8 and stores the result into vx.
	 * ushr-int/lit8 vx, vy, lit8
	 * 
	 * 
	 * This means that vx always gets a new entry.
	 * 
	 * Other types of add operations may involve three registers.
	 */
	public AddTaintAnalyzer(TaintAnalyzer ta) {
		this.ir = ta.getIr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();
		logger = Logger.getLogger(AddTaintAnalyzer.class);
	}

	// 	0x1c add-int v3, v7, v2
	public Object analyzeInstruction() {

		Register destReg = ir.getInvolvedRegisters().get(0); // v3 
		Register srcReg = ir.getInvolvedRegisters().get(1); // v7

		SymbolTableEntry srcEntry = localSymSpace.find(srcReg.getName());
		//       0x1c add-int v3, v7, v2

		if (destReg.getName().equals(srcReg.getName())) {
			//Do nothing case.
			// add-int/... v3 v3 1 
		} else {
			// add-int/... v3 v7 1 . Create a new entry and store result into it.

			//Even if there is an existing entry, it will be replaced with the same key automatically.
			//existing entry may be tainted already.

			SymbolTableEntry destEntry = localSymSpace.find(destReg.getName());
			if (destEntry != null) {
				EntryDetails destEntryDetails = destEntry.getEntryDetails();
				destEntry.setLineNumber(ir.getLineNumber());
				destEntry.setInstrInfo("");

				if (srcEntry != null && srcEntry.getEntryDetails().isTainted()) {
					destEntryDetails.setTainted(true);
					ArrayList<SourceInfo> existingSiList = srcEntry.getEntryDetails().getSourceInfoList();
					ArrayList<SourceInfo> siList = destEntryDetails.getSourceInfoList();

					if (existingSiList != null && existingSiList.size() > 0) {
						if (siList == null)
							siList = new ArrayList<SourceInfo>();
						for (SourceInfo si : existingSiList) {
							if (!siList.contains(si))
								siList.add(si);
						}
					}
					destEntryDetails.setSourceInfoList(siList);
				}

				destEntryDetails.setField(false);
				destEntryDetails.setRecord(false);

				destEntryDetails.setValue("");

				destEntry.setEntryDetails(destEntryDetails);
			} else {

				destEntry = new SymbolTableEntry();

				EntryDetails destEntryDetails = destEntry.getEntryDetails();

				destEntry.setName(destReg.getName());
				destEntry.setLineNumber(ir.getLineNumber());
				destEntry.setInstrInfo("");

				if (srcEntry != null) {
					if (srcEntry.getEntryDetails().getType() != null)
						destEntryDetails.setType(srcEntry.getEntryDetails().getType());
					else
						destEntryDetails.setType(destReg.getType());
					destEntryDetails.setConstant(srcEntry.getEntryDetails().isConstant());

				} else {
					destEntryDetails.setType(destReg.getType());
					destEntryDetails.setConstant(false);
				}
				destEntryDetails.setTainted(false);
				if (srcEntry != null && srcEntry.getEntryDetails().isTainted()) {
					destEntryDetails.setTainted(true);
					ArrayList<SourceInfo> existingSiList = srcEntry.getEntryDetails().getSourceInfoList();
					ArrayList<SourceInfo> siList = destEntryDetails.getSourceInfoList();

					if (existingSiList != null && existingSiList.size() > 0) {
						if (siList == null)
							siList = new ArrayList<SourceInfo>();
						for (SourceInfo si : existingSiList) {
							if (!siList.contains(si))
								siList.add(si);
						}
					}
					destEntryDetails.setSourceInfoList(siList);
				}

				destEntryDetails.setField(false);
				destEntryDetails.setRecord(false);
				destEntryDetails.setValue("");
				destEntry.setEntryDetails(destEntryDetails);
				localSymSpace.addEntry(destEntry); 
			}

		}
		logger.debug("\n AddTaintAnalyzer");
		localSymSpace.logInfoSymbolSpace();

		return null;
	}
}
