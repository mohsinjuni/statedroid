package patternMatcher.statemachines.csm.intent.states;

import java.util.Hashtable;

import models.cfg.InstructionResponse;
import models.cfg.Register;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;
import patternMatcher.events.csm.intent.IntentActionDefinedEvent;
import patternMatcher.events.csm.intent.IntentActionUriDefinedEvent;
import patternMatcher.events.csm.intent.IntentContextClsDefinedEvent;
import patternMatcher.events.csm.intent.IntentPutExtraEvent;
import patternMatcher.events.csm.intent.IntentSetActionEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.csm.intent.IntentStates;
import patternMatcher.statemachines.csm.uri.states.UriCallForwardingDefinedState;
import patternMatcher.statemachines.csm.uri.states.UriTelDefinedState;
import taintanalyzer.TaintAnalyzer;
import configuration.Config;

public class IntentDefinedState extends IntentStates {

	private TaintAnalyzer ta;
	private SymbolSpace localSymbolSpace;

	public IntentDefinedState(TaintAnalyzer taParam) {
		this.ta = taParam;
		this.localSymbolSpace = Config.getInstance().getLocalSymbolSpace();
	}

	public IntentDefinedState() {
	}

	private String currInstr = "";

	@Override
	public State update(IntentPutExtraEvent e) {
		InstructionResponse ir = (InstructionResponse) e.getEventInfo().get(InstructionResponse.CLASS_NAME);
		String instrText = ir.getInstr().getText();

		Register intentReg = ir.getInvolvedRegisters().get(0);
		SymbolTableEntry intentEntry = this.localSymbolSpace.find(intentReg.getName());

		if (intentEntry != null) {
			SymbolTableEntry param1Entry = (SymbolTableEntry) e.getEventInfo().get("param1Entry");
			SymbolTableEntry param2Entry = (SymbolTableEntry) e.getEventInfo().get("param2Entry");
			if (param1Entry != null && param2Entry != null) {
				State currState = param2Entry.getEntryDetails().getState();
				if (currState != null && currState instanceof IntentContextClsMainActState) {
					String param1Value = param1Entry.getEntryDetails().getValue();
					if (param1Value != null && param1Value.contains("extra.shortcut.INTENT")) {
						IntentShortcutIntentDefinedState state = new IntentShortcutIntentDefinedState(this.ta);
						intentEntry.getEntryDetails().setState(state);
					}
				}
			}
		}
		return this;
	}

	@Override
	public State update(IntentSetActionEvent e) {
		InstructionResponse ir = (InstructionResponse) e.getEventInfo().get(InstructionResponse.CLASS_NAME);
		String instrText = ir.getInstr().getText();

		Register intentReg = ir.getInvolvedRegisters().get(0);
		SymbolTableEntry intentEntry = this.localSymbolSpace.find(intentReg.getName());

		if (intentEntry != null) {
			Hashtable recordFieldList = intentEntry.getEntryDetails().getRecordFieldList();
			if (recordFieldList != null && recordFieldList.containsKey("actionEntry")) {
				SymbolTableEntry actionEntry = (SymbolTableEntry) recordFieldList.get("actionEntry");
				if (actionEntry != null && actionEntry.getEntryDetails().getValue().contains("com.android.launcher.action.INSTALL_SHORTCUT")) {
					IntentShortcutActionDefinedState state = new IntentShortcutActionDefinedState(this.ta);
					intentEntry.getEntryDetails().setState(state);
				}
			}
		}
		return this;
	}

}
