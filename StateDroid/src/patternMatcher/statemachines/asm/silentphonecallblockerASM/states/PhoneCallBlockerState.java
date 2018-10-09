package patternMatcher.statemachines.asm.silentphonecallblockerASM.states;
import models.cfg.InstructionResponse;
import patternMatcher.attackreporter.AttackReporter;
import patternMatcher.attackreporter.SilentPhoneCallBlockerReport;
import patternMatcher.events.asm.phonevolume.AudioManagerRingerModeOnASMEvent;
import patternMatcher.events.asm.phonevolume.RingerModeNormalASMEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.asm.silentphonecallblockerASM.SilentPhoneCallBlockerASMStates;
import taintanalyzer.TaintAnalyzer;
import configuration.Config;

public class PhoneCallBlockerState extends SilentPhoneCallBlockerASMStates{

	private String currInstr="";
	private TaintAnalyzer ta;
	public PhoneCallBlockerState(){}
	public PhoneCallBlockerState(TaintAnalyzer ta){
		this.ta = ta;
	}

	@Override
	public State update(AudioManagerRingerModeOnASMEvent e) {
		
//		System.out.println("PhoneBlockingEvent Received");
		
		SilentPhoneCallBlockerReport rep = new SilentPhoneCallBlockerReport();
		rep.setInstrContainerCls(e.getCurrPkgClsName());
		rep.setInstContainerMthd(e.getCurrMethodName());

		rep.setCompPkgName(ta.getCurrComponentPkgName());
		rep.setCompCallbackMethdName(ta.getCurrComponentCallback());
		rep.setCurrComponentClsName(ta.getCurrComponentName());
		
		String permStr = Config.getInstance().getCurrCFGPermutationString();
 		rep.setPermutationStr(permStr);
 		rep.setMessage(" [ATK] ##### This app can reduce volume, block incoming calls and then restore the volume.::");
 		
 		InstructionResponse ir = (InstructionResponse) e.getEventInfo().get(InstructionResponse.CLASS_NAME);
 	    rep.setSinkAPI(ir.getInstr().getText());
 	    
 		if(!AttackReporter.getInstance().checkIfSilentPhoneCallBlockerExists(rep))
 		{
 			AttackReporter.getInstance().getSilentPhoneCallBlockerReportList().add(rep);
 			rep.printReport();
 		}

		return (State) new InitialState(ta);
	}

}
