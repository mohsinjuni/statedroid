
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

import taintanalyzer.TaintAnalyzer;
import taintanalyzer.instranalyzers.BaseTaintAnalyzer;
import configuration.Config;
import enums.Constants;
import enums.Constants.MediaRecorderFields;

public class SetAudioEncoderAnalyzer extends BaseTaintAnalyzer{

	private InstructionResponse ir;
	private Instruction instr;
	private TaintAnalyzer ta;
	private SymbolSpace localSymSpace;

	public SetAudioEncoderAnalyzer(TaintAnalyzer ta)
	{
		this.ta = ta;
		ir = ta.getIr();
		instr = ta.getIr().getInstr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();		
		logger = Logger.getLogger(SetAudioEncoderAnalyzer.class);
	}

//	0x24 invoke-virtual v2, v6, Landroid/media/MediaRecorder;->setAudioEncoder(I)V
	//This does not return anything. So v2 should be adjusted only.

// setAudioEncoder(int audio_encoder)	
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
        		SymbolTableEntry audioEncoderEntry=null;
        		
        		//Since input is of type Integer which uses copy by value, we need deep copy here.
 			    audioEncoderEntry = new SymbolTableEntry(inputParamEntry); 
				   
	       		String value = audioEncoderEntry.getEntryDetails().getValue();
        		String audioEncoderName="";
        		if(value != null)
        		{
        			Hashtable audioEncoderConsts = Constants.getInstance().getAudioEncoderConstants();
        			
        			if(audioEncoderConsts.containsKey(value))
        			{
        				audioEncoderName = (String) audioEncoderConsts.get(value);
        			}
        			audioEncoderEntry.getEntryDetails().setValue(audioEncoderName);
        		}

        		if(audioEncoderEntry != null)
        		{
        			audioEncoderEntry.setName(MediaRecorderFields.audio_encoder.toString()); 
        			
        			if(fieldList == null)
        				fieldList = new Hashtable();
        			
        			fieldList.put(MediaRecorderFields.audio_encoder.toString(), audioEncoderEntry);
        			
        			callerEntry.getEntryDetails().setRecordFieldList(fieldList);
        			
        			callerEntry.getEntryDetails().setRecord(true);
        			
        			if(inputParamEntry.getEntryDetails().isTainted())
        			{
        				callerEntry.getEntryDetails().setTainted(true); // Though it provides redundant info
        				
        			   	ArrayList<SourceInfo> existingSiList = inputParamEntry.getEntryDetails().getSourceInfoList();
        			   	ArrayList<SourceInfo> siList = callerEntry.getEntryDetails().getSourceInfoList();
        			   	
        			   	if(existingSiList != null && existingSiList.size()> 0)
        			   	{
        			   		if(siList == null)
        			   			siList = new ArrayList<SourceInfo>();
	        				for(SourceInfo si : existingSiList)
	        				{
		        	    		if(!siList.contains(si))
		        	    			siList.add(si);
	        				}
        			   	}
        			   	callerEntry.getEntryDetails().setSourceInfoList(siList);
        			}
        		}
        	}
            logger.debug("\n <SetAudioEncoderAnalyzer>");
        	return callerEntry;
       }
  
       logger.debug("\n <SetAudioEncoderAnalyzer>");
//	       localSymSpace.printSymbolSpace();
       return null;
		
	}
}
