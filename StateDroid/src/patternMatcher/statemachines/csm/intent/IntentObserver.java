package patternMatcher.statemachines.csm.intent;

import java.util.Hashtable;

import models.cfg.InstructionResponse;
import models.cfg.Register;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;
import patternMatcher.AttackObserver;
import patternMatcher.events.Event;
import patternMatcher.events.csm.SendBroadcastEvent;
import patternMatcher.events.csm.intent.IntentActionDefinedEvent;
import patternMatcher.events.csm.intent.IntentActionUriDefinedEvent;
import patternMatcher.events.csm.intent.IntentCategoryDefinedEvent;
import patternMatcher.events.csm.intent.IntentContextClsDefinedEvent;
import patternMatcher.events.csm.intent.IntentDefinedEvent;
import patternMatcher.events.csm.intent.IntentPutExtraEvent;
import patternMatcher.events.csm.intent.IntentSetActionEvent;
import patternMatcher.events.csm.intent.IntentSetDataEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.csm.intent.states.InitialState;
import taintanalyzer.TaintAnalyzer;
import configuration.Config;

public class IntentObserver extends AttackObserver {

	private State state;
	private TaintAnalyzer taSubject;
	private SymbolSpace localSymbolSpace;
	private SymbolSpace localSymSpace = Config.getInstance().getLocalSymbolSpace();

	public IntentObserver(TaintAnalyzer taParam) {
		this.taSubject = taParam;
		//		this.taSubject.attach(this);
		this.state = new InitialState(taSubject);
		this.localSymbolSpace = Config.getInstance().getLocalSymbolSpace();
	}

	public IntentObserver() {
		this.state = new InitialState(taSubject);
	}

	//Get current state of intent and set it to 'this.state' and then let it make the transition. This is specially helpful for MediaRecorder objects.

	@Override
	public void update(IntentActionDefinedEvent e) {
		//This event is received when intent defines its action by its 'Intent(String action)' API or may be, by '.addAction()' API.
		this.state = new InitialState(taSubject);
		state = state.update(e);
	}

	@Override
	public void update(IntentSetDataEvent e) {
		this.state = getCurrentStateOfAudioManager(e);
		if (this.state != null) {
			state = state.update(e);
		}
	}

	@Override
	public void update(IntentActionUriDefinedEvent e) {
		// 		0xd6 invoke-direct v2, v7, v8, Landroid/content/Intent;-><init>(Ljava/lang/String; Landroid/net/Uri;)V
		this.state = new InitialState(taSubject);
		state = state.update(e);
	}

	@Override
	public void update(IntentContextClsDefinedEvent e) {
		this.state = new InitialState(taSubject);
		state = state.update(e);
	}

	@Override
	public void update(SendBroadcastEvent e) {
		State currState = getCurrentState(e, 1);
		if (currState != null) {
			this.state = currState;
			state = state.update(e);
		}
	}

	@Override
	public void update(IntentDefinedEvent e) {
		this.state = new InitialState(taSubject);
		state = state.update(e);
	}

	@Override
	public void update(IntentSetActionEvent e) {
		State currState = getCurrentState(e, 0);
		if (currState != null) {
			this.state = currState;
			state = state.update(e);
		}
	}

	@Override
	public void update(IntentPutExtraEvent e) {
		State currState = getCurrentState(e, 0);
		if (currState != null) {
			this.state = currState;
			state = state.update(e);
		}
	}

	@Override
	public void update(IntentCategoryDefinedEvent e) {
		//	0x1c invoke-virtual v0, v1, Landroid/content/Intent;->addCategory(Ljava/lang/String;)Landroid/content/Intent;

		State currState = getCurrentState(e, 0);
		if (currState != null) {
			this.state = currState;
			state = state.update(e);
		}
	}

	public State getCurrentStateOfAudioManager(Event e) {
		State currState = null;
		Hashtable eventInfo = (Hashtable) e.getEventInfo();
		InstructionResponse ir = (InstructionResponse) eventInfo.get(InstructionResponse.CLASS_NAME);

		Register reg1 = ir.getInvolvedRegisters().get(0);
		SymbolTableEntry intentEntry = localSymSpace.find(reg1.getName());

		if (intentEntry != null)
			currState = intentEntry.getEntryDetails().getState();

		return currState;
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

	public TaintAnalyzer getTaSubject() {
		return taSubject;
	}

	public void setTaSubject(TaintAnalyzer taSubject) {
		this.taSubject = taSubject;
	}

}
