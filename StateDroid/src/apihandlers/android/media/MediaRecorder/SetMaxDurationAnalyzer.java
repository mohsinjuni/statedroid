
package apihandlers.android.media.MediaRecorder;

import java.util.ArrayList;
import java.util.Hashtable;

import models.cfg.Instruction;
import models.cfg.InstructionResponse;
import models.cfg.Register;
import models.symboltable.SourceInfo;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;

import org.apache.log4j.Logger;

import patternMatcher.events.Event;
import patternMatcher.events.EventFactory;
import patternMatcher.events.csm.mediaRecorder.MediaSetAudioSourceEvent;
import patternMatcher.events.csm.mediaRecorder.MediaSetMaxDurationEvent;

import taintanalyzer.TaintAnalyzer;
import taintanalyzer.instranalyzers.BaseTaintAnalyzer;
import configuration.Config;
import enums.Constants.MediaRecorderFields;

public class SetMaxDurationAnalyzer extends BaseTaintAnalyzer{

	private InstructionResponse ir;
	private Instruction instr;
	private TaintAnalyzer ta;
	private SymbolSpace localSymSpace;
	private final String fieldName = MediaRecorderFields.max_duration_ms.toString();

	public SetMaxDurationAnalyzer(TaintAnalyzer ta)
	{
		this.ta = ta;
		ir = ta.getIr();
		instr = ta.getIr().getInstr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();		
		logger = Logger.getLogger(SetMaxDurationAnalyzer.class);
	}

//	0x24 invoke-virtual v2, v6, Landroid/media/MediaRecorder;->setMaxDuration(I)V

// setMaxDuration(int max_duration_ms)
	public Object analyzeInstruction()
	{

		Register reg1 = ir.getInvolvedRegisters().get(0);  //v2
		Register reg2 = ir.getInvolvedRegisters().get(1);  //v6
		
        SymbolTableEntry callerEntry = localSymSpace.find(reg1.getName());
        SymbolTableEntry inputParamEntry = localSymSpace.find(reg2.getName());
        

        if(callerEntry != null)
        {
        	Hashtable fieldList = (Hashtable) callerEntry.getEntryDetails().getRecordFieldList();
        	
        	if(inputParamEntry != null)
        	{
        		SymbolTableEntry maxDurationEntry=null;
        		
        		maxDurationEntry = new SymbolTableEntry(inputParamEntry); //deep copy
        		
        		if(maxDurationEntry != null)
        		{
        			maxDurationEntry.setName(fieldName); // We dont need to rename either but I am doing for the sake of it.
        			// rest of the data for this entry be the same.
           			if(fieldList == null)
        				fieldList = new Hashtable();
           			
        			fieldList.put(fieldName, maxDurationEntry);
        			callerEntry.getEntryDetails().setRecordFieldList(fieldList);
        			
        			callerEntry.getEntryDetails().setRecord(true);
         		}
        	}
            logger.debug("\n <SetMaxDurationAnalyzer>");
        	return callerEntry;
       }
       logger.debug("\n <SetMaxDurationAnalyzer>");
//	       localSymSpace.printSymbolSpace();
       return null;
		
	}
}
