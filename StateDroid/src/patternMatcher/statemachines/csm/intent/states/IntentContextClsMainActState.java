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


public class IntentContextClsMainActState extends IntentStates{
	
	private TaintAnalyzer ta;
	private SymbolSpace localSymbolSpace;
	
	public IntentContextClsMainActState(TaintAnalyzer taParam)
	{
		this.ta = taParam;
		this.localSymbolSpace = Config.getInstance().getLocalSymbolSpace();
	}
	public IntentContextClsMainActState(){}
	
}
