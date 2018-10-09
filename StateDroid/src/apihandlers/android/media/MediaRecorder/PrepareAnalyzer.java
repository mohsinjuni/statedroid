
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
import patternMatcher.events.csm.mediaRecorder.MediaSetAudioSourceEvent;

import taintanalyzer.TaintAnalyzer;
import taintanalyzer.instranalyzers.BaseTaintAnalyzer;
import configuration.Config;
import enums.Constants;

public class PrepareAnalyzer extends BaseTaintAnalyzer{

	private InstructionResponse ir;
	private Instruction instr;
	private TaintAnalyzer ta;
	private SymbolSpace localSymSpace;
	private final String fieldName = "";

	public PrepareAnalyzer(TaintAnalyzer ta)
	{
		this.ta = ta;
		ir = ta.getIr();
		instr = ta.getIr().getInstr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();		
		logger = Logger.getLogger(PrepareAnalyzer.class);
	}

//	0x4e invoke-virtual v2, Landroid/media/MediaRecorder;->prepare()V

//  This class does not do anything. In the future, we might need this. Or may be, we can set flags that this has been called.
	public Object analyzeInstruction()
	{
		Register reg1 = ir.getInvolvedRegisters().get(0);  //v2
		
        SymbolTableEntry callerEntry = localSymSpace.find(reg1.getName());

        if(callerEntry != null)
        {
        			
	  		EventFactory.getInstance().registerEvent("mediaPrepareEvent", new MediaPrepareEvent());
			
			//First, crate event and set it in TaintAnalyzer. Then, do what we do for each source API i.e. return an entry marked as tainted.
			Event mediaPrepareEvent = EventFactory.getInstance().createEvent("mediaPrepareEvent");
			mediaPrepareEvent.setName("mediaPrepareEvent");
		
			Hashtable<String, Object> eventInfo = mediaPrepareEvent.getEventInfo(); 
			
			mediaPrepareEvent.setCurrMethodName(instr.getCurrMethodName());
			mediaPrepareEvent.setCurrPkgClsName(instr.getCurrPkgClassName());
	   		mediaPrepareEvent.setCurrCompCallbackMethodName(ta.getCurrComponentCallback());
			mediaPrepareEvent.setCurrComponentName(ta.getCurrComponentName());
			mediaPrepareEvent.setCurrComponentPkgName(ta.getCurrComponentPkgName());

			eventInfo.put("mediaRecorder", callerEntry); 
			eventInfo.put(InstructionResponse.CLASS_NAME, ir); 
			mediaPrepareEvent.setEventInfo(eventInfo);
			
			mediaPrepareEvent.setEventInfo(eventInfo);
			
			ta.setCurrCSMEvent(mediaPrepareEvent);

       }
       logger.debug("\n <SetAudioSourceAnalyzer>");
//	       localSymSpace.printSymbolSpace();
       return null;
		
	}
}
