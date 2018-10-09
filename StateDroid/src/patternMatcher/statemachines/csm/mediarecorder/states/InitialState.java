package patternMatcher.statemachines.csm.mediarecorder.states;
import java.util.Hashtable;

import configuration.Config;

import models.cfg.InstructionResponse;
import models.cfg.Register;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;

import patternMatcher.events.csm.mediaRecorder.MediaSetAudioSourceEvent;
import patternMatcher.events.csm.mediaRecorder.MediaSetVideoSourceEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.csm.intent.states.IntentActionCallUriTellDefinedState;
import patternMatcher.statemachines.csm.mediarecorder.MediaRecorderStates;
import patternMatcher.statemachines.csm.uri.states.UriTelDefinedState;
import taintanalyzer.TaintAnalyzer;


public class InitialState extends MediaRecorderStates{

	TaintAnalyzer ta;
	private SymbolSpace localSymSpace = Config.getInstance().getLocalSymbolSpace();
	
	public InitialState(TaintAnalyzer taParam)
	{
		this.ta = taParam;
		
	}
	// InitialState => AudioSourceDefinedState => DataSourceConfiguredState => PreparedState
	// InitialState => AudioSourceDefinedState => AudioAndVideoSourceDefinedState => VideoDataSourceConfiguredState => VideoPreparedState
	// InitialState => VideoSourceDefinedState => AudioAndVideoSourceDefinedState => VideoDataSourceConfiguredState => VideoPreparedState
	public InitialState()
	{
		
	}

	@Override
	public State update(MediaSetAudioSourceEvent e) {
		InstructionResponse ir = (InstructionResponse) e.getEventInfo().get(InstructionResponse.CLASS_NAME);
		Register recorderReg = ir.getInvolvedRegisters().get(0);
		SymbolTableEntry recorderEntry = this.localSymSpace.find(recorderReg.getName());
		
		if(recorderEntry != null){
			AudioSourceDefinedState state = new AudioSourceDefinedState(this.ta);
			recorderEntry.getEntryDetails().setState(state);
		}
		return this;
	}
	
	@Override
	public State update(MediaSetVideoSourceEvent e) {
		InstructionResponse ir = (InstructionResponse) e.getEventInfo().get(InstructionResponse.CLASS_NAME);
		Register recorderReg = ir.getInvolvedRegisters().get(0);
		SymbolTableEntry recorderEntry = this.localSymSpace.find(recorderReg.getName());
		
		if(recorderEntry != null){
			VideoSourceDefinedState state = new VideoSourceDefinedState(this.ta);
			recorderEntry.getEntryDetails().setState(state);
		}
		return this;
	}


}
