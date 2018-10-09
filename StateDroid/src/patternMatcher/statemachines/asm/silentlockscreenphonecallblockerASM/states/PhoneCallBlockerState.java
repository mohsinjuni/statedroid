package patternMatcher.statemachines.asm.silentlockscreenphonecallblockerASM.states;
import models.cfg.InstructionResponse;
import patternMatcher.attackreporter.AttackReporter;
import patternMatcher.attackreporter.SilentPhoneCallBlockerReport;
import patternMatcher.events.asm.phonevolume.RingerModeNormalASMEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.asm.silentlockscreenphonecallblockerASM.SilentLockScreenPhoneCallBlockerASMStates;
import taintanalyzer.TaintAnalyzer;
import configuration.Config;

public class PhoneCallBlockerState extends SilentLockScreenPhoneCallBlockerASMStates{

	private String currInstr="";
	private TaintAnalyzer ta;
	public PhoneCallBlockerState(){}
	public PhoneCallBlockerState(TaintAnalyzer ta){
		this.ta = ta;
	}

	@Override
	public State update(RingerModeNormalASMEvent e) {
		
		SilentPhoneCallBlockerReport rep = new SilentPhoneCallBlockerReport();
		rep.setInstrContainerCls(e.getCurrPkgClsName());
		rep.setInstContainerMthd(e.getCurrMethodName());

		rep.setCompPkgName(ta.getCurrComponentPkgName());
		rep.setCompCallbackMethdName(ta.getCurrComponentCallback());
		rep.setCurrComponentClsName(ta.getCurrComponentName());
		
		String permStr = Config.getInstance().getCurrCFGPermutationString();
 		rep.setPermutationStr(permStr);
 		rep.setMessage(" [ATK] ##### This app can reduce volume, perform checks for device lock, block incoming calls and then restore the volume.::");
 		
 		InstructionResponse ir = (InstructionResponse) e.getEventInfo().get(InstructionResponse.CLASS_NAME);
 	    rep.setSinkAPI(ir.getInstr().getText());
 	    
 		if(!AttackReporter.getInstance().checkIfSilentPhoneCallBlockerExists(rep)){
 			AttackReporter.getInstance().getSilentPhoneCallBlockerReportList().add(rep);
 			rep.printReport();
 		}
		return (State) new InitialState(ta);
	}

}
