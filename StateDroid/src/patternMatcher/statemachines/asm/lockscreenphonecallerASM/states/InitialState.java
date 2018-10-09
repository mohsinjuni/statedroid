package patternMatcher.statemachines.asm.lockscreenphonecallerASM.states;
import patternMatcher.events.asm.KeyguardLockCheckingASMEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.asm.lockscreenphonecallerASM.LockScreenPhoneCallerASMStates;
import taintanalyzer.TaintAnalyzer;

public class InitialState extends LockScreenPhoneCallerASMStates{

	private String currInstr="";
	private TaintAnalyzer ta;
	public InitialState(){}
	public InitialState(TaintAnalyzer ta){
		this.ta = ta;
	}

	@Override
	public State update(KeyguardLockCheckingASMEvent e) {
		return (State) new KeyguardLockCheckedState(this.ta);
	}

}
