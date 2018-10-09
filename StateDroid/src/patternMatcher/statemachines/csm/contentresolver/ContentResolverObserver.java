package patternMatcher.statemachines.csm.contentresolver;
import java.util.Hashtable;

import configuration.Config;

import models.cfg.InstructionResponse;
import models.cfg.Register;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;
import patternMatcher.AttackObserver;
import patternMatcher.events.Event;
import patternMatcher.events.csm.SmsSenderEvent;
import patternMatcher.events.csm.contentresolver.ContentResolverApplyBatchEvent;
import patternMatcher.events.csm.contentresolver.ContentResolverDeleteEvent;
import patternMatcher.events.csm.contentresolver.ContentResolverInsertEvent;
import patternMatcher.events.csm.contentresolver.ContentResolverQueryEvent;
import patternMatcher.events.csm.contentresolver.ContentResolverUpdateEvent;
import patternMatcher.events.csm.cursor.CursorGetStringEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.csm.contentresolver.states.InitialState;
import taintanalyzer.TaintAnalyzer;


public class ContentResolverObserver extends AttackObserver {

	private State state;
	private TaintAnalyzer taSubject;
	private SymbolSpace localSymSpace = Config.getInstance().getLocalSymbolSpace();
	

	public ContentResolverObserver (){
		this.state = new InitialState();
	}

	public ContentResolverObserver (TaintAnalyzer taParam){
		this.taSubject = taParam;
		this.state = new InitialState(taSubject);
	}

	@Override
	public void update(ContentResolverUpdateEvent e){ 
		this.state = new InitialState(taSubject);
		this.state = state.update(e);
	}
 	
	@Override
	public void update(ContentResolverDeleteEvent e){ 
		this.state = new InitialState(taSubject);
		this.state = state.update(e);
	}
	
	@Override
	public void update(ContentResolverInsertEvent e){ 
		this.state = new InitialState(taSubject);
		this.state = state.update(e);
	}
	
	@Override
	public void update(ContentResolverApplyBatchEvent e){ 
		this.state = state.update(e);
	}
	
	@Override
	public void update(ContentResolverQueryEvent e){ 
		this.state = new InitialState(taSubject);
		this.state = state.update(e);
	}

	@Override
	public void update(CursorGetStringEvent e){ 
		this.state = getCurrentState(e);
		if(this.state != null){
			this.state = state.update(e);
		}
	}

	@Override
	public void update(SmsSenderEvent e) {
		
	}
	
	public State getCurrentState(Event e){
		State currState = null;
		Hashtable eventInfo = (Hashtable) e.getEventInfo();
  		InstructionResponse ir = (InstructionResponse) eventInfo.get(InstructionResponse.CLASS_NAME);
  		
        SymbolTableEntry callerEntry = (SymbolTableEntry) eventInfo.get("cursorEntry");

        if(callerEntry != null)
        {
        	currState = callerEntry.getEntryDetails().getState();
        }
		
		return currState;
	}
	
	public State getCurrentStateByFirstRegister(Event e){
		State currState = null;
		Hashtable eventInfo = (Hashtable) e.getEventInfo();
  		InstructionResponse ir = (InstructionResponse) eventInfo.get(InstructionResponse.CLASS_NAME);
  		Register crReg = ir.getInvolvedRegisters().get(0);
  		SymbolTableEntry crEntry = this.localSymSpace.find(crReg.getName());
  		if(crEntry != null){
        	currState = crEntry.getEntryDetails().getState();
        }
		
		return currState;
	}
	public TaintAnalyzer getTaSubject() {
		return taSubject;
	}

	public void setTaSubject(TaintAnalyzer taSubject) {
		this.taSubject = taSubject;
	}


}
