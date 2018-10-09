
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

public class SetVideoEncoderAnalyzer extends BaseTaintAnalyzer{

	private InstructionResponse ir;
	private Instruction instr;
	private TaintAnalyzer ta;
	private SymbolSpace localSymSpace;

	public SetVideoEncoderAnalyzer(TaintAnalyzer ta)
	{
		this.ta = ta;
		ir = ta.getIr();
		instr = ta.getIr().getInstr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();		
		logger = Logger.getLogger(SetVideoEncoderAnalyzer.class);
	}

//	0x24 invoke-virtual v2, v6, Landroid/media/MediaRecorder;->setVideoEncoder(I)V
	//This does not return anything. So v2 should be adjusted only.

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
        		SymbolTableEntry videoEncoderEntry=null;
        		
        		//Since input is of type Integer which uses copy by value, we need deep copy here.
 			    videoEncoderEntry = new SymbolTableEntry(inputParamEntry); 
				   
	       		String value = videoEncoderEntry.getEntryDetails().getValue();
        		String videoEncoderName="";
        		if(value != null)
        		{
        			Hashtable videoEncoderConsts = Constants.getInstance().getVideoEncoderConstants();
        			
        			if(videoEncoderConsts.containsKey(value))
        			{
        				videoEncoderName = (String) videoEncoderConsts.get(value);
        			}
        			videoEncoderEntry.getEntryDetails().setValue(videoEncoderName);
        		}

        		if(videoEncoderEntry != null)
        		{
        			videoEncoderEntry.setName(MediaRecorderFields.video_encoder.toString()); 
        			
        			if(fieldList == null)
        				fieldList = new Hashtable();
        			
        			fieldList.put(MediaRecorderFields.video_encoder.toString(), videoEncoderEntry);
        			
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
            logger.debug("\n <SetVideoEncoderAnalyzer>");
        	return callerEntry;
       }
  
       logger.debug("\n <SetVideoEncoderAnalyzer>");
//	       localSymSpace.printSymbolSpace();
       return null;
		
	}
}
