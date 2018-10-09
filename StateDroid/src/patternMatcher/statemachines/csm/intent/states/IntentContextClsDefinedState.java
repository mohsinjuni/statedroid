package patternMatcher.statemachines.csm.intent.states;
import java.util.Hashtable;

import models.cfg.InstructionResponse;
import models.cfg.Register;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;
import patternMatcher.events.csm.intent.IntentSetActionEvent;
import patternMatcher.events.csm.intent.IntentSetDataEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.csm.intent.IntentStates;
import patternMatcher.statemachines.csm.uri.states.UriTelDefinedState;
import taintanalyzer.TaintAnalyzer;
import configuration.Config;


public class IntentContextClsDefinedState extends IntentStates{
	
	private TaintAnalyzer ta;
	private SymbolSpace localSymbolSpace;
	
	public IntentContextClsDefinedState(TaintAnalyzer taParam)
	{
		this.ta = taParam;
		this.localSymbolSpace = Config.getInstance().getLocalSymbolSpace();
	}
	public IntentContextClsDefinedState(){}
	
//    Intent shortcutIntent = new Intent(getApplicationContext(), MainActivity.class);
//    shortcutIntent.setAction(Intent.ACTION_MAIN);
	
	@Override
	public State update(IntentSetActionEvent e) {
		InstructionResponse ir = (InstructionResponse) e.getEventInfo().get(InstructionResponse.CLASS_NAME);
		String instrText = ir.getInstr().getText();

		Register intentReg = ir.getInvolvedRegisters().get(0);
		SymbolTableEntry intentEntry = this.localSymbolSpace.find(intentReg.getName());
		
		if(intentEntry != null){
			State intentState = intentEntry.getEntryDetails().getState();
			if(intentState != null && intentState instanceof IntentContextClsDefinedState){ 
				Hashtable recordFieldList = intentEntry.getEntryDetails().getRecordFieldList();
				if(recordFieldList != null){
					SymbolTableEntry actionEntry = (SymbolTableEntry) recordFieldList.get("actionEntry");
					if(actionEntry != null){
						String actionName = actionEntry.getEntryDetails().getValue();
						if(actionName.contains("android.intent.action.MAIN")){
							IntentContextClsMainActState state = new IntentContextClsMainActState(this.ta);
							intentEntry.getEntryDetails().setState(state);
						}
					}
				}
			}
		}
		return this;
	}


}
