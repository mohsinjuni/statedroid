
package apihandlers.org.apache.commons.io.FileUtils;

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
import patternMatcher.events.csm.filereading.BufferedReaderReadDataEvent;
import patternMatcher.events.csm.filereading.FileUtilsReadFileToStringEvent;
import patternMatcher.events.csm.filereading.FilesToStringEvent;

import taintanalyzer.TaintAnalyzer;
import taintanalyzer.instranalyzers.BaseTaintAnalyzer;
import configuration.Config;

public class ReadFileToStringAnalyzer extends BaseTaintAnalyzer{

	private InstructionResponse ir;
	private Instruction instr;
	private TaintAnalyzer ta;
	private SymbolSpace localSymSpace;

	public ReadFileToStringAnalyzer(TaintAnalyzer ta){
		ir = ta.getIr();
		this.ta = ta;
		instr = ta.getIr().getInstr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();
		logger = Logger.getLogger(ReadFileToStringAnalyzer.class);
	}
//		0x4 const-string v1, 'path/to/your/file.txt'
//		0x8 invoke-direct v0, v1, Ljava/io/File;-><init>(Ljava/lang/String;)V
//		0xe invoke-static v0, Lorg/apache/commons/io/FileUtils;->readFileToString(Ljava/io/File;)Ljava/lang/String;
//		0x14 move-result-object v7

		public Object analyzeInstruction(){
		
		Register reg1 = ir.getInvolvedRegisters().get(0);  //v0
        SymbolTableEntry inputParamEntry = localSymSpace.find(reg1.getName());
//        logger.debug("start AppendAnalyzer");
        
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

    		EventFactory.getInstance().registerEvent("fileUtilsReadFileToStringEvent", new FileUtilsReadFileToStringEvent());
			Event event = EventFactory.getInstance().createEvent("fileUtilsReadFileToStringEvent");
			event.setName("fileUtilsReadFileToStringEvent");
			
			Hashtable eventInfo = event.getEventInfo();
			eventInfo.put(InstructionResponse.CLASS_NAME, ir);
			event.setEventInfo(eventInfo);
			InstructionReturnValue returValue = new InstructionReturnValue(returnEntry, event);
			return returValue;
        	
      	}
     return null;
	}
}
