package taintanalyzer.instranalyzers;

import java.util.ArrayList;
import java.util.Hashtable;

import models.cfg.Instruction;
import models.cfg.InstructionResponse;
import models.cfg.Register;
import models.symboltable.EntryDetails;
import models.symboltable.SourceInfo;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;

import org.apache.log4j.Logger;

import taintanalyzer.TaintAnalyzer;
import configuration.Config;

public class ConstClassTaintAnalyzer extends BaseTaintAnalyzer {

	private InstructionResponse ir;
	private SymbolSpace localSymSpace;
	boolean tainted = false;
	String[] used;
	String changed;

	/*
	 * 
	 * 0x20e const-class v29, Landroid/telephony/TelephonyManager;
	 * 
	 * v29.value = Landroid/telephony/TelephonyManager; , v29.type =
	 * Ljava/lang/Class;
	 */

	public ConstClassTaintAnalyzer(TaintAnalyzer ta) {
		this.ir = ta.getIr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();
		logger = Logger.getLogger(ConstClassTaintAnalyzer.class);
	}

	public Object analyzeInstruction() {
		Register destReg = ir.getInvolvedRegisters().get(0);

		String apiInfo = "";

		Instruction instr = ir.getInstr();
		apiInfo = String.valueOf(" [PkgClass] = ").concat(
				instr.getCurrPkgClassName().concat(" , [method] = ").concat(instr.getCurrMethodName()));

		// In all cases, it should create a new entry and replace the existing one in the symbol table.

		SymbolTableEntry destEntry = new SymbolTableEntry();

		EntryDetails destEntryDetails = destEntry.getEntryDetails();

		destEntry = new SymbolTableEntry();

		destEntry.setName(destReg.getName());
		destEntry.setInstrInfo(apiInfo);
		destEntry.setLineNumber(ir.getLineNumber());

		destEntryDetails.setType("Ljava/lang/Class;");
		destEntryDetails.setTainted(false);
		destEntryDetails.setConstant(true);
		destEntryDetails.setValue(destReg.getValue().trim());
		destEntryDetails.setField(false);
		destEntryDetails.setRecord(false);

		destEntry.setEntryDetails(destEntryDetails);
		localSymSpace.addEntry(destEntry);

		logger.debug("\n ConstClassTaintAnalyzer");
		localSymSpace.logInfoSymbolSpace();
		return null;
	}
}
