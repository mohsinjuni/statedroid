package patternMatcher.statemachines.asm.ringermodesilencerASM.states;
import models.cfg.InstructionResponse;
import patternMatcher.attackreporter.AttackReporter;
import patternMatcher.attackreporter.GenericReport;
import patternMatcher.events.asm.phonevolume.RingerModeSilentASMEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.asm.ringermodesilencerASM.RingerModeSilencerASMStates;
import taintanalyzer.TaintAnalyzer;
import configuration.Config;

public class InitialState extends RingerModeSilencerASMStates{

	private String currInstr="";
	private TaintAnalyzer ta;
	public InitialState(){}
	public InitialState(TaintAnalyzer ta){
		this.ta = ta;
	}
	
	@Override
	public State update(RingerModeSilentASMEvent e) {

		GenericReport rep = new GenericReport();
		rep.setInstrContainerCls(e.getCurrPkgClsName());
		rep.setInstContainerMthd(e.getCurrMethodName());

		rep.setCompPkgName(ta.getCurrComponentPkgName());
		rep.setCompCallbackMethdName(ta.getCurrComponentCallback());
		rep.setCurrComponentClsName(ta.getCurrComponentName());

		String permStr = Config.getInstance().getCurrCFGPermutationString();
 		rep.setPermutationStr(permStr);
 		rep.setMessage(" ##### This app can silence your phone to suppress some notifications. ##########" +
 				"\n     It uses hidden APIs (through Java reflection) to silence the phone which makes this functionality more suspicious.\n");
 		
 		InstructionResponse ir = (InstructionResponse) e.getEventInfo().get(InstructionResponse.CLASS_NAME);
 	    rep.setSinkAPI(ir.getInstr().getText());
 	    
 		if(!AttackReporter.getInstance().checkIfRingerModeSilencerReportExists(rep))
 		{
 			AttackReporter.getInstance().getRingerModeSilencerReportList().add(rep);
 			rep.printReport();
 		}
		
		return this;
	}

}
