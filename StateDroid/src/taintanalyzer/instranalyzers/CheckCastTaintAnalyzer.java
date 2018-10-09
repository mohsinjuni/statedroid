package taintanalyzer.instranalyzers;

import java.util.ArrayList;

import models.cfg.InstructionResponse;
import models.cfg.Register;
import models.symboltable.EntryDetails;
import models.symboltable.SourceInfo;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;

import org.apache.log4j.Logger;

import configuration.Config;

import taintanalyzer.TaintAnalyzer;

public class CheckCastTaintAnalyzer extends BaseTaintAnalyzer {

	private InstructionResponse ir;
	private SymbolSpace localSymSpace;
	boolean tainted = false;
	String[] used;
	String changed;
	ArrayList<Register> involvedRegisters;

	public CheckCastTaintAnalyzer(TaintAnalyzer ta) {
		this.ir = ta.getIr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();
		logger = Logger.getLogger(CheckCastTaintAnalyzer.class);
	}

	// 	0x26 check-cast v7, Landroid/telephony/TelephonyManager;
	// It should always create a new entry and replace the existing one with the same key.

	// 0x64 check-cast v6, Ljava/net/HttpURLConnection; 

	//Just change type of the variable, nothing else.
	public Object analyzeInstruction() {
		Register destReg = ir.getInvolvedRegisters().get(0);

		SymbolTableEntry entry = localSymSpace.find(destReg.getName());

		if (entry != null) {
			entry.getEntryDetails().setType(destReg.getType());
		} else {
			entry = new SymbolTableEntry();
			EntryDetails entryDetails = entry.getEntryDetails();

			entry = new SymbolTableEntry();

			entry.setLineNumber(ir.getLineNumber());
			entry.setName(destReg.getName());
			entry.setInstrInfo("");

			entryDetails.setType(destReg.getType());
			entryDetails.setConstant(false);
			entryDetails.setField(false);
			entryDetails.setRecord(false);
			entryDetails.setTainted(false);
			entryDetails.setValue(destReg.getType());
			entry.setEntryDetails(entryDetails);

			localSymSpace.addEntry(entry);
		}

		logger.debug("\n CheckCastTaintAnalyzer");

		localSymSpace.logInfoSymbolSpace();

		return null;
	}
}
