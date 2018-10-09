package patternMatcher.statemachines.csm.mediarecorder.states;
import java.util.Hashtable;

import configuration.Config;

import models.cfg.InstructionResponse;
import models.cfg.Register;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;

import patternMatcher.events.csm.mediaRecorder.MediaPrepareEvent;
import patternMatcher.events.csm.mediaRecorder.MediaPreviewDisplayEvent;
import patternMatcher.events.csm.mediaRecorder.MediaSetCameraEvent;
import patternMatcher.events.csm.mediaRecorder.MediaSetMaxDurationEvent;
import patternMatcher.events.csm.mediaRecorder.MediaSetOutputFileEvent;
import patternMatcher.events.csm.mediaRecorder.MediaSetOutputFormatEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.csm.mediarecorder.MediaRecorderStates;
import taintanalyzer.TaintAnalyzer;


public class VideoDataSourceConfiguredState extends MediaRecorderStates{

	TaintAnalyzer ta;
	private SymbolSpace localSymSpace = Config.getInstance().getLocalSymbolSpace();
	
	public VideoDataSourceConfiguredState(TaintAnalyzer taParam)
	{
		this.ta = taParam;
	}
	
	public VideoDataSourceConfiguredState(){}

	@Override
	public State update(MediaPrepareEvent e) {
		InstructionResponse ir = (InstructionResponse) e.getEventInfo().get(InstructionResponse.CLASS_NAME);
		Register recorderReg = ir.getInvolvedRegisters().get(0);
		SymbolTableEntry recorderEntry = this.localSymSpace.find(recorderReg.getName());
			
		if(recorderEntry != null){
			State currState = recorderEntry.getEntryDetails().getState();
			if(currState != null && currState instanceof VideoDataSourceConfiguredState){
				State state = new VideoPreparedState(this.ta);
				recorderEntry.getEntryDetails().setState(state);
			}
		}
		return this;  //This does not matter anymore. Each object maintains its state itself.
	}

}
