
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
import patternMatcher.events.csm.mediaRecorder.MediaSetVideoSourceEvent;


import taintanalyzer.TaintAnalyzer;
import taintanalyzer.instranalyzers.BaseTaintAnalyzer;
import configuration.Config;
import enums.Constants;
import enums.Constants.MediaRecorderFields;

public class SetVideoFrameRateAnalyzer extends BaseTaintAnalyzer{

	private InstructionResponse ir;
	private Instruction instr;
	private TaintAnalyzer ta;
	private SymbolSpace localSymSpace;

	public SetVideoFrameRateAnalyzer(TaintAnalyzer ta)
	{
		this.ta = ta;
		ir = ta.getIr();
		instr = ta.getIr().getInstr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();		
		logger = Logger.getLogger(SetVideoFrameRateAnalyzer.class);
	}

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
        		SymbolTableEntry videoFrameRateEntry=null;
			    videoFrameRateEntry =  new SymbolTableEntry(inputParamEntry); //deep copy
    
			    if(videoFrameRateEntry != null)
        		{
        			videoFrameRateEntry.getEntryDetails().setValue(inputParamEntry.getEntryDetails().getValue());

        			videoFrameRateEntry.setName(MediaRecorderFields.video_frame_rate.toString()); // We dont need to rename either but I am doing for the sake of it.
        			// rest of the data for this entry be the same.
           			if(fieldList == null)
        				fieldList = new Hashtable();
           			
        			fieldList.put(MediaRecorderFields.video_frame_rate.toString(), videoFrameRateEntry); 
        			callerEntry.getEntryDetails().setRecordFieldList(fieldList);
        			callerEntry.getEntryDetails().setRecord(true);
        		}
        	}
            logger.debug("\n <SetAudioSourceAnalyzer>");
        	return callerEntry;
       }
       logger.debug("\n <SetAudioSourceAnalyzer>");
       return null;
		
	}
}
