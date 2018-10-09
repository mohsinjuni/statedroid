package patternMatcher.statemachines.csm.mediarecorder.states;
import java.util.Hashtable;
import java.util.Iterator;

import models.cfg.InstructionResponse;
import models.cfg.Register;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;
import patternMatcher.events.Event;
import patternMatcher.events.EventFactory;
import patternMatcher.events.asm.audiovideo.AudioRecorderASMEvent;
import patternMatcher.events.csm.mediaRecorder.MediaStartEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.csm.mediarecorder.MediaRecorderStates;
import taintanalyzer.TaintAnalyzer;
import configuration.Config;


public class PreparedState extends MediaRecorderStates{

	TaintAnalyzer ta;
	private SymbolSpace localSymSpace = Config.getInstance().getLocalSymbolSpace();
	
	public PreparedState(TaintAnalyzer taParam)
	{
		this.ta = taParam;
	}
	
	public PreparedState(){}

	@Override
	public State update(MediaStartEvent e) {
		InstructionResponse ir = (InstructionResponse) e.getEventInfo().get(InstructionResponse.CLASS_NAME);
		Register recorderReg = ir.getInvolvedRegisters().get(0);
		SymbolTableEntry recorderEntry = this.localSymSpace.find(recorderReg.getName());
			
		if(recorderEntry != null){
			EventFactory.getInstance().registerEvent("audioRecorderASMEvent", new AudioRecorderASMEvent());
			Event audioRecorderEvent = EventFactory.getInstance().createEvent("audioRecorderASMEvent");
			audioRecorderEvent.setName("audioRecorderASMEvent");
			
			audioRecorderEvent.setCurrComponentName(e.getCurrComponentName());
			audioRecorderEvent.setCurrPkgClsName(e.getCurrPkgClsName());
			audioRecorderEvent.setCurrMethodName(e.getCurrMethodName());
			audioRecorderEvent.setCurrComponentPkgName(e.getCurrComponentPkgName());
			audioRecorderEvent.setCurrCompCallbackMethodName(e.getCurrCompCallbackMethodName());
			
			Hashtable attackParams = getAttackParameters(recorderEntry);
			audioRecorderEvent.getEventInfo().put("attackParameters", attackParams);
			audioRecorderEvent.getEventInfo().put(InstructionResponse.CLASS_NAME,
					ir);
			
			ta.setCurrASMEvent(audioRecorderEvent);
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
