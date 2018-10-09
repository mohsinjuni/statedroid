package patternMatcher.statemachines.csm.reflection;
import patternMatcher.events.csm.reflection.ClassGetDeclaredFieldEvent;
import patternMatcher.events.csm.reflection.ClassGetDeclaredMethodEvent;
import patternMatcher.events.csm.reflection.FieldGetByKeyEvent;
import patternMatcher.events.csm.reflection.FieldSetAccessibleEvent;
import patternMatcher.events.csm.reflection.ITelephonyAnswerRingingCallEvent;
import patternMatcher.events.csm.reflection.ITelephonySilenceRingerEvent;
import patternMatcher.events.csm.reflection.MethodInvokeEvent;
import patternMatcher.events.csm.reflection.MethodSetAccessibleEvent;
import patternMatcher.events.csm.reflection.ShowCallScreenWithDialpadEvent;
import patternMatcher.statemachines.State;



public class ReflectionStates extends State{

	public State update(ClassGetDeclaredMethodEvent e){ return this;};	
	public State update(MethodSetAccessibleEvent e){ return this;};	
	public State update(MethodInvokeEvent e){ return this;};	
	public State update(ITelephonySilenceRingerEvent e) { return this;};
	public State update(ITelephonyAnswerRingingCallEvent e) { return this;};
	public State update(ClassGetDeclaredFieldEvent e){ return this;};	
	public State update(FieldSetAccessibleEvent e){ return this;};	
	public State update(FieldGetByKeyEvent e){ return this;};	
	public State update(ShowCallScreenWithDialpadEvent e){ return this;};	
	
}
