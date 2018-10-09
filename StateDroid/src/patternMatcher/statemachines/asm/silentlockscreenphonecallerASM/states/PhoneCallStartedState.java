package patternMatcher.statemachines.asm.silentlockscreenphonecallerASM.states;
import models.cfg.InstructionResponse;
import patternMatcher.attackreporter.AttackReporter;
import patternMatcher.attackreporter.GenericReport;
import patternMatcher.events.asm.phonecall.PhoneCallBlockingASMEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.asm.silentlockscreenphonecallerASM.SilentLockScreenPhoneCallerASMStates;
import taintanalyzer.TaintAnalyzer;
import configuration.Config;

public class PhoneCallStartedState extends SilentLockScreenPhoneCallerASMStates{

	private String currInstr="";
	private TaintAnalyzer ta;
	public PhoneCallStartedState(){}
	public PhoneCallStartedState(TaintAnalyzer ta){
		this.ta = ta;
	}

	@Override
	public State update(PhoneCallBlockingASMEvent e) {
		
		GenericReport rep = new GenericReport();
		rep.setInstrContainerCls(e.getCurrPkgClsName());
		rep.setInstContainerMthd(e.getCurrMethodName());

		rep.setCompPkgName(ta.getCurrComponentPkgName());
		rep.setCompCallbackMethdName(ta.getCurrComponentCallback());
		rep.setCurrComponentClsName(ta.getCurrComponentName());
		
		String permStr = Config.getInstance().getCurrCFGPermutationString();
 		rep.setPermutationStr(permStr);
 		rep.setMessage(" [ATK] ##### This app checks if screen is locked or not, start a phone call and then disconnect the phone call.::");
 		
 		InstructionResponse ir = (InstructionResponse) e.getEventInfo().get(InstructionResponse.CLASS_NAME);
 	    rep.setSinkAPI(ir.getInstr().getText());
 	    
 		if(!AttackReporter.getInstance().checkIfSilentLockScreenPhoneCallerReportExists(rep)){
 			AttackReporter.getInstance().getSilentLockScreenPhoneCallerReportList().add(rep);
 			rep.printReport();
 		}
		return (State) new InitialState(ta);
	}

}
