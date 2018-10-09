package patternMatcher.statemachines.csm.mediarecorder.states;
import java.util.Hashtable;

import configuration.Config;

import models.cfg.InstructionResponse;
import models.cfg.Register;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;

import patternMatcher.events.Event;
import patternMatcher.events.EventFactory;
import patternMatcher.events.asm.phonevolume.VoiceCallStreamDecreasingASMEvent;
import patternMatcher.events.asm.phonevolume.VoiceCallStreamIncreasingASMEvent;
import patternMatcher.events.csm.mediaRecorder.MediaSetAudioSourceEvent;
import patternMatcher.events.csm.mediaRecorder.MediaSetOutputFormatEvent;
import patternMatcher.events.csm.mediaRecorder.MediaSetVideoSourceEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.csm.mediarecorder.MediaRecorderStates;
import patternMatcher.statemachines.csm.uri.states.UriTelDefinedState;
import taintanalyzer.TaintAnalyzer;


public class VideoSourceDefinedState extends MediaRecorderStates{

	TaintAnalyzer ta;
	private SymbolSpace localSymSpace = Config.getInstance().getLocalSymbolSpace();
	
	public VideoSourceDefinedState(TaintAnalyzer taParam){
		this.ta = taParam;
	}
	
	public VideoSourceDefinedState(){}

	@Override
	public State update(MediaSetAudioSourceEvent e) {
		InstructionResponse ir = (InstructionResponse) e.getEventInfo().get(InstructionResponse.CLASS_NAME);
		Register recorderReg = ir.getInvolvedRegisters().get(0);
		SymbolTableEntry recorderEntry = this.localSymSpace.find(recorderReg.getName());
		
		if(recorderEntry != null){
			State currState = recorderEntry.getEntryDetails().getState();
			if(currState != null && currState instanceof VideoSourceDefinedState){
				AudioAndVideoSourceDefinedState state = new AudioAndVideoSourceDefinedState(this.ta);
				recorderEntry.getEntryDetails().setState(state);
			}
		}
		return this;
	}
}
