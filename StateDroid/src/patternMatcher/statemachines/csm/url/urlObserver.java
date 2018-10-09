package patternMatcher.statemachines.csm.url;

import java.util.Hashtable;

import configuration.Config;

import models.cfg.InstructionResponse;
import models.cfg.Register;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;
import patternMatcher.AttackObserver;
import patternMatcher.events.Event;
import patternMatcher.events.csm.filereading.DataOutputStreamDefinedEvent;
import patternMatcher.events.csm.uri.UriParsedEvent;
import patternMatcher.events.csm.url.UrlGetOutputStreamEvent;
import patternMatcher.events.csm.url.UrlInitEvent;
import patternMatcher.events.csm.url.UrlOpenConnectionEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.csm.url.states.InitialState;
import taintanalyzer.TaintAnalyzer;

public class urlObserver extends AttackObserver {

	private State state;
	private TaintAnalyzer taSubject;
	private SymbolSpace localSymSpace = Config.getInstance().getLocalSymbolSpace();

	public urlObserver(TaintAnalyzer taParam) {
		this.taSubject = taParam;
		//		this.taSubject.attach(this);
		this.state = new InitialState(taSubject);
	}

	public urlObserver() {
		this.state = new InitialState(taSubject);
	}

	@Override
	public void update(UrlInitEvent e) {
		this.state = new InitialState(taSubject);
		state = state.update(e);
	}

	@Override
	public void update(UrlOpenConnectionEvent e) {
		State currState = getCurrentStateOfCallerEntry(e);
		if (currState != null) {
			this.state = currState;
			state = state.update(e);
		}
	}

	@Override
	public void update(UrlGetOutputStreamEvent e) {
		State currState = getCurrentStateOfCallerEntry(e);
		if (currState != null) {
			this.state = currState;
			state = state.update(e);
		}
	}

	@Override
	public void update(DataOutputStreamDefinedEvent e) {
		State currState = getCurrentState(e, 1);
		if (currState != null) {
			this.state = currState;
			state = state.update(e);
		}
	}

	public State getCurrentState(Event e, int paramNo) {

		State currState = null;
		Hashtable eventInfo = (Hashtable) e.getEventInfo();
		InstructionResponse ir = (InstructionResponse) eventInfo.get(InstructionResponse.CLASS_NAME);

		Register paramReg = ir.getInvolvedRegisters().get(paramNo);
		SymbolTableEntry paramEntry = localSymSpace.find(paramReg.getName());
		if (paramEntry != null)
			currState = paramEntry.getEntryDetails().getState();

		return currState;
	}

	public State getCurrentStateOfCallerEntry(Event e) {
		SymbolTableEntry urlEntry = (SymbolTableEntry) e.getEventInfo().get("entry");
		if (urlEntry != null) {
			return urlEntry.getEntryDetails().getState();
		}
		return null;
	}

	public TaintAnalyzer getTaSubject() {
		return taSubject;
	}

	public void setTaSubject(TaintAnalyzer taSubject) {
		this.taSubject = taSubject;
	}

}
