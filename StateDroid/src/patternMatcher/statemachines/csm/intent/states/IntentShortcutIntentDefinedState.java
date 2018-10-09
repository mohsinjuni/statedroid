package patternMatcher.statemachines.csm.intent.states;

import java.util.Hashtable;

import models.cfg.InstructionResponse;
import models.cfg.Register;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;
import patternMatcher.events.csm.intent.IntentCategoryDefinedEvent;
import patternMatcher.events.csm.intent.IntentSetActionEvent;
import patternMatcher.events.csm.intent.IntentSetDataEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.csm.intent.IntentStates;
import patternMatcher.statemachines.csm.uri.states.UriTelDefinedState;
import taintanalyzer.TaintAnalyzer;
import configuration.Config;

public class IntentShortcutIntentDefinedState extends IntentStates {

	private TaintAnalyzer ta;
	private SymbolSpace localSymbolSpace;

	public IntentShortcutIntentDefinedState(TaintAnalyzer taParam) {
		this.ta = taParam;
		this.localSymbolSpace = Config.getInstance().getLocalSymbolSpace();
	}

	public IntentShortcutIntentDefinedState() {
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
				if (actionEntry != null
						&& actionEntry.getEntryDetails().getValue().contains("com.android.launcher.action.INSTALL_SHORTCUT")) {
					IntentShortcutIntentAndActionDefinedState state = new IntentShortcutIntentAndActionDefinedState(this.ta);
					intentEntry.getEntryDetails().setState(state);
				}
			}
		}
		return this;
	}

}
