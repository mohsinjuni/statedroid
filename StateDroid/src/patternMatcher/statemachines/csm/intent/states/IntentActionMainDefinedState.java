package patternMatcher.statemachines.csm.intent.states;
import java.util.Hashtable;

import models.cfg.InstructionResponse;
import models.cfg.Register;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;
import patternMatcher.events.csm.intent.IntentCategoryDefinedEvent;
import patternMatcher.events.csm.intent.IntentSetDataEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.csm.intent.IntentStates;
import patternMatcher.statemachines.csm.uri.states.UriTelDefinedState;
import taintanalyzer.TaintAnalyzer;
import configuration.Config;


public class IntentActionMainDefinedState extends IntentStates{
	
	private TaintAnalyzer ta;
	private SymbolSpace localSymbolSpace;
	
	public IntentActionMainDefinedState(TaintAnalyzer taParam)
	{
		this.ta = taParam;
		this.localSymbolSpace = Config.getInstance().getLocalSymbolSpace();
	}
	public IntentActionMainDefinedState(){}
	
//	0x42 invoke-static v1, Landroid/net/Uri;->parse(Ljava/lang/String;)Landroid/net/Uri;
//	0x48 move-result-object v1
//	0x4a invoke-virtual v0, v1, Landroid/content/Intent;->setData(Landroid/net/Uri;)Landroid/content/Intent;
	
	@Override
	public State update(IntentCategoryDefinedEvent e) {
		InstructionResponse ir = (InstructionResponse) e.getEventInfo().get(InstructionResponse.CLASS_NAME);
		String instrText = ir.getInstr().getText();

		Register intentReg = ir.getInvolvedRegisters().get(0);
		SymbolTableEntry intentEntry = this.localSymbolSpace.find(intentReg.getName());
		
		if(intentEntry != null){
			State intentState = intentEntry.getEntryDetails().getState();
			if(intentState != null && intentState instanceof IntentActionMainDefinedState){ 
				Hashtable recordFieldList = intentEntry.getEntryDetails().getRecordFieldList();
				if(recordFieldList != null){
					SymbolTableEntry categoryEntry = (SymbolTableEntry) recordFieldList.get("categoryEntry");
					if(categoryEntry != null ){
						String value = categoryEntry.getEntryDetails().getValue();
						if(value != null && value.contains("category.HOME")){
							IntentActionMainCatHomeDefinedState state = new IntentActionMainCatHomeDefinedState();
							intentEntry.getEntryDetails().setState(state);
						}
					}
				}
			}
		}
		return this;
	}
}
