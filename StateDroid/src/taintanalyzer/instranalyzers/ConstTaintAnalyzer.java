package taintanalyzer.instranalyzers;

import java.util.ArrayList;

import models.cfg.InstructionResponse;
import models.cfg.Register;
import models.symboltable.Context;
import models.symboltable.EntryDetails;
import models.symboltable.SourceInfo;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;

import org.apache.log4j.Logger;

import configuration.Config;

import taintanalyzer.TaintAnalyzer;

public class ConstTaintAnalyzer extends BaseTaintAnalyzer {

	private InstructionResponse ir;
	private SymbolSpace localSymSpace;
	boolean tainted = false;
	String[] used;
	String changed;

	// Instruction format is 	0x5c const/4 v8, 0

	public ConstTaintAnalyzer(TaintAnalyzer ta) {
		this.ir = ta.getIr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();
		logger = Logger.getLogger(ConstTaintAnalyzer.class);
	}

	public Object analyzeInstruction() {
		Register destReg = ir.getInvolvedRegisters().get(0);

		SymbolTableEntry entry = new SymbolTableEntry();

		Context ctxt = Config.getInstance().getPrevMethodContext();
		ctxt.printContext();

		EntryDetails destEntryDetails = entry.getEntryDetails();

		entry = new SymbolTableEntry();

		entry.setName(destReg.getName());
		entry.setInstrInfo(" ");
		entry.setLineNumber(ir.getLineNumber());

		destEntryDetails.setType("I");
		destEntryDetails.setTainted(false);
		destEntryDetails.setConstant(true);
		destEntryDetails.setValue(destReg.getValue());
		destEntryDetails.setField(false);
		destEntryDetails.setRecord(false);

		entry.setEntryDetails(destEntryDetails);

		localSymSpace.addEntry(entry);

		logger.debug("\n ConstTaintAnalyzer");
		localSymSpace.logInfoSymbolSpace();
		return null;
	}
}
