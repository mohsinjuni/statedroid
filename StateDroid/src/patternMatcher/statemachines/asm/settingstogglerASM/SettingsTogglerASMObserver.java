package patternMatcher.statemachines.asm.settingstogglerASM;
import patternMatcher.AttackObserver;
import patternMatcher.events.asm.phonevolume.RingerModeSilentASMEvent;
import patternMatcher.events.asm.settings.DeviceAirPlaneModeModificationASMEvent;
import patternMatcher.events.asm.settings.DeviceBrightnessModificationASMEvent;
import patternMatcher.events.asm.settings.MobileDataTogglerASMEvent;
import patternMatcher.events.asm.settings.WifiTogglerASMEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.asm.settingstogglerASM.states.InitialState;
import taintanalyzer.TaintAnalyzer;


public class SettingsTogglerASMObserver extends AttackObserver {

	private State state;
	private TaintAnalyzer taSubject;
	
	public SettingsTogglerASMObserver (TaintAnalyzer taParam)
	{
		this.taSubject = taParam;
		this.state = new InitialState(this.taSubject);
	}
	public SettingsTogglerASMObserver (){
		this.state = new InitialState();
	}

	@Override
	public void update(WifiTogglerASMEvent e) {
		this.state = new InitialState(this.taSubject);
		state = state.update(e);		
	}
	
	@Override
	public void update(DeviceBrightnessModificationASMEvent e) {
		this.state = new InitialState(this.taSubject);
		state = state.update(e);		
	}
	
	@Override
	public void update(DeviceAirPlaneModeModificationASMEvent e) {
		this.state = new InitialState(this.taSubject);
		state = state.update(e);		
	}
	
	@Override
	public void update(MobileDataTogglerASMEvent e) {
		this.state = new InitialState(this.taSubject);
		state = state.update(e);		
	}
	public TaintAnalyzer getTaSubject() {
		return taSubject;
	}

	public void setTaSubject(TaintAnalyzer taSubject) {
		this.taSubject = taSubject;
	}


}
