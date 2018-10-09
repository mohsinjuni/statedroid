
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
import patternMatcher.events.csm.SetRingerModeEvent;
import patternMatcher.events.csm.mediaRecorder.MediaSetAudioSourceEvent;

import taintanalyzer.TaintAnalyzer;
import taintanalyzer.instranalyzers.BaseTaintAnalyzer;
import configuration.Config;
import enums.Constants;
import enums.Constants.MediaRecorderFields;

public class SetAudioSourceAnalyzer extends BaseTaintAnalyzer{

	private InstructionResponse ir;
	private Instruction instr;
	private TaintAnalyzer ta;
	private SymbolSpace localSymSpace;

	public SetAudioSourceAnalyzer(TaintAnalyzer ta)
	{
		this.ta = ta;
		ir = ta.getIr();
		instr = ta.getIr().getInstr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();
		logger = Logger.getLogger(SetAudioSourceAnalyzer.class);
	}

//	0x24 invoke-virtual v2, v6, Landroid/media/MediaRecorder;->setAudioSource(I)V

// public void setAudioSource (int audio_source)	
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
        		SymbolTableEntry audioSourceEntry=null;

        		audioSourceEntry = new SymbolTableEntry(inputParamEntry); //Needs deep copy
				   
	       		String value = audioSourceEntry.getEntryDetails().getValue();
        		String audioSource="";
        		if(value != null && !value.isEmpty())
        		{
        			Hashtable audioConsts = Constants.getInstance().getAudioSourceConstants();
        			
        			if(audioConsts.containsKey(value))
        			{
        				audioSource = (String) audioConsts.get(value);
        			}
        			audioSourceEntry.getEntryDetails().setValue(audioSource);
        		}
      			audioSourceEntry.setName(MediaRecorderFields.audio_source.toString()); // We dont need to rename either but I am doing for the sake of it.
    			// rest of the data for this entry be the same.
       			if(fieldList == null)
    				fieldList = new Hashtable();
       			
    			fieldList.put(MediaRecorderFields.audio_source.toString(), audioSourceEntry);
    			
    			callerEntry.getEntryDetails().setRecordFieldList(fieldList);
    			callerEntry.getEntryDetails().setRecord(true);
    	  		EventFactory.getInstance().registerEvent("mediaSetAudioSourceEvent", new MediaSetAudioSourceEvent());
    			
    			Event mediaSetAudioSourceEvent = EventFactory.getInstance().createEvent("mediaSetAudioSourceEvent");
    			mediaSetAudioSourceEvent.setName("mediaSetAudioSourceEvent");
    		
    			Hashtable<String, Object> eventInfo = mediaSetAudioSourceEvent.getEventInfo(); 
    			eventInfo.put(InstructionResponse.CLASS_NAME, ir); 
    			mediaSetAudioSourceEvent.setEventInfo(eventInfo);
    			
    			ta.setCurrCSMEvent(mediaSetAudioSourceEvent);
        	}
            logger.debug("\n <SetAudioSourceAnalyzer>");
        	return callerEntry;
       }
       logger.debug("\n <SetAudioSourceAnalyzer>");
//	       localSymSpace.printSymbolSpace();
       return null;
		
	}
}
