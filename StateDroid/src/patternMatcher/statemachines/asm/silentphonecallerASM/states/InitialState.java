package patternMatcher.statemachines.asm.silentphonecallerASM.states;
import patternMatcher.events.asm.phonevolume.VoiceCallStreamDecreasingASMEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.asm.silentphonecallerASM.SilentPhoneCallerASMStates;
import taintanalyzer.TaintAnalyzer;


public class InitialState extends SilentPhoneCallerASMStates{

	private String currInstr="";
	private TaintAnalyzer ta;
	public InitialState(){}
	public InitialState(TaintAnalyzer ta){
		this.ta = ta;
	}

	@Override
	public State update(VoiceCallStreamDecreasingASMEvent e) {
		
//		System.out.println("VoiceCallStreamDecreasingEvent Received");
		
		return this;
	}

}
