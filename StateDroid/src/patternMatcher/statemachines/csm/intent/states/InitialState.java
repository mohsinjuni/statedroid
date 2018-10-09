package patternMatcher.statemachines.csm.intent.states;

import java.util.Hashtable;

import models.cfg.InstructionResponse;
import models.cfg.Register;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;
import patternMatcher.events.csm.intent.IntentActionDefinedEvent;
import patternMatcher.events.csm.intent.IntentActionUriDefinedEvent;
import patternMatcher.events.csm.intent.IntentContextClsDefinedEvent;
import patternMatcher.events.csm.intent.IntentDefinedEvent;
import patternMatcher.events.csm.intent.IntentPutExtraEvent;
import patternMatcher.events.csm.intent.IntentSetActionEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.csm.intent.IntentStates;
import patternMatcher.statemachines.csm.uri.states.UriCallForwardingDefinedState;
import patternMatcher.statemachines.csm.uri.states.UriTelDefinedState;
import taintanalyzer.TaintAnalyzer;
import configuration.Config;

public class InitialState extends IntentStates {

	private TaintAnalyzer ta;
	private SymbolSpace localSymbolSpace;

	public InitialState(TaintAnalyzer taParam) {
		this.ta = taParam;
		this.localSymbolSpace = Config.getInstance().getLocalSymbolSpace();
	}

	public InitialState() {
	}

	private String currInstr = "";

	/*
	 * 0xd6 invoke-direct v2, v7, v8,
	 * Landroid/content/Intent;-><init>(Ljava/lang/String; Landroid/net/Uri;)V
	 * intent(action, Uri);
	 */

	@Override
	public State update(IntentActionUriDefinedEvent e) {
		InstructionResponse ir = (InstructionResponse) e.getEventInfo().get(InstructionResponse.CLASS_NAME);
		String instrText = ir.getInstr().getText();

		Register uriReg = ir.getInvolvedRegisters().get(0);
		SymbolTableEntry intentEntry = this.localSymbolSpace.find(uriReg.getName());

		if (intentEntry != null) {
			Hashtable recordFieldList = intentEntry.getEntryDetails().getRecordFieldList();
			if (recordFieldList != null) {
				SymbolTableEntry uriEntry = (SymbolTableEntry) recordFieldList.get("uriEntry");
				SymbolTableEntry actionEntry = (SymbolTableEntry) recordFieldList.get("actionEntry");

				if (uriEntry != null && actionEntry != null) {
					String actionName = actionEntry.getEntryDetails().getValue();
					State uriState = uriEntry.getEntryDetails().getState();
					if (actionName != null && !actionName.isEmpty()) {
						if (actionName.contains("action.CALL") // 'android.intent.action.CALL' 
								&& uriState instanceof UriTelDefinedState) {
							IntentActionCallUriTellDefinedState state = new IntentActionCallUriTellDefinedState();
							intentEntry.getEntryDetails().setState(state);
						} else if (actionName.contains("action.CALL") // 'android.intent.action.CALL' 
								&& uriState instanceof UriCallForwardingDefinedState) {
							IntentActionCallUriCallFrwrdingDefinedState state = new IntentActionCallUriCallFrwrdingDefinedState();
							intentEntry.getEntryDetails().setState(state);
						}
					}
				}
			}
		}
		return this;
	}

	//		 * 	 *   	0xa new-instance v0, Landroid/content/Intent;
	//					0xe const-string v1, 'android.intent.action.CALL'
	//					0x12 invoke-direct v0, v1, Landroid/content/Intent;-><init>(Ljava/lang/String;)V

	//				0x42 invoke-static v1, Landroid/net/Uri;->parse(Ljava/lang/String;)Landroid/net/Uri;
	//				0x48 move-result-object v1
	//				0x4a invoke-virtual v0, v1, Landroid/content/Intent;->setData(Landroid/net/Uri;)Landroid/content/Intent;
	//				0x62 invoke-virtual v1, v0, Landroid/content/Context;->startActivity(Landroid/content/Intent;)V
	@Override
	public State update(IntentActionDefinedEvent e) {
		InstructionResponse ir = (InstructionResponse) e.getEventInfo().get(InstructionResponse.CLASS_NAME);
		String instrText = ir.getInstr().getText();

		Register intentReg = ir.getInvolvedRegisters().get(0);
		SymbolTableEntry intentEntry = this.localSymbolSpace.find(intentReg.getName());

		if (intentEntry != null) {
			Hashtable recordFieldList = intentEntry.getEntryDetails().getRecordFieldList();
			if (recordFieldList != null) {
				SymbolTableEntry actionEntry = (SymbolTableEntry) recordFieldList.get("actionEntry");
				if (actionEntry != null) {
					String actionName = actionEntry.getEntryDetails().getValue();
					if (actionName != null && !actionName.isEmpty() && actionName.contains("action.CALL")) {
						IntentActionCallDefinedState state = new IntentActionCallDefinedState(this.ta);
						intentEntry.getEntryDetails().setState(state);
					} else if (actionName != null && !actionName.isEmpty() && actionName.contains("action.MAIN")) {
						IntentActionMainDefinedState state = new IntentActionMainDefinedState(this.ta);
						intentEntry.getEntryDetails().setState(state);
					}
				}
			}
		}
		return this;
	}

	@Override
	public State update(IntentContextClsDefinedEvent e) {
		InstructionResponse ir = (InstructionResponse) e.getEventInfo().get(InstructionResponse.CLASS_NAME);
		String instrText = ir.getInstr().getText();

		Register intentReg = ir.getInvolvedRegisters().get(0);
		SymbolTableEntry intentEntry = this.localSymbolSpace.find(intentReg.getName());

		if (intentEntry != null) {
			IntentContextClsDefinedState state = new IntentContextClsDefinedState(this.ta);
			intentEntry.getEntryDetails().setState(state);
		}
		return this;
	}

	@Override
	public State update(IntentDefinedEvent e) {
		InstructionResponse ir = (InstructionResponse) e.getEventInfo().get(InstructionResponse.CLASS_NAME);
		String instrText = ir.getInstr().getText();

		Register intentReg = ir.getInvolvedRegisters().get(0);
		SymbolTableEntry intentEntry = this.localSymbolSpace.find(intentReg.getName());

		if (intentEntry != null) {
			IntentDefinedState state = new IntentDefinedState(this.ta);
			intentEntry.getEntryDetails().setState(state);
		}
		return this;
	}

}
