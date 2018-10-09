package patternMatcher.statemachines.asm.silentphonecallblockerASM.states;
import models.cfg.InstructionResponse;
import patternMatcher.attackreporter.AttackReporter;
import patternMatcher.attackreporter.PhoneCallBlockerReport;
import patternMatcher.events.asm.phonecall.PhoneCallBlockingASMEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.asm.silentphonecallblockerASM.SilentPhoneCallBlockerASMStates;
import taintanalyzer.TaintAnalyzer;
import configuration.Config;

public class RingerModeSilentState extends SilentPhoneCallBlockerASMStates{

	private String currInstr="";
	private TaintAnalyzer ta;
	public RingerModeSilentState(){}
	public RingerModeSilentState(TaintAnalyzer ta){
		this.ta = ta;
	}

	@Override
	public State update(PhoneCallBlockingASMEvent e) {
		
		return (State) new PhoneCallBlockerState(this.ta);
	}

}
