package patternMatcher.statemachines.asm.silentphonecallerASM.states;
import models.cfg.InstructionResponse;
import patternMatcher.attackreporter.AttackReporter;
import patternMatcher.attackreporter.PhoneCallerReport;
import patternMatcher.events.asm.phonecall.PhoneCallingASMEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.asm.silentphonecallerASM.SilentPhoneCallerASMStates;
import taintanalyzer.TaintAnalyzer;
import configuration.Config;


public class VoiceCallStreamDecreasedState extends SilentPhoneCallerASMStates{

	private String currInstr="";
	private TaintAnalyzer ta;
	public VoiceCallStreamDecreasedState(){}
	public VoiceCallStreamDecreasedState(TaintAnalyzer ta){
		this.ta = ta;
	}

	@Override
	public State update(PhoneCallingASMEvent e) {
		
		String permStr = Config.getInstance().getCurrCFGPermutationString();

 		String phoneNo = (String) e.getEventInfo().get("phoneNo");
		
		PhoneCallerReport rep = new PhoneCallerReport();
		rep.setInstrContainerCls(e.getCurrPkgClsName());
		rep.setInstContainerMthd(e.getCurrMethodName());

		rep.setCompPkgName(ta.getCurrComponentPkgName());
		rep.setCompCallbackMethdName(ta.getCurrComponentCallback());
		rep.setCurrComponentClsName(ta.getCurrComponentName());
		
 		rep.setPhoneNo(phoneNo);
 		rep.setPermutationStr(permStr);
 		rep.setMessage(" [ATK] ##### This app reduces voice-call volum down AND then makes call to this number:: [phoneNo] = " + phoneNo);
 		
 		InstructionResponse ir = (InstructionResponse) e.getEventInfo().get("instrResponse");
 	    rep.setSinkAPI(ir.getInstr().getText());
 	    
 		if(!AttackReporter.getInstance().checkIfSilentPhoneCallerExists(rep))
 		{
 			AttackReporter.getInstance().getSilentPhoneCallerReportList().add(rep);
 			rep.printReport();
 		}


 		return (State) new InitialState(ta);
	}
}
