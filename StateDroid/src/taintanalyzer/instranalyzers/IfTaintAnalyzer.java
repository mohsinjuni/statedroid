package taintanalyzer.instranalyzers;

import models.cfg.InstructionResponse;
import models.cfg.Register;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;

import org.apache.log4j.Logger;

import taintanalyzer.TaintAnalyzer;

public class IfTaintAnalyzer extends BaseTaintAnalyzer {

	private InstructionResponse ir;
	private SymbolSpace localSymSpace;
	boolean tainted = false;
	String[] used;
	String changed;

	// Instruction format is //  0x5e if-lt v8, v10, +33

	public IfTaintAnalyzer(TaintAnalyzer ta) {
		this.ir = ta.getIr();
		logger = Logger.getLogger(IfTaintAnalyzer.class);
	}

	public Object analyzeInstruction() {
		logger.debug("\n IfTaintAnalyzer");

		return null;
	}
}
