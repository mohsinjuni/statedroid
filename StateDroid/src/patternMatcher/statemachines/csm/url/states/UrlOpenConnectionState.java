package patternMatcher.statemachines.csm.url.states;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;
import patternMatcher.events.csm.url.UrlGetOutputStreamEvent;
import patternMatcher.events.csm.url.UrlOpenConnectionEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.csm.url.urlStates;
import taintanalyzer.TaintAnalyzer;
import configuration.Config;

public class UrlOpenConnectionState extends urlStates{
	
	private TaintAnalyzer ta;
	private SymbolSpace localSymbolSpace;
	public UrlOpenConnectionState(TaintAnalyzer taParam){
		this.ta = taParam;
		this.localSymbolSpace = Config.getInstance().getLocalSymbolSpace();
	}

	public UrlOpenConnectionState(){}

//		0x1f8 invoke-virtual/range v35, Ljavax/net/ssl/HttpsURLConnection;->getOutputStream()Ljava/io/OutputStream;
//		0x1fe move-result-object v36

	@Override
	public State update(UrlGetOutputStreamEvent e) {
		SymbolTableEntry urlEntry = (SymbolTableEntry) e.getEventInfo().get("entry");
		if(urlEntry != null){
			UrlGetOutputStreamState state = new UrlGetOutputStreamState(this.ta);
			urlEntry.getEntryDetails().setState(state);
		}
		return this;
	}
}
