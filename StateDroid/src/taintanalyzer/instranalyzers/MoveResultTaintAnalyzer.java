package taintanalyzer.instranalyzers;

import java.util.Hashtable;

import models.cfg.InstructionResponse;
import models.cfg.InstructionReturnValue;
import models.cfg.Register;
import models.symboltable.EntryDetails;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;

import org.apache.log4j.Logger;

import patternMatcher.events.Event;

import configuration.Config;

import taintanalyzer.TaintAnalyzer;

public class MoveResultTaintAnalyzer extends BaseTaintAnalyzer {

	private InstructionResponse ir;
	private SymbolSpace localSymSpace;
	boolean tainted = false;
	String[] used;
	String changed;
	TaintAnalyzer ta;

	public MoveResultTaintAnalyzer(TaintAnalyzer taParam) {
		ta = taParam;
		this.ir = taParam.getIr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();
		logger = Logger.getLogger(MoveResultTaintAnalyzer.class);
	}

	public Object analyzeInstruction() {
		Register destReg = ir.getInvolvedRegisters().get(0);

		SymbolTableEntry entry = null;
		Object obj = ta.getInstrReturnedObject();

		if (obj != null) {
			SymbolTableEntry returnEntry = null;
			if (obj instanceof SymbolTableEntry) {
				returnEntry = (SymbolTableEntry) obj;
				entry = new SymbolTableEntry(returnEntry); //deep copy
				entry.setName(destReg.getName());
				entry.setLineNumber(ir.getLineNumber());
				entry.setInstrInfo(returnEntry.getInstrInfo());
				this.localSymSpace.addEntry(entry);

			} else if (obj instanceof InstructionReturnValue) {
				InstructionReturnValue instReturnObj = (InstructionReturnValue) obj;
				returnEntry = instReturnObj.getReturnEntry();

				entry = new SymbolTableEntry(returnEntry); // deep copy
				entry.setName(destReg.getName());
				entry.setLineNumber(ir.getLineNumber());
				entry.setInstrInfo(returnEntry.getInstrInfo());
				this.localSymSpace.addEntry(entry);

				Event event = instReturnObj.getEvent();
				event.getEventInfo().put("entry", entry);

				//Special case for callerEntry because deep-copy doesnot copy anything from the recordFieldList for now.

				Hashtable recordFieldList = returnEntry.getEntryDetails().getRecordFieldList();
				SymbolTableEntry callerEntry = (SymbolTableEntry) recordFieldList.get("callerEntry");
				event.getEventInfo().put("callerEntry", callerEntry);
				event.getEventInfo().put(InstructionResponse.CLASS_NAME, ir);

				ta.setCurrCSMEvent(event);

			}
		} else {
			//Create a dummy entry
			entry = new SymbolTableEntry();
			entry.setName(destReg.getName());

			EntryDetails entryDetails = entry.getEntryDetails();

			if (ta.getPrev() != null && ta.getPrev().getInstResponse() != null) {
				String type = ta.getPrev().getInstResponse().getReturnType();
				if (type != null)
					entryDetails.setType(type); // Hopefully it's not null.
			} else
				entryDetails.setType("type");

			//Since it is a new entry, other values will be set to default.

			entry.setEntryDetails(entryDetails);

			this.localSymSpace.addEntry(entry);

		}

		logger.debug("\n MoveResultTaintAnalyzer");
		localSymSpace.logInfoSymbolSpace();

		return null;
	}
}
