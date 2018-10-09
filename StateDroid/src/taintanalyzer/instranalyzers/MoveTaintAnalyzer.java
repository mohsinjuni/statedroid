package taintanalyzer.instranalyzers;

import java.util.Hashtable;

import models.cfg.InstructionResponse;
import models.cfg.Register;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;

import org.apache.log4j.Logger;

import configuration.Config;

import taintanalyzer.TaintAnalyzer;

public class MoveTaintAnalyzer extends BaseTaintAnalyzer {

	private InstructionResponse ir;
	private SymbolSpace localSymSpace;
	boolean tainted = false;
	String[] used;
	String changed;

	//	move vB vA .. vB is destination and VA is source here. 
	public MoveTaintAnalyzer(TaintAnalyzer ta) {
		this.ir = ta.getIr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();
		logger = Logger.getLogger(MoveTaintAnalyzer.class);
	}

	public Object analyzeInstruction() {
		Register destReg = ir.getInvolvedRegisters().get(0);
		Register srcReg = ir.getInvolvedRegisters().get(1);

		SymbolTableEntry destEntry = null;
		SymbolTableEntry srcEntry = localSymSpace.find(srcReg.getName());

		if (srcEntry != null) {
			destEntry = new SymbolTableEntry(srcEntry); // deep copy

			destEntry.setLineNumber(ir.getLineNumber());
			destEntry.setName(destReg.getName());
			destEntry.setInstrInfo(ir.getInstr().getText());

			this.localSymSpace.addEntry(destEntry); // It just replaces the existing one, if there is one.
		}

		logger.debug("\n MoveDestSrcTaintAnalyzer");
		localSymSpace.logInfoSymbolSpace();

		return null;
	}
}
