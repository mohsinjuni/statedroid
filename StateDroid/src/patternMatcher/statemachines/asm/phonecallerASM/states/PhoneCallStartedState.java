package patternMatcher.statemachines.asm.phonecallerASM.states;
import models.cfg.InstructionResponse;
import patternMatcher.attackreporter.AttackReporter;
import patternMatcher.attackreporter.GenericReport;
import patternMatcher.attackreporter.PhoneCallBlockerReport;
import patternMatcher.attackreporter.PhoneCallerReport;
import patternMatcher.events.asm.phonecall.PhoneCallBlockingASMEvent;
import patternMatcher.events.asm.phonecall.PhoneCallingASMEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.asm.phonecallerASM.PhoneCallerASMStates;
import taintanalyzer.TaintAnalyzer;
import configuration.Config;


public class PhoneCallStartedState extends PhoneCallerASMStates{

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
 		rep.setMessage("[ATK] This app can start a phone call and then disconnect a phone call.::");
 		
 		InstructionResponse ir = (InstructionResponse) e.getEventInfo().get(InstructionResponse.CLASS_NAME);
 	    rep.setSinkAPI(ir.getInstr().getText());
 	    
 		if(!AttackReporter.getInstance().checkIfPhoneCallStartAndEndReportExists(rep)){
 			AttackReporter.getInstance().getPhoneCallStartAndEndReportList().add(rep);
 			rep.printReport();
 		}
		return this;
	}

}
