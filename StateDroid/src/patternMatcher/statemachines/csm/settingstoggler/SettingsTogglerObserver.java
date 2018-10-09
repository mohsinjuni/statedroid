package patternMatcher.statemachines.csm.settingstoggler;
import java.util.Hashtable;

import models.cfg.InstructionResponse;
import models.cfg.Register;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;
import patternMatcher.AttackObserver;
import patternMatcher.events.Event;
import patternMatcher.events.csm.settings.SetWifiEnabledEvent;
import patternMatcher.events.csm.settings.SettingsGlobalPutStringEvent;
import patternMatcher.events.csm.settings.SettingsSystemPutIntEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.csm.settingstoggler.states.InitialState;
import taintanalyzer.TaintAnalyzer;
import configuration.Config;


public class SettingsTogglerObserver extends AttackObserver {

	private State state;
	private TaintAnalyzer taSubject;
	private Hashtable attackParameters;
	private SymbolSpace localSymSpace = Config.getInstance().getLocalSymbolSpace();

	public SettingsTogglerObserver (){}
	
	public SettingsTogglerObserver (TaintAnalyzer taParam)
	{
		this.taSubject = taParam;
		this.attackParameters = new Hashtable();
	}

	@Override
	public void update(SetWifiEnabledEvent e) {
//		0xc check-cast v0, Landroid/net/wifi/WifiManager;
//		0x10 invoke-virtual v0, v3, Landroid/net/wifi/WifiManager;->setWifiEnabled(Z)Z
		
    	State currState = getCurrentStateOfCallerEntry(e);
    	if(currState != null){
    		this.state = currState;
        	state = state.update(e);
    	}
	}

	@Override
	public void update(SettingsSystemPutIntEvent e) {
		this.state = new InitialState(this.taSubject);
    	state = state.update(e);
	}
	
	@Override
	public void update(SettingsGlobalPutStringEvent e) {
		this.state = new InitialState(this.taSubject);
    	state = state.update(e);
	}
	public State getCurrentStateOfCallerEntry(Event e){

		State currState = null;
		Hashtable eventInfo = (Hashtable) e.getEventInfo();
  		InstructionResponse ir = (InstructionResponse) eventInfo.get(InstructionResponse.CLASS_NAME);
  		
  		Register reg1 = ir.getInvolvedRegisters().get(0);  
        SymbolTableEntry wifiMgrEntry = localSymSpace.find(reg1.getName());

        if(wifiMgrEntry != null)
        	currState = wifiMgrEntry.getEntryDetails().getState();
		
		return currState;
	}

	public TaintAnalyzer getTaSubject() {
		return taSubject;
	}

	public void setTaSubject(TaintAnalyzer taSubject) {
		this.taSubject = taSubject;
	}


}
