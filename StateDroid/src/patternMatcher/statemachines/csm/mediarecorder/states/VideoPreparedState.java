package patternMatcher.statemachines.csm.mediarecorder.states;
import java.util.Hashtable;
import java.util.Iterator;

import models.cfg.InstructionResponse;
import models.cfg.Register;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;
import patternMatcher.events.Event;
import patternMatcher.events.EventFactory;
import patternMatcher.events.asm.audiovideo.VideoRecorderASMEvent;
import patternMatcher.events.csm.mediaRecorder.MediaStartEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.csm.mediarecorder.MediaRecorderStates;
import taintanalyzer.TaintAnalyzer;
import configuration.Config;


public class VideoPreparedState extends MediaRecorderStates{

	TaintAnalyzer ta;
	private SymbolSpace localSymSpace = Config.getInstance().getLocalSymbolSpace();
	
	public VideoPreparedState(TaintAnalyzer taParam)
	{
		this.ta = taParam;
	}
	
	public VideoPreparedState(){}

	@Override
	public State update(MediaStartEvent e) {
		InstructionResponse ir = (InstructionResponse) e.getEventInfo().get(InstructionResponse.CLASS_NAME);
		Register recorderReg = ir.getInvolvedRegisters().get(0);
		SymbolTableEntry recorderEntry = this.localSymSpace.find(recorderReg.getName());
			
		if(recorderEntry != null){
			EventFactory.getInstance().registerEvent("videoRecorderASMEvent", new VideoRecorderASMEvent());
			Event audioVideoRecorderEvent = EventFactory.getInstance().createEvent("videoRecorderASMEvent");
			audioVideoRecorderEvent.setName("videoRecorderASMEvent");
			
			audioVideoRecorderEvent.setCurrComponentName(e.getCurrComponentName());
			audioVideoRecorderEvent.setCurrPkgClsName(e.getCurrPkgClsName());
			audioVideoRecorderEvent.setCurrMethodName(e.getCurrMethodName());
			audioVideoRecorderEvent.setCurrComponentPkgName(e.getCurrComponentPkgName());
			audioVideoRecorderEvent.setCurrCompCallbackMethodName(e.getCurrCompCallbackMethodName());
			
			Hashtable attackParams = getAttackParameters(recorderEntry);
			audioVideoRecorderEvent.getEventInfo().put("attackParameters", attackParams);
			audioVideoRecorderEvent.getEventInfo().put(InstructionResponse.CLASS_NAME,
					ir);
			
			ta.setCurrASMEvent(audioVideoRecorderEvent);
		}
		return this;
	}
	
	private Hashtable getAttackParameters(SymbolTableEntry entry){
		Hashtable attackParameters = new Hashtable();
		
		Hashtable recordFieldList = entry.getEntryDetails().getRecordFieldList();
		if(recordFieldList == null || recordFieldList.size() < 1){
			return attackParameters;
		}
		
		Iterator itr = recordFieldList.keySet().iterator();
		while(itr.hasNext()){
			String key = (String) itr.next();
			SymbolTableEntry field = (SymbolTableEntry) recordFieldList.get(key);
			if(field != null){
				attackParameters.put(key, field.getEntryDetails().getValue());
			}
		}
		return attackParameters;
	}

}
