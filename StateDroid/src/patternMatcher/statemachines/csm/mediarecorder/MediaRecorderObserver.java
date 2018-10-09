package patternMatcher.statemachines.csm.mediarecorder;
import java.util.Hashtable;

import models.cfg.InstructionResponse;
import models.cfg.Register;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;
import patternMatcher.AttackObserver;
import patternMatcher.events.Event;
import patternMatcher.events.csm.mediaRecorder.MediaPrepareEvent;
import patternMatcher.events.csm.mediaRecorder.MediaSetAudioSourceEvent;
import patternMatcher.events.csm.mediaRecorder.MediaSetOutputFileEvent;
import patternMatcher.events.csm.mediaRecorder.MediaSetOutputFormatEvent;
import patternMatcher.events.csm.mediaRecorder.MediaSetVideoSourceEvent;
import patternMatcher.events.csm.mediaRecorder.MediaStartEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.csm.mediarecorder.states.InitialState;
import taintanalyzer.TaintAnalyzer;
import configuration.Config;


public class MediaRecorderObserver extends AttackObserver {

	private State state;
	private TaintAnalyzer taSubject;
	private Hashtable attackParameters;
	private SymbolSpace localSymSpace = Config.getInstance().getLocalSymbolSpace();

	public MediaRecorderObserver ()
	{
		this.state = new InitialState();
	}

	public MediaRecorderObserver (TaintAnalyzer taParam)
	{
		this.taSubject = taParam;
		this.attackParameters = new Hashtable();
		this.state = new InitialState(taSubject);
	}

	public void registerObserver (TaintAnalyzer taParam){}

  	@Override
	public void update(MediaSetAudioSourceEvent e) {
  		//Get media-recorder object and set its state to AudioOrVideoSourceDefinedState
  		// 0x24 invoke-virtual v2, v6, Landroid/media/MediaRecorder;->setAudioSource(I)V
    	State currState = getCurrentStateOfMediaRecorder(e);
    	if(currState == null){
        	this.state = new InitialState(this.taSubject);
    	}else{
    		this.state = currState;
    	}
		state = state.update(e);		
	}
  	
	@Override
	public void update(MediaSetVideoSourceEvent e) {
  		//Get media-recorder object and set its state to AudioOrVideoSourceDefinedState
		
		// 0x24 invoke-virtual v2, v6, Landroid/media/MediaRecorder;->setVideoSource(I)V
    	State currState = getCurrentStateOfMediaRecorder(e);
    	if(currState == null){
        	this.state = new InitialState(this.taSubject);
    	}else{
    		this.state = currState;
    	}
		state = state.update(e);		
	}

	@Override
	public void update(MediaSetOutputFormatEvent e) {
		State currState = getCurrentStateOfMediaRecorder(e);
    	if(currState != null){
    		this.state = currState;
    		state = state.update(e);		
    	}
	}
	
	@Override
	public void update(MediaSetOutputFileEvent e) {
		State currState = getCurrentStateOfMediaRecorder(e);
    	if(currState != null){
    		this.state = currState;
    		state = state.update(e);		
    	}
	}

	@Override
	public void update(MediaPrepareEvent e) {
		State currState = getCurrentStateOfMediaRecorder(e);
    	if(currState != null){
    		this.state = currState;
    		state = state.update(e);		
    	}		
	}
	
	@Override
	public void update(MediaStartEvent e) {
		State currState = getCurrentStateOfMediaRecorder(e);
    	if(currState != null){
    		this.state = currState;
    		state = state.update(e);		
    	}		
	}

	public State getCurrentStateOfMediaRecorder(Event e){
		State currState = null;
		Hashtable eventInfo = (Hashtable) e.getEventInfo();
  		InstructionResponse ir = (InstructionResponse) eventInfo.get(InstructionResponse.CLASS_NAME);
  		
  		Register reg1 = ir.getInvolvedRegisters().get(0);  
        SymbolTableEntry recorderEntry = localSymSpace.find(reg1.getName());

        if(recorderEntry != null)
        {
        	currState = recorderEntry.getEntryDetails().getState();
        }
		
		return currState;
	}

	public TaintAnalyzer getTaSubject() {
		return taSubject;
	}

	public void setTaSubject(TaintAnalyzer taSubject) {
		this.taSubject = taSubject;
	}


}
