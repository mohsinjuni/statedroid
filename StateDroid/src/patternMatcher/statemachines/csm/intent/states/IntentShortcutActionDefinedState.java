package patternMatcher.statemachines.csm.intent.states;
import java.util.Hashtable;

import models.cfg.InstructionResponse;
import models.cfg.Register;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;
import patternMatcher.events.csm.intent.IntentCategoryDefinedEvent;
import patternMatcher.events.csm.intent.IntentPutExtraEvent;
import patternMatcher.events.csm.intent.IntentSetDataEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.csm.intent.IntentStates;
import patternMatcher.statemachines.csm.uri.states.UriTelDefinedState;
import taintanalyzer.TaintAnalyzer;
import configuration.Config;


public class IntentShortcutActionDefinedState extends IntentStates{
	
	private TaintAnalyzer ta;
	private SymbolSpace localSymbolSpace;
	
	public IntentShortcutActionDefinedState(TaintAnalyzer taParam)
	{
		this.ta = taParam;
		this.localSymbolSpace = Config.getInstance().getLocalSymbolSpace();
	}
	public IntentShortcutActionDefinedState(){}
	
	@Override
	public State update(IntentPutExtraEvent e) {
		InstructionResponse ir = (InstructionResponse) e.getEventInfo().get(InstructionResponse.CLASS_NAME);
		String instrText = ir.getInstr().getText();

		Register intentReg = ir.getInvolvedRegisters().get(0);
		SymbolTableEntry intentEntry = this.localSymbolSpace.find(intentReg.getName());
		
		if(intentEntry != null){
			SymbolTableEntry param1Entry = (SymbolTableEntry) e.getEventInfo().get("param1Entry"); 
			SymbolTableEntry param2Entry = (SymbolTableEntry) e.getEventInfo().get("param2Entry");
			if(param1Entry != null && param2Entry != null){
				State currState = param2Entry.getEntryDetails().getState();
				if(currState != null && currState instanceof IntentContextClsMainActState){
					String param1Value = param1Entry.getEntryDetails().getValue();
					if(param1Value != null &&  param1Value.contains("extra.shortcut.INTENT")){
						IntentShortcutIntentAndActionDefinedState state = new IntentShortcutIntentAndActionDefinedState(this.ta);
						intentEntry.getEntryDetails().setState(state);
					}
				}
			}
		}
		return this;
	}
}
