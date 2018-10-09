package patternMatcher.statemachines.asm.phonecallforwardingASM.states;
import models.cfg.InstructionResponse;
import patternMatcher.attackreporter.AttackReporter;
import patternMatcher.attackreporter.PhoneCallForwardingReport;
import patternMatcher.attackreporter.PhoneCallerReport;
import patternMatcher.events.asm.phonecall.PhoneCallForwardingASMEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.asm.phonecallforwardingASM.PhoneCallForwardingASMStates;
import taintanalyzer.TaintAnalyzer;
import configuration.Config;


public class InitialState extends PhoneCallForwardingASMStates{

	private String currInstr="";
	
	private TaintAnalyzer ta;
	public InitialState(){}
	public InitialState(TaintAnalyzer ta){
		this.ta = ta;
	}
	
	@Override
	public State update(PhoneCallForwardingASMEvent e) {
		
		String permStr = Config.getInstance().getCurrCFGPermutationString();
 		String callForwardingCode = (String) e.getEventInfo().get("callForwardingCode");
		
		PhoneCallForwardingReport rep = new PhoneCallForwardingReport();
		rep.setInstrContainerCls(e.getCurrPkgClsName());
		rep.setInstContainerMthd(e.getCurrMethodName());
		rep.setCompPkgName(ta.getCurrComponentPkgName());
		rep.setCompCallbackMethdName(ta.getCurrComponentCallback());
		rep.setCurrComponentClsName(ta.getCurrComponentName());

		rep.setCallForwardingCode(callForwardingCode);
 		rep.setPermutationStr(permStr);
 		rep.setMessage(" ##### This app can forward incoming phone calls using this code ::  " + callForwardingCode);
 		
 		InstructionResponse ir = (InstructionResponse) e.getEventInfo().get(InstructionResponse.CLASS_NAME);
 	    rep.setSinkAPI(ir.getInstr().getText());
 	    
 		if(!AttackReporter.getInstance().checkIfPhoneCallForwardingReportExists(rep)){
 			AttackReporter.getInstance().getPhoneCallForwardingReportList().add(rep);
 			rep.printReport();
 		}
		return this;
	}

}
