package patternMatcher.statemachines.csm.settingstoggler.states;
import models.cfg.InstructionResponse;
import models.cfg.Register;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;
import patternMatcher.events.Event;
import patternMatcher.events.EventFactory;
import patternMatcher.events.asm.settings.WifiTogglerASMEvent;
import patternMatcher.events.csm.settings.SetWifiEnabledEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.csm.settingstoggler.SettingsTogglerStates;
import taintanalyzer.TaintAnalyzer;
import configuration.Config;


public class WifiManagerDefinedState extends SettingsTogglerStates{

	TaintAnalyzer ta;
	private SymbolSpace localSymSpace = Config.getInstance().getLocalSymbolSpace();
	
	public WifiManagerDefinedState(TaintAnalyzer taParam)
	{
		this.ta = taParam;
	}
	
	public WifiManagerDefinedState(){}
	
	@Override
	public State update(SetWifiEnabledEvent e) {
		
		InstructionResponse ir = (InstructionResponse) e.getEventInfo().get(InstructionResponse.CLASS_NAME);
		Register callerEntryReg = ir.getInvolvedRegisters().get(0);
		SymbolTableEntry callerEntry = this.localSymSpace.find(callerEntryReg.getName());
			
		if(callerEntry != null){
			State currState = callerEntry.getEntryDetails().getState();
			if(currState != null && currState instanceof WifiManagerDefinedState){
				
				String inputBooleanValue = (String) e.getEventInfo().get("booleanValue");
				int val = Integer.parseInt(inputBooleanValue);
				if(val == 0 || val == 1){
					
					EventFactory.getInstance().registerEvent("wifiTogglerASMEvent", new WifiTogglerASMEvent());
					Event asmEvent = EventFactory.getInstance().createEvent("wifiTogglerASMEvent");
					asmEvent.setName("wifiTogglerASMEvent");
					
					asmEvent.setCurrMethodName(ir.getInstr().getCurrMethodName());
					asmEvent.setCurrPkgClsName(ir.getInstr().getCurrPkgClassName());
					
					asmEvent.setCurrComponentPkgName(ta.getCurrComponentPkgName());
					asmEvent.setCurrComponentName(ta.getCurrComponentName());
					asmEvent.setCurrCompCallbackMethodName(ta.getCurrComponentCallback());
					asmEvent.getEventInfo().put(InstructionResponse.CLASS_NAME, ir);
					asmEvent.getEventInfo().put("booleanValue", inputBooleanValue);
					
					ta.setCurrASMEvent(asmEvent);
				}
			}
		}
		return this;  
	}
}
