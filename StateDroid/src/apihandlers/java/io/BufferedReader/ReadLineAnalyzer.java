
package apihandlers.java.io.BufferedReader;

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
import patternMatcher.events.csm.filereading.BufferedReaderDefinedEvent;
import patternMatcher.events.csm.filereading.BufferedReaderReadDataEvent;

import taintanalyzer.TaintAnalyzer;
import taintanalyzer.instranalyzers.BaseTaintAnalyzer;
import configuration.Config;

public class ReadLineAnalyzer extends BaseTaintAnalyzer{

	private InstructionResponse ir;
	private Instruction instr;
	private TaintAnalyzer ta;
	private SymbolSpace localSymSpace;

	public ReadLineAnalyzer(TaintAnalyzer ta){
		ir = ta.getIr();
		this.ta = ta;
		instr = ta.getIr().getInstr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();
		logger = Logger.getLogger(ReadLineAnalyzer.class);
	}
//		0x1c invoke-virtual v6, Ljava/io/BufferedReader;->readLine()Ljava/lang/String;
//		0x22 move-result-object v10
	public Object analyzeInstruction(){
		
		Register reg1 = ir.getInvolvedRegisters().get(0);  //v6
        SymbolTableEntry brEntry = localSymSpace.find(reg1.getName());
        
        if(brEntry != null){
        	
        	SymbolTableEntry returnEntry = new SymbolTableEntry();
        	returnEntry.getEntryDetails().setType(ir.getReturnType());
        	returnEntry.setLineNumber(ir.getLineNumber());
        	returnEntry.setInstrInfo(ir.getInstr().getText());

        	Hashtable recordFieldList = new Hashtable();
			recordFieldList.put("callerEntry", brEntry);
			returnEntry.getEntryDetails().setRecordFieldList(recordFieldList);
        	
        	//Generate event and send it via InstructionReturnValue object.
    		EventFactory.getInstance().registerEvent("bufferedReaderReadDataEvent", new BufferedReaderReadDataEvent());
			Event event = EventFactory.getInstance().createEvent("bufferedReaderReadDataEvent");
			event.setName("bufferedReaderReadDataEvent");
			
			Hashtable eventInfo = event.getEventInfo();
			eventInfo.put(InstructionResponse.CLASS_NAME, ir);
			event.setEventInfo(eventInfo);
			InstructionReturnValue returValue = new InstructionReturnValue(returnEntry, event);
			return returValue;
      	}
        
     return null;
	}
}
