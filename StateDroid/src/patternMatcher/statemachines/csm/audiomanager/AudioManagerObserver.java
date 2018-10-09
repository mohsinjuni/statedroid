package patternMatcher.statemachines.csm.audiomanager;

import java.util.Hashtable;

import models.cfg.InstructionResponse;
import models.cfg.Register;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;
import patternMatcher.AttackObserver;
import patternMatcher.events.Event;
import patternMatcher.events.csm.SetRingerModeEvent;
import patternMatcher.events.csm.audiomanager.AudioMgrSetVibrateSettingEvent;
import patternMatcher.events.csm.audiomanager.SetStreamVolumeEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.csm.audiomanager.states.InitialState;
import taintanalyzer.TaintAnalyzer;
import configuration.Config;

public class AudioManagerObserver extends AttackObserver {

	private State state;
	private TaintAnalyzer taSubject;
	private Hashtable attackParameters;
	private SymbolSpace localSymSpace = Config.getInstance().getLocalSymbolSpace();

	public AudioManagerObserver() {
	}

	public AudioManagerObserver(TaintAnalyzer taParam) {
		this.taSubject = taParam;
		this.attackParameters = new Hashtable();
	}

	@Override
	public void update(SetStreamVolumeEvent e) {
		//		0x88 invoke-virtual v0, v9, v7, v9, Landroid/media/AudioManager;->setStreamVolume(I I I)V

		State currState = getCurrentStateOfAudioManager(e);
		if (currState != null) {
			this.state = currState;
			state = state.update(e);
		} else {
			this.state = new InitialState(this.taSubject);
			state = state.update(e);
		}
	}

	@Override
	public void update(SetRingerModeEvent e) {
		// 		0x1a2 const/16 v28, 0
		//		0x1a6 move/from16 v0, v28
		//		0x1aa invoke-virtual v6, v0, Landroid/media/AudioManager;->setRingerMode(I)V    

		State currState = getCurrentStateOfAudioManager(e);
		if (currState != null) {
			this.state = currState;
			state = state.update(e);
		} else {
			this.state = new InitialState(this.taSubject);
			state = state.update(e);
		}
	}

	@Override
	public void update(AudioMgrSetVibrateSettingEvent e) {
		// 		0x1a2 const/16 v28, 0
		//		0x1a6 move/from16 v0, v28
		//		0x1aa invoke-virtual v6, v0, Landroid/media/AudioManager;->setRingerMode(I)V    

		State currState = getCurrentStateOfAudioManager(e);
		if (currState != null) {
			this.state = currState;
			state = state.update(e);
		} else {
			this.state = new InitialState(this.taSubject);
			state = state.update(e);
		}
	}

	public State getCurrentStateOfAudioManager(Event e) {
		//		0x88 invoke-virtual v0, v9, v7, v9, Landroid/media/AudioManager;->setStreamVolume(I I I)V
		State currState = null;
		Hashtable eventInfo = (Hashtable) e.getEventInfo();
		InstructionResponse ir = (InstructionResponse) eventInfo.get(InstructionResponse.CLASS_NAME);

		Register reg1 = ir.getInvolvedRegisters().get(0);
		SymbolTableEntry audioMgrEntry = localSymSpace.find(reg1.getName());

		if (audioMgrEntry != null)
			currState = audioMgrEntry.getEntryDetails().getState();

		return currState;
	}

	public TaintAnalyzer getTaSubject() {
		return taSubject;
	}

	public void setTaSubject(TaintAnalyzer taSubject) {
		this.taSubject = taSubject;
	}

}
