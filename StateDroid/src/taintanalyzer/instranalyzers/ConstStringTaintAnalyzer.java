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

public class ConstStringTaintAnalyzer extends BaseTaintAnalyzer {

	private InstructionResponse ir;
	private SymbolSpace localSymSpace;
	boolean tainted = false;
	String[] used;
	String changed;

	public ConstStringTaintAnalyzer(TaintAnalyzer ta) {
		this.ir = ta.getIr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();
		logger = Logger.getLogger(ConstStringTaintAnalyzer.class);
	}

	public Object analyzeInstruction() {
		Register destReg = ir.getInvolvedRegisters().get(0);

		String apiInfo = "";

		Instruction instr = ir.getInstr();
		apiInfo = String.valueOf(" [PkgClass] = ").concat(
				instr.getCurrPkgClassName().concat(" , [method] = ").concat(instr.getCurrMethodName()));

		// In all cases, it should create a new entry and replace the existing one in the symbol table.

		SymbolTableEntry entry = new SymbolTableEntry();

		EntryDetails destEntryDetails = entry.getEntryDetails();

		entry = new SymbolTableEntry();

		entry.setName(destReg.getName());
		entry.setInstrInfo(apiInfo);
		entry.setLineNumber(ir.getLineNumber());

		destEntryDetails.setType(destReg.getType());
		destEntryDetails.setTainted(false);
		destEntryDetails.setConstant(true);
		destEntryDetails.setValue(destReg.getValue());
		destEntryDetails.setField(false);
		destEntryDetails.setRecord(false);

		//.setRecordFieldList and setSourceInfo are not necessary.

		entry.setEntryDetails(destEntryDetails);
		localSymSpace.addEntry(entry);

		logger.debug("\n ConstStringTaintAnalyzer");
		localSymSpace.logInfoSymbolSpace();
		return null;
	}
}
