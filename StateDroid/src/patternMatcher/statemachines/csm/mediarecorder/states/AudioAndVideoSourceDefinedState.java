package patternMatcher.statemachines.csm.mediarecorder.states;
import models.cfg.InstructionResponse;
import models.cfg.Register;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;
import patternMatcher.events.csm.mediaRecorder.MediaSetOutputFileEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.csm.mediarecorder.MediaRecorderStates;
import taintanalyzer.TaintAnalyzer;
import configuration.Config;


public class AudioAndVideoSourceDefinedState extends MediaRecorderStates{

	TaintAnalyzer ta;
	private SymbolSpace localSymSpace = Config.getInstance().getLocalSymbolSpace();
	
	public AudioAndVideoSourceDefinedState(TaintAnalyzer taParam){
		this.ta = taParam;
	}
	
	public AudioAndVideoSourceDefinedState(){}

	
	@Override
	public State update(MediaSetOutputFileEvent e) {
		InstructionResponse ir = (InstructionResponse) e.getEventInfo().get(InstructionResponse.CLASS_NAME);
		Register recorderReg = ir.getInvolvedRegisters().get(0);
		SymbolTableEntry recorderEntry = this.localSymSpace.find(recorderReg.getName());
			
		if(recorderEntry != null){
			State currState = recorderEntry.getEntryDetails().getState();
			if(currState != null && currState instanceof AudioAndVideoSourceDefinedState){
				State state = new VideoDataSourceConfiguredState(this.ta);
				recorderEntry.getEntryDetails().setState(state);
			}
		}
		return this;  //This does not matter anymore. Each object maintains its state itself.
	}
}
