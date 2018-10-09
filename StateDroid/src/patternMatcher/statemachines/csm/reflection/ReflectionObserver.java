package patternMatcher.statemachines.csm.reflection;
import java.util.Hashtable;

import models.cfg.InstructionResponse;
import models.cfg.Register;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;
import patternMatcher.AttackObserver;
import patternMatcher.events.Event;
import patternMatcher.events.csm.reflection.ClassGetDeclaredFieldEvent;
import patternMatcher.events.csm.reflection.ClassGetDeclaredMethodEvent;
import patternMatcher.events.csm.reflection.FieldGetByKeyEvent;
import patternMatcher.events.csm.reflection.FieldSetAccessibleEvent;
import patternMatcher.events.csm.reflection.ITelephonyAnswerRingingCallEvent;
import patternMatcher.events.csm.reflection.ITelephonyEndCallEvent;
import patternMatcher.events.csm.reflection.ITelephonySilenceRingerEvent;
import patternMatcher.events.csm.reflection.MethodInvokeEvent;
import patternMatcher.events.csm.reflection.MethodSetAccessibleEvent;
import patternMatcher.events.csm.reflection.ShowCallScreenWithDialpadEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.csm.reflection.states.InitialState;
import taintanalyzer.TaintAnalyzer;
import configuration.Config;


public class ReflectionObserver extends AttackObserver {

	private State state;
	private TaintAnalyzer taSubject;
	private SymbolSpace localSymSpace = Config.getInstance().getLocalSymbolSpace();
	

	public ReflectionObserver (TaintAnalyzer taParam){
		this.taSubject = taParam;
		this.state = new InitialState(taSubject);
	}

	public ReflectionObserver (){
		this.state = new InitialState(taSubject);
	}
	    
	@Override
	public void update(ClassGetDeclaredMethodEvent e) {
		State currState = getCurrentState(e);
    	if(currState != null){
    		this.state = currState;
    	}else{
    		this.state = new InitialState(this.taSubject);
    	}
		state = state.update(e);		
	}

	@Override
	public void update(ClassGetDeclaredFieldEvent e) {
		State currState = getCurrentState(e);
    	if(currState != null){
    		this.state = currState;
    	}else{
    		this.state = new InitialState(this.taSubject);
    	}
		state = state.update(e);		
	}

	@Override
	public void update(FieldSetAccessibleEvent e) {
		State currState = getCurrentState(e);
    	if(currState != null){
    		this.state = currState;
    	}else{
    		this.state = new InitialState(this.taSubject);
    	}
		state = state.update(e);		
	}

	@Override
	public void update(FieldGetByKeyEvent e) {
		State currState = getCurrentState(e);
    	if(currState != null){
    		this.state = currState;
    	}else{
    		this.state = new InitialState(this.taSubject);
    	}
		state = state.update(e);		
	}

	@Override
	public void update(MethodSetAccessibleEvent e) {
		// 		0xf8 invoke-virtual v5, v12, Ljava/lang/reflect/Method;->setAccessible(Z)V
		this.state = getCurrentState(e);
		if(this.state != null){
			state = state.update(e);		
		}
	}

	@Override
	public void update(MethodInvokeEvent e) {
		State currState = getCurrentState(e);
    	if(currState != null){
    		this.state = currState;
    		state = state.update(e);		
    	}		
	}

	@Override
	public void update(ITelephonyEndCallEvent e) {
		State currState = getCurrentState(e);
    	if(currState != null){
    		this.state = currState;
    		state = state.update(e);		
    	}		
	}
	
	@Override
	public void update(ITelephonySilenceRingerEvent e) {
		State currState = getCurrentState(e);
    	if(currState != null){
    		this.state = currState;
    		state = state.update(e);		
    	}		
	}
	
	@Override
	public void update(ITelephonyAnswerRingingCallEvent e) {
		State currState = getCurrentState(e);
    	if(currState != null){
    		this.state = currState;
    		state = state.update(e);		
    	}		
	}
	
	@Override
    public void update(ShowCallScreenWithDialpadEvent e){ 
//		0x20 invoke-interface v1, v2, Lcom/android/internal/telephony/ITelephony;->showCallScreenWithDialpad(Z)Z
		State currState = getCurrentState(e);
    	if(currState != null){
    		this.state = currState;
    		state = state.update(e);		
    	}		
	}

	
	public State getCurrentState(Event e){
		State currState = null;
		Hashtable eventInfo = (Hashtable) e.getEventInfo();
  		InstructionResponse ir = (InstructionResponse) eventInfo.get(InstructionResponse.CLASS_NAME);
  		
  		Register reg1 = ir.getInvolvedRegisters().get(0);  
        SymbolTableEntry recorderEntry = localSymSpace.find(reg1.getName());

        if(recorderEntry != null){
        	currState = recorderEntry.getEntryDetails().getState();
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
