package taintanalyzer.instranalyzers;

import java.util.Hashtable;

import models.cfg.InstructionResponse;
import models.cfg.Register;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;

import org.apache.log4j.Logger;

import configuration.Config;

import taintanalyzer.TaintAnalyzer;

public class MoveObjectTaintAnalyzer extends BaseTaintAnalyzer {

	private InstructionResponse ir;
	private SymbolSpace localSymSpace;
	boolean tainted = false;
	String[] used;
	String changed;

	//	move-object vB vA .. vB is destination and VA is source here. 
	// need shallow copy.    

	public MoveObjectTaintAnalyzer(TaintAnalyzer ta) {
		this.ir = ta.getIr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();
		logger = Logger.getLogger(MoveObjectTaintAnalyzer.class);
	}

	public Object analyzeInstruction() {

		String instrText = ir.getInstr().getText();
		if (instrText.contains("0x4c4 move-object/from16 v0, v18")) {
			//			System.out.println(instrText);
		}
		Register destReg = ir.getInvolvedRegisters().get(0);
		Register srcReg = ir.getInvolvedRegisters().get(1);

		SymbolTableEntry destEntry = null;
		SymbolTableEntry srcEntry = localSymSpace.find(srcReg.getName());

		if (srcEntry != null) {
			destEntry = (SymbolTableEntry) srcEntry.clone(); // Shallow copy

			destEntry.setLineNumber(ir.getLineNumber());
			destEntry.setName(destReg.getName());
			destEntry.setInstrInfo(ir.getInstr().getText());

			this.localSymSpace.addEntry(destEntry); // It just replaces the existing one, if there is one.
		}

		logger.debug("\n MoveResultTaintAnalyzer");
		localSymSpace.logInfoSymbolSpace();

		return null;
	}
}
