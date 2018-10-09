
package apihandlers.android.media.AudioManager;

import java.util.Hashtable;

import models.cfg.Instruction;
import models.cfg.InstructionResponse;
import models.cfg.Register;
import models.manifest.AndroidManifest;
import models.manifest.ComponentManifest;
import models.symboltable.SourceInfo;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;

import org.apache.log4j.Logger;

import patternMatcher.events.Event;
import patternMatcher.events.EventFactory;
import patternMatcher.events.asm.phonevolume.VoiceCallStreamDecreasingASMEvent;
import patternMatcher.events.asm.phonevolume.VoiceCallStreamIncreasingASMEvent;
import patternMatcher.events.csm.CreateFromPduEvent;
import patternMatcher.events.csm.audiomanager.SetStreamVolumeEvent;


import taintanalyzer.TaintAnalyzer;
import taintanalyzer.instranalyzers.BaseTaintAnalyzer;
import configuration.Config;

public class SetStreamVolumeAnalyzer extends BaseTaintAnalyzer{

	private InstructionResponse ir;
	private Instruction instr;
	private TaintAnalyzer ta;
	private SymbolSpace localSymSpace;
	private final String fieldName = "output_format";

	/*	audiomanager.setStreamVolume(   AudioManager.STREAM_VOICE_CALL,  AudioManager.ADJUST_LOWER,  0); --decreasing/reduced to 0.
    
    	0x166 const/4 v14, 0
		0x168 const/4 v15, -1
		0x16a const/16 v16, 8
		0x16e move/from16 v0, v16
	//	0x172 invoke-virtual v3, v14, v15, v0, Landroid/media/AudioManager;->setStreamVolume(I I I)V
	 * 
	 * audiomanager.setStreamVolume(           --increasing volume, infact it sets to the maximum
			    AudioManager.STREAM_VOICE_CALL,
			    audiomanager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL),
			    0);
			    
		0x1d8 const/4 v14, 0
		0x1da const/4 v15, 0
		0x1dc invoke-virtual v3, v15, Landroid/media/AudioManager;->getStreamMaxVolume(I)I	    
		0x1e2 move-result v15
		0x1e4 const/16 v16, 0
		0x1e8 move/from16 v0, v16
		0x1ec invoke-virtual v3, v14, v15, v0, Landroid/media/AudioManager;->setStreamVolume(I I I)V	    
	
	*/
//	0x1ec invoke-virtual v3, v14, v15, v0, Landroid/media/AudioManager;->setStreamVolume(I I I)V	   	

	public SetStreamVolumeAnalyzer(TaintAnalyzer ta)
	{
		ir = ta.getIr();
		instr = ta.getIr().getInstr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();		
		logger = Logger.getLogger(SetStreamVolumeAnalyzer.class);
		this.ta = ta;
	}

	public Object analyzeInstruction()
	{
		//Based on input, create the relevant event.

		Register reg1 = ir.getInvolvedRegisters().get(0);  //v3
		Register reg2 = ir.getInvolvedRegisters().get(1);  //v14
		Register reg3 = ir.getInvolvedRegisters().get(2);  //v15
		Register reg4 = ir.getInvolvedRegisters().get(3);  //v0
		
        SymbolTableEntry callerEntry = localSymSpace.find(reg1.getName());
        SymbolTableEntry param1Entry = localSymSpace.find(reg2.getName());
        SymbolTableEntry param2Entry = localSymSpace.find(reg3.getName()); //This reg sets -1 or 5 value.
        SymbolTableEntry param3Entry = localSymSpace.find(reg4.getName());
        
        
  		EventFactory.getInstance().registerEvent("SetStreamVolumeEvent", new SetStreamVolumeEvent());
		
		//First, crate event and set it in TaintAnalyzer. Then, do what we do for each source API i.e. return an entry marked as tainted.
		Event event = EventFactory.getInstance().createEvent("SetStreamVolumeEvent");
		event.setName("SetStreamVolumeEvent");
	
		Hashtable<String, Object> eventInfo = event.getEventInfo(); 
		
		if(callerEntry != null)
		{
	    	Hashtable recordFieldList = callerEntry.getEntryDetails().getRecordFieldList();
	    	
	    	if(recordFieldList == null)
	    		recordFieldList = new Hashtable();
	    	
	    	if(param1Entry != null)
	    	{
 		    	SymbolTableEntry streamTypeEntry = new SymbolTableEntry(param1Entry); //deep copy
		    	streamTypeEntry.setName("streamType");
		    	recordFieldList.put("streamType", streamTypeEntry);
	    	}
	    	
	    	if(param2Entry != null)
	    	{
		    	SymbolTableEntry streamOperationEntry = new SymbolTableEntry(param2Entry); //deep copy
		    	streamOperationEntry.setName("streamOperation");
		    	recordFieldList.put("streamOperation", streamOperationEntry);
	    	}	    
	    	
	    	if(param3Entry != null)
	    	{
		    	SymbolTableEntry flagEntry = new SymbolTableEntry(param3Entry); //deep copy
		    	flagEntry.setName("flag");
		    	recordFieldList.put("flag", flagEntry);
	    	}
	    	callerEntry.getEntryDetails().setRecordFieldList(recordFieldList);
	    	eventInfo.put(InstructionResponse.CLASS_NAME, ir);
			event.setEventInfo(eventInfo);
			
			ta.setCurrCSMEvent(event);
	    	
	        logger.debug("\n <SetStreamVolumeEvent>");
	    	return callerEntry;
       }
       logger.debug("\n <SetStreamVolume-Analyzer>");
       return null;
		
	}
}
