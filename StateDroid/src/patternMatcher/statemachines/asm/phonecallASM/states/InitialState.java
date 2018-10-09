package patternMatcher.statemachines.asm.phonecallASM.states;
import models.cfg.InstructionResponse;
import patternMatcher.attackreporter.AttackReporter;
import patternMatcher.attackreporter.GenericReport;
import patternMatcher.attackreporter.PhoneCallBlockerReport;
import patternMatcher.attackreporter.PhoneCallerReport;
import patternMatcher.events.asm.phonecall.PhoneCallAnsweringASMEvent;
import patternMatcher.events.asm.phonecall.PhoneCallBlockingASMEvent;
import patternMatcher.events.asm.phonecall.PhoneCallingASMEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.asm.phonecallASM.PhoneCallASMStates;
import taintanalyzer.TaintAnalyzer;
import configuration.Config;


public class InitialState extends PhoneCallASMStates{

	private String currInstr="";
	private TaintAnalyzer ta;
	public InitialState(){}
	public InitialState(TaintAnalyzer ta){
		this.ta = ta;
	}

	@Override
	public State update(PhoneCallingASMEvent e) {
		
//		System.out.println("PhoneCallingEvent Received");

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
 		rep.setMessage(" ##### This app can make phone calls to this number:: [phoneNo] = " + phoneNo);
 		
 		InstructionResponse ir = (InstructionResponse) e.getEventInfo().get(InstructionResponse.CLASS_NAME);
 	    rep.setSinkAPI(ir.getInstr().getText());
 	    
 		if(!AttackReporter.getInstance().checkIfPhoneCallerReportExists(rep))
 		{
 			AttackReporter.getInstance().getPhoneCallerReportList().add(rep);
 			rep.printReport();
 		}


	 return this;
	}

	@Override
	public State update(PhoneCallBlockingASMEvent e) {
		
//		System.out.println("PhoneBlockingEvent Received");
		
		PhoneCallBlockerReport rep = new PhoneCallBlockerReport();
		rep.setInstrContainerCls(e.getCurrPkgClsName());
		rep.setInstContainerMthd(e.getCurrMethodName());

		rep.setCompPkgName(ta.getCurrComponentPkgName());
		rep.setCompCallbackMethdName(ta.getCurrComponentCallback());
		rep.setCurrComponentClsName(ta.getCurrComponentName());
		
		String permStr = Config.getInstance().getCurrCFGPermutationString();
 		rep.setPermutationStr(permStr);
 		rep.setMessage(" ##### This app can block incoming phone calls.::");
 		
 		InstructionResponse ir = (InstructionResponse) e.getEventInfo().get(InstructionResponse.CLASS_NAME);
 	    rep.setSinkAPI(ir.getInstr().getText());
 	    
 		if(!AttackReporter.getInstance().checkIfPhoneCallBlockerExists(rep))
 		{
 			AttackReporter.getInstance().getPhoneCallBlockerReportList().add(rep);
 			rep.printReport();
 		}

		return this;
	}
	@Override
	public State update(PhoneCallAnsweringASMEvent e) {
		
//		System.out.println("PhoneBlockingEvent Received");
		
		GenericReport rep = new GenericReport();
		rep.setInstrContainerCls(e.getCurrPkgClsName());
		rep.setInstContainerMthd(e.getCurrMethodName());
		rep.setCompPkgName(e.getCurrComponentPkgName() + "/" + e.getCurrComponentName());
		rep.setCompCallbackMethdName(e.getCurrCompCallbackMethodName());
		
		String permStr = Config.getInstance().getCurrCFGPermutationString();
 		rep.setPermutationStr(permStr);
 		rep.setMessage(" ##### This app can answer phone calls.::");
 		
 		InstructionResponse ir = (InstructionResponse) e.getEventInfo().get(InstructionResponse.CLASS_NAME);
 	    rep.setSinkAPI(ir.getInstr().getText());
 	    
 		if(!AttackReporter.getInstance().checkIfPhoneCallAnswererReportExists(rep))
 		{
 			AttackReporter.getInstance().getPhoneCallAnswererReportList().add(rep);
 			rep.printReport();
 		}

		return this;
	}
}
