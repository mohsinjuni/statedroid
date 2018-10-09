package patternMatcher.statemachines.csm.intent.states;

import java.util.Hashtable;

import models.cfg.InstructionResponse;
import models.cfg.Register;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;
import patternMatcher.events.csm.intent.IntentSetDataEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.csm.intent.IntentStates;
import patternMatcher.statemachines.csm.uri.states.UriTelDefinedState;
import taintanalyzer.TaintAnalyzer;
import configuration.Config;

public class IntentActionCallDefinedState extends IntentStates {

	private TaintAnalyzer ta;
	private SymbolSpace localSymbolSpace;

	public IntentActionCallDefinedState(TaintAnalyzer taParam) {
		this.ta = taParam;
		this.localSymbolSpace = Config.getInstance().getLocalSymbolSpace();
	}

	public IntentActionCallDefinedState() {
	}

	//	0x42 invoke-static v1, Landroid/net/Uri;->parse(Ljava/lang/String;)Landroid/net/Uri;
	//	0x48 move-result-object v1
	//	0x4a invoke-virtual v0, v1, Landroid/content/Intent;->setData(Landroid/net/Uri;)Landroid/content/Intent;

	@Override
	public State update(IntentSetDataEvent e) {
		InstructionResponse ir = (InstructionResponse) e.getEventInfo().get(InstructionResponse.CLASS_NAME);
		String instrText = ir.getInstr().getText();

		Register intentReg = ir.getInvolvedRegisters().get(0);
		SymbolTableEntry intentEntry = this.localSymbolSpace.find(intentReg.getName());

		if (intentEntry != null) {
			State intentState = intentEntry.getEntryDetails().getState();
			if (intentState != null && intentState instanceof IntentActionCallDefinedState) { // I should better use getClass(). instanceOf will return true
																								// event if it is subclass of IntentActionCallDefinedState
																								// Or make this state-class as a final class.
				Hashtable recordFieldList = intentEntry.getEntryDetails().getRecordFieldList();
				if (recordFieldList != null) {
					SymbolTableEntry uriEntry = (SymbolTableEntry) recordFieldList.get("uriEntry");
					if (uriEntry != null) {
						State uriState = uriEntry.getEntryDetails().getState();
						if (uriState != null && uriState instanceof UriTelDefinedState) {
							IntentActionCallUriTellDefinedState state = new IntentActionCallUriTellDefinedState();
							intentEntry.getEntryDetails().setState(state);
						}
					}
				}
			}
		}
		return this;
	}

}
