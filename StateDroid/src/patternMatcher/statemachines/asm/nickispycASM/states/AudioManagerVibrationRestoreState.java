package patternMatcher.statemachines.asm.nickispycASM.states;

import models.cfg.InstructionResponse;
import configuration.Config;
import patternMatcher.attackreporter.AttackReporter;
import patternMatcher.attackreporter.GenericReport;
import patternMatcher.events.asm.contentprovider.ContentProviderDeletionASMEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.asm.nickispycASM.NickiSpyCASMStates;
import taintanalyzer.TaintAnalyzer;

public class AudioManagerVibrationRestoreState extends NickiSpyCASMStates {

	private TaintAnalyzer ta;

	public AudioManagerVibrationRestoreState(TaintAnalyzer taParam) {
		this.ta = taParam;
	}

	public AudioManagerVibrationRestoreState() {
	}
	
	@Override
	public State update(ContentProviderDeletionASMEvent e) {
		generateReport(e);
		return new InitialState(this.ta);
	}

	public void generateReport(ContentProviderDeletionASMEvent e){
		String permStr = Config.getInstance().getCurrCFGPermutationString();
		
		GenericReport rep = new GenericReport();
		rep.setCompPkgName(ta.getCurrComponentPkgName());
		rep.setCompCallbackMethdName(ta.getCurrComponentCallback());
		rep.setCurrComponentClsName(ta.getCurrComponentName());
 		rep.setInstrContainerCls(e.getCurrPkgClsName());
 		rep.setInstContainerMthd(e.getCurrMethodName());
					
 		rep.setPermutationStr(permStr);
 		
 		String msg = " [ATK] ##### This app can launch an attack with following actions performed in the given order.\n\n " +
 				" \t(1) Setting AudioMgr ringer mode to silent mode => (2) Set device vibration off \n\t" +
 				" => (3)  Hide call-screen dial pad using Java reflection => (4) ANSWER phone call using Java reflection \n\t" +
 				"=> (5) Show home screen => (6) Disconnect answered phone call \n\t" +
 				"==> (7) Restore ringer-mode setting ==> (8) Restore vibrate setting \n\t" +
 				"(9) Delete call logs";
 		rep.setMessage(msg);
 		
 		InstructionResponse ir = (InstructionResponse) e.getEventInfo().get(InstructionResponse.CLASS_NAME);
 	    rep.setSinkAPI(ir.getInstr().getText());
 	    
 		if(!AttackReporter.getInstance().checkIfNickiSpyCASMReportExists(rep)){
 			AttackReporter.getInstance().getNickiSpyCASMReportList().add(rep);
 			rep.printReport();
 		}

	}

}
