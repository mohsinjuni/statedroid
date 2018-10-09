
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
import patternMatcher.events.csm.SetRingerModeEvent;
import patternMatcher.events.csm.audiomanager.SetStreamVolumeEvent;


import taintanalyzer.TaintAnalyzer;
import taintanalyzer.instranalyzers.BaseTaintAnalyzer;
import configuration.Config;

public class SetRingerModeAnalyzer extends BaseTaintAnalyzer{

	private InstructionResponse ir;
	private Instruction instr;
	private TaintAnalyzer ta;
	private SymbolSpace localSymSpace;
	private final String fieldName = "output_format";

	/*	audiomanager.setStreamVolume(   AudioManager.STREAM_VOICE_CALL,  AudioManager.ADJUST_LOWER,  0); --decreasing/reduced to 0.
    
 		0x1a2 const/16 v28, 0
		0x1a6 move/from16 v0, v28
		0x1aa invoke-virtual v6, v0, Landroid/media/AudioManager;->setRingerMode(I)V    
	
		0 turns it to the totally silent mode
		2 returns to the mode where it was earlier before the totally-silent mode.
	*/

	public SetRingerModeAnalyzer(TaintAnalyzer ta)
	{
		ir = ta.getIr();
		instr = ta.getIr().getInstr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();		
		logger = Logger.getLogger(SetRingerModeAnalyzer.class);
		this.ta = ta;
	}

	public Object analyzeInstruction(){
		
		//Based on input, create the relevant event.
		Register reg1 = ir.getInvolvedRegisters().get(0);  
		Register reg2 = ir.getInvolvedRegisters().get(1);  
		
        SymbolTableEntry callerEntry = localSymSpace.find(reg1.getName());
        SymbolTableEntry param1Entry = localSymSpace.find(reg2.getName());
        
        if(callerEntry != null){
        	Hashtable recordFieldList = callerEntry.getEntryDetails().getRecordFieldList();

        	if(param1Entry != null){
	        	if(recordFieldList == null){
	        		recordFieldList = new Hashtable();
	        	}
	        	SymbolTableEntry ringerModeEntry = new SymbolTableEntry(param1Entry); //deep copy
	        	ringerModeEntry.setName("ringerModeEntry");
	        	recordFieldList.put(ringerModeEntry.getName(), ringerModeEntry);
	        	callerEntry.getEntryDetails().setRecordFieldList(recordFieldList);
	        	
        	}
	  		EventFactory.getInstance().registerEvent("setRingerModeEvent", new SetRingerModeEvent());
			Event setRingerModeEvent = EventFactory.getInstance().createEvent("setRingerModeEvent");
			setRingerModeEvent.setName("setRingerModeEvent");
		
			Hashtable<String, Object> eventInfo = setRingerModeEvent.getEventInfo(); 
			
			setRingerModeEvent.setCurrMethodName(instr.getCurrMethodName());
			setRingerModeEvent.setCurrPkgClsName(instr.getCurrPkgClassName());
	   		setRingerModeEvent.setCurrCompCallbackMethodName(ta.getCurrComponentCallback());
			setRingerModeEvent.setCurrComponentName(ta.getCurrComponentName());
			setRingerModeEvent.setCurrComponentPkgName(ta.getCurrComponentPkgName());
	
			eventInfo.put(InstructionResponse.CLASS_NAME, ir);
			
			setRingerModeEvent.setEventInfo(eventInfo);
			
			ta.setCurrCSMEvent(setRingerModeEvent);
        }
		
       logger.debug("\n <SetRingerModeAnalyzer>");
       return null;
		
	}
}
