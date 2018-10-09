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

public class ArrayLengthTaintAnalyzer extends BaseTaintAnalyzer {

	private InstructionResponse ir;
	private SymbolSpace localSymSpace;
	boolean tainted = false;
	String[] used;
	String changed;

	//	0x5a array-length v10, v9
	public ArrayLengthTaintAnalyzer(TaintAnalyzer ta) {
		this.ir = ta.getIr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();
		logger = Logger.getLogger(ArrayLengthTaintAnalyzer.class);
	}

	public Object analyzeInstruction() {
		Register destReg = ir.getInvolvedRegisters().get(0);
		Register srcReg = ir.getInvolvedRegisters().get(1);

		SymbolTableEntry srcEntry = localSymSpace.find(srcReg.getName());

		//	   	0x5a array-length v10, v9 , v10=dest
		// It should always createa a new entry.       

		SymbolTableEntry destEntry = new SymbolTableEntry();

		EntryDetails destEntryDetails = destEntry.getEntryDetails();
		ArrayList<SourceInfo> siList = destEntry.getEntryDetails().getSourceInfoList();

		destEntry.setName(destReg.getName());
		destEntry.setInstrInfo("");

		destEntryDetails.setType("I");
		destEntry.setLineNumber(ir.getLineNumber());

		if (srcEntry != null) {
			destEntryDetails.setTainted(srcEntry.getEntryDetails().isTainted());
			ArrayList<SourceInfo> existingSilist = srcEntry.getEntryDetails().getSourceInfoList();

			if (existingSilist != null && existingSilist.size() > 0) {
				if (siList == null)
					siList = new ArrayList<SourceInfo>();
				for (SourceInfo si : existingSilist) {
					if (!siList.contains(si))
						siList.add(si);
				}
			}
			destEntry.getEntryDetails().setSourceInfoList(siList);
		}

		destEntryDetails.setConstant(true);
		destEntryDetails.setField(false);
		destEntryDetails.setRecord(false);
		destEntryDetails.setValue(destReg.getValue());
		destEntry.setEntryDetails(destEntryDetails);

		localSymSpace.addEntry(destEntry);

		logger.debug("\n ArrayTaintAnalyzer");
		localSymSpace.logInfoSymbolSpace();

		return null;
	}
}
