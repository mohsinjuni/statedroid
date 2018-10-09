
package apihandlers.com.google.common.io.Files;

import java.util.Hashtable;

import javax.annotation.processing.FilerException;

import models.cfg.Instruction;
import models.cfg.InstructionResponse;
import models.cfg.InstructionReturnValue;
import models.cfg.Register;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;

import org.apache.log4j.Logger;

import patternMatcher.events.Event;
import patternMatcher.events.EventFactory;
import patternMatcher.events.csm.filereading.FileUtilsReadFileToStringEvent;
import patternMatcher.events.csm.filereading.FilesToStringEvent;

import taintanalyzer.TaintAnalyzer;
import taintanalyzer.instranalyzers.BaseTaintAnalyzer;
import configuration.Config;

public class ToStringAnalyzer extends BaseTaintAnalyzer{

	private InstructionResponse ir;
	private Instruction instr;
	private TaintAnalyzer ta;
	private SymbolSpace localSymSpace;

	public ToStringAnalyzer(TaintAnalyzer ta){
		ir = ta.getIr();
		instr = ta.getIr().getInstr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();
		logger = Logger.getLogger(ToStringAnalyzer.class);
	}
//		0x8 invoke-direct v0, v1, Ljava/io/File;-><init>(Ljava/lang/String;)V
//		0xe sget-object v1, Lcom/google/common/base/Charsets;->UTF_8 Ljava/nio/charset/Charset;
//		0x12 invoke-static v0, v1, Lcom/google/common/io/Files;->toString(Ljava/io/File; Ljava/nio/charset/Charset;)Ljava/lang/String;
//		0x18 move-result-object v3

		public Object analyzeInstruction(){
		
		Register reg1 = ir.getInvolvedRegisters().get(0);  //v0
        SymbolTableEntry inputParamEntry = localSymSpace.find(reg1.getName());
        
        if(inputParamEntry != null){
        	
        	SymbolTableEntry returnEntry = new SymbolTableEntry();
        	returnEntry.getEntryDetails().setType(ir.getReturnType());
        	returnEntry.setLineNumber(ir.getLineNumber());
        	returnEntry.setInstrInfo(ir.getInstr().getText());
        	
        	Hashtable recordFieldList = new Hashtable();

        	SymbolTableEntry callerEntry = new SymbolTableEntry(inputParamEntry); //deeep copy to preserve the data.
        	callerEntry.setName("callerEntry");
        	recordFieldList.put("callerEntry", callerEntry);
        	returnEntry.getEntryDetails().setRecordFieldList(recordFieldList);

    		EventFactory.getInstance().registerEvent("filesToStringEvent", new FilesToStringEvent());
			Event event = EventFactory.getInstance().createEvent("filesToStringEvent");
			event.setName("filesToStringEvent");
			
			Hashtable eventInfo = event.getEventInfo();
			eventInfo.put(InstructionResponse.CLASS_NAME, ir);
			event.setEventInfo(eventInfo);
			InstructionReturnValue returValue = new InstructionReturnValue(returnEntry, event);
			return returValue;
        	
      	}
     return null;
	}
}
