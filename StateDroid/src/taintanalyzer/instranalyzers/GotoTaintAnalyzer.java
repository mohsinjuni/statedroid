package taintanalyzer.instranalyzers;

import models.cfg.InstructionResponse;
import models.cfg.Register;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;

import org.apache.log4j.Logger;

import configuration.Config;

import taintanalyzer.TaintAnalyzer;

public class GotoTaintAnalyzer extends BaseTaintAnalyzer {

	private InstructionResponse ir;
	private SymbolSpace localSymSpace;
	boolean tainted = false;
	String[] used;
	String changed;

	// Instruction format is //  0x5e if-lt v8, v10, +33

	public GotoTaintAnalyzer(TaintAnalyzer ta) {
		this.ir = ta.getIr();
		logger = Logger.getLogger(GotoTaintAnalyzer.class);
		localSymSpace = Config.getInstance().getLocalSymbolSpace();
	}

	public Object analyzeInstruction() {
		logger.debug("\n GotoTaintAnalyzer");

		this.localSymSpace.logInfoSymbolSpace();

		return null;
	}
}
