package taintanalyzer.instranalyzers;

import java.util.Enumeration;
import java.util.Hashtable;

import models.cfg.InstructionResponse;
import models.cfg.Register;
import models.symboltable.EntryDetails;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;

import org.apache.log4j.Logger;

import configuration.Config;

import taintanalyzer.TaintAnalyzer;

public class IgetTaintAnalyzer extends BaseTaintAnalyzer {

	private InstructionResponse ir;
	private SymbolSpace localSymSpace;
	boolean tainted = false;
	String[] used;
	String changed;
	SymbolTableEntry destLocalEntry;
	SymbolTableEntry srcLocalEntry;
	SymbolTableEntry destGlobalEntry;
	SymbolTableEntry srcGlobalEntry;
	Register srcReg;
	Register destReg;

	public IgetTaintAnalyzer(TaintAnalyzer ta) {
		this.ir = ta.getIr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();
		logger = Logger.getLogger(IgetTaintAnalyzer.class);

	}

	// int x = this.index; Any changes to 'x' is/should not reflected in index values, so we need deeeeeep copy (Expensive operation)	
	//	0x0 iget v0, v4, Landroid/support/v4/app/Fragment;->mIndex I
	// It has only copy-by value, not object reference here. So create a new entry totally and set its parameters.	

	public Object analyzeInstruction() {
		String text = ir.getInstr().getText();
		//		if(text.contains("0xe iget v1, v3, Lcom/google/android/service/CallsListenerService;->oldRingerMode I")){
		//			System.out.println("IGET-Object ");
		//		}
		destReg = ir.getInvolvedRegisters().get(0); //v0
		srcReg = ir.getInvolvedRegisters().get(1); //v4

		String objectName = ir.getMethodOrObjectName();

		srcLocalEntry = localSymSpace.find(srcReg.getName(), objectName);

		SymbolTableEntry field = null; //= new SymbolTableEntry();

		if (srcLocalEntry != null) {

			destLocalEntry = new SymbolTableEntry(srcLocalEntry); ///deeeeeeeeeep copy

			destLocalEntry.setName(destReg.getName()); // renaming stuff
			destLocalEntry.setLineNumber(ir.getLineNumber());
			destLocalEntry.setInstrInfo(this.ir.getInstr().getText());

			this.localSymSpace.addEntry(destLocalEntry);

		} else {
			// dummy copy

			destLocalEntry = new SymbolTableEntry();

			EntryDetails destEntryDetails = destLocalEntry.getEntryDetails();

			destLocalEntry.setName(destReg.getName());
			destLocalEntry.setLineNumber(ir.getLineNumber());
			destLocalEntry.setInstrInfo(this.ir.getInstr().getText());

			destEntryDetails.setType(destReg.getType());
			//TODO When we use additional handlers for each iget type, we can then mention speicific type like "I", "Z" etc.

			destEntryDetails.setValue(" ");
			destEntryDetails.setTainted(false);
			destEntryDetails.setConstant(false);
			destEntryDetails.setRecord(false);

			destLocalEntry.setEntryDetails(destEntryDetails);

			this.localSymSpace.addEntry(destLocalEntry);
		}
		logger.debug("\n <<<<<<<<<IgetTaintAnalyzer>>>>>>>>>>>>");
		localSymSpace.logInfoSymbolSpace();
		return null;
	}

}
