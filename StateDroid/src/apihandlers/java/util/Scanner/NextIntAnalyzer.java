
package apihandlers.java.util.Scanner;

import java.util.ArrayList;
import java.util.Hashtable;

import models.cfg.Instruction;
import models.cfg.InstructionResponse;
import models.cfg.InstructionReturnValue;
import models.cfg.Register;
import models.symboltable.SourceInfo;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;

import org.apache.log4j.Logger;

import patternMatcher.events.Event;
import patternMatcher.events.EventFactory;
import patternMatcher.events.csm.filereading.BufferedReaderReadDataEvent;
import patternMatcher.events.csm.filereading.ScannerDefinedEvent;
import patternMatcher.events.csm.filereading.ScannerReadDataEvent;

import taintanalyzer.TaintAnalyzer;
import taintanalyzer.instranalyzers.BaseTaintAnalyzer;
import configuration.Config;

public class NextIntAnalyzer extends BaseTaintAnalyzer{

	private InstructionResponse ir;
	private Instruction instr;
	private TaintAnalyzer ta;
	private SymbolSpace localSymSpace;

	public NextIntAnalyzer(TaintAnalyzer ta){
		ir = ta.getIr();
		this.ta = ta;
		instr = ta.getIr().getInstr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();
		logger = Logger.getLogger(NextIntAnalyzer.class);
	}
//		0x50 invoke-virtual v7, Ljava/util/Scanner;->nextInt()I
//		0x56 move-result v0
	public Object analyzeInstruction(){
		Register reg1 = ir.getInvolvedRegisters().get(0);  //v7
        SymbolTableEntry scannerEntry = localSymSpace.find(reg1.getName());

        if(scannerEntry != null){
        	SymbolTableEntry returnEntry = new SymbolTableEntry();
        	returnEntry.getEntryDetails().setType(ir.getReturnType());
        	returnEntry.setLineNumber(ir.getLineNumber());
        	returnEntry.setInstrInfo(ir.getInstr().getText());

        	Hashtable recordFieldList = new Hashtable();
			recordFieldList.put("callerEntry", scannerEntry);
			returnEntry.getEntryDetails().setRecordFieldList(recordFieldList);
        	
        	//Generate event and send it via InstructionReturnValue object.
    		EventFactory.getInstance().registerEvent("scannerReadDataEvent", new ScannerReadDataEvent());
			Event event = EventFactory.getInstance().createEvent("scannerReadDataEvent");
			event.setName("scannerReadDataEvent");
			
			Hashtable eventInfo = event.getEventInfo();
			eventInfo.put(InstructionResponse.CLASS_NAME, ir);
			event.setEventInfo(eventInfo);
			InstructionReturnValue returValue = new InstructionReturnValue(returnEntry, event);
			return returValue;
      	}
     return null;
	}
}
