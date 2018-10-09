
package apihandlers.android.media.MediaRecorder;

import java.util.Hashtable;

import models.cfg.Instruction;
import models.cfg.InstructionResponse;
import models.cfg.Register;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;

import org.apache.log4j.Logger;

import patternMatcher.events.Event;
import patternMatcher.events.EventFactory;
import patternMatcher.events.csm.mediaRecorder.MediaPrepareEvent;
import patternMatcher.events.csm.mediaRecorder.MediaStartEvent;

import taintanalyzer.TaintAnalyzer;
import taintanalyzer.instranalyzers.BaseTaintAnalyzer;
import configuration.Config;

public class StartAnalyzer extends BaseTaintAnalyzer{

	private InstructionResponse ir;
	private Instruction instr;
	private TaintAnalyzer ta;
	private SymbolSpace localSymSpace;
	private final String fieldName = "";

	public StartAnalyzer(TaintAnalyzer ta)
	{
		this.ta = ta;
		ir = ta.getIr();
		instr = ta.getIr().getInstr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();		
		logger = Logger.getLogger(StartAnalyzer.class);
	}

//	0x54 invoke-virtual v2, Landroid/media/MediaRecorder;->start()V

	// MediaRecorder state diagram is given here.
	// http://developer.android.com/reference/android/media/MediaRecorder.html
	public Object analyzeInstruction()
	{

		Register reg1 = ir.getInvolvedRegisters().get(0);  //v2
		
        SymbolTableEntry mediaRecorderEntry = localSymSpace.find(reg1.getName());

        if(mediaRecorderEntry != null)
        {
        			
	  		EventFactory.getInstance().registerEvent("mediaStartEvent", new MediaStartEvent());
			
			//First, crate event and set it in TaintAnalyzer. Then, do what we do for each source API i.e. return an entry marked as tainted.
			Event mediaStartEvent = EventFactory.getInstance().createEvent("mediaStartEvent");
			mediaStartEvent.setName("mediaStartEvent");
		
			Hashtable<String, Object> eventInfo = mediaStartEvent.getEventInfo(); 
			
			mediaStartEvent.setCurrMethodName(instr.getCurrMethodName());
			mediaStartEvent.setCurrPkgClsName(instr.getCurrPkgClassName());
	   		mediaStartEvent.setCurrCompCallbackMethodName(ta.getCurrComponentCallback());
			mediaStartEvent.setCurrComponentName(ta.getCurrComponentName());
			mediaStartEvent.setCurrComponentPkgName(ta.getCurrComponentPkgName());

			eventInfo.put("mediaRecorder", mediaRecorderEntry); 
			eventInfo.put(InstructionResponse.CLASS_NAME, ir); 
			
			mediaStartEvent.setEventInfo(eventInfo);
			
			ta.setCurrCSMEvent(mediaStartEvent);

       }
       logger.debug("\n <SetAudioSourceAnalyzer>");
       return null;
		
	}
}
