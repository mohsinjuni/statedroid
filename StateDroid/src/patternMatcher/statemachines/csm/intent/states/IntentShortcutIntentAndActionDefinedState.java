package patternMatcher.statemachines.csm.intent.states;
import java.util.Hashtable;

import models.cfg.InstructionResponse;
import models.cfg.Register;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;
import patternMatcher.attackreporter.AttackReporter;
import patternMatcher.attackreporter.GenericReport;
import patternMatcher.events.csm.SendBroadcastEvent;
import patternMatcher.events.csm.filereading.DataOutputStreamWriteEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.csm.intent.IntentStates;
import taintanalyzer.TaintAnalyzer;
import configuration.Config;


public class IntentShortcutIntentAndActionDefinedState extends IntentStates{
	
	private TaintAnalyzer ta;
	private SymbolSpace localSymbolSpace;
	
	public IntentShortcutIntentAndActionDefinedState(TaintAnalyzer taParam)
	{
		this.ta = taParam;
		this.localSymbolSpace = Config.getInstance().getLocalSymbolSpace();
	}
	public IntentShortcutIntentAndActionDefinedState(){}
	
	
	@Override
	public State update(SendBroadcastEvent e) {
		InstructionResponse ir = (InstructionResponse) e.getEventInfo().get(InstructionResponse.CLASS_NAME);
		String instrText = ir.getInstr().getText();
		reportAttack(e, ir);
		return this;
	}
	public void reportAttack(SendBroadcastEvent e, InstructionResponse ir){
		String permStr = Config.getInstance().getCurrCFGPermutationString();

		GenericReport rep = new GenericReport();
		rep.setCompPkgName(ta.getCurrComponentPkgName());
		rep.setCompCallbackMethdName(ta.getCurrComponentCallback());
		rep.setCurrComponentClsName(ta.getCurrComponentName());

 		rep.setInstrContainerCls(ir.getInstr().getCurrPkgClassName());
 		rep.setInstContainerMthd(ir.getInstr().getCurrMethodName());
 		rep.setPermutationStr(permStr);
 		
 		String msg = "##### This app installs app shortcuts on the home screen \n\n";
 		rep.setMessage(msg);
 	    rep.setSinkAPI(ir.getInstr().getText());
 	    
 		if(!AttackReporter.getInstance().checkIfInstallAppShortcutsReportExists(rep)){
 			AttackReporter.getInstance().getInstallAppShortcutsReportList().add(rep);
 			rep.printReport();
 		}
	}

}
