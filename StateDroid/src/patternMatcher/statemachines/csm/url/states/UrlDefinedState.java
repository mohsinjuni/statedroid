package patternMatcher.statemachines.csm.url.states;
import java.util.Hashtable;

import models.cfg.InstructionResponse;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;
import patternMatcher.events.csm.url.UrlInitEvent;
import patternMatcher.events.csm.url.UrlOpenConnectionEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.csm.url.urlStates;
import taintanalyzer.TaintAnalyzer;
import configuration.Config;


public class UrlDefinedState extends urlStates{
	
	private TaintAnalyzer ta;
	private SymbolSpace localSymbolSpace;
	public UrlDefinedState(TaintAnalyzer taParam){
		this.ta = taParam;
		this.localSymbolSpace = Config.getInstance().getLocalSymbolSpace();
	}

	public UrlDefinedState(){}
	
//		0x110 invoke-virtual/range v34, Ljava/net/URL;->openConnection()Ljava/net/URLConnection;
//		0x116 move-result-object v35

//	   event.getEventInfo().put("entry", returnEntryClone);
//	   event.getEventInfo().put(InstructionResponse.CLASS_NAME, ir);
	
	@Override
	public State update(UrlOpenConnectionEvent e) {
		SymbolTableEntry urlEntry = (SymbolTableEntry) e.getEventInfo().get("entry");
		if(urlEntry != null){
			UrlOpenConnectionState state = new UrlOpenConnectionState(this.ta);
			urlEntry.getEntryDetails().setState(state);
		}
		return this;
	}

}
