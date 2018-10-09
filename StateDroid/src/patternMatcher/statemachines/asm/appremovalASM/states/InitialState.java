package patternMatcher.statemachines.asm.appremovalASM.states;

import models.cfg.InstructionResponse;
import models.symboltable.SymbolTableEntry;
import patternMatcher.attackreporter.AttackReporter;
import patternMatcher.attackreporter.GenericReport;
import patternMatcher.events.asm.appremoval.AppRemovalASMEvent;
import patternMatcher.events.asm.phonevolume.AudioManagerRingerModeOffASMEvent;
import patternMatcher.events.asm.phonevolume.AudioManagerStreamVolumeChangedASMEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.asm.appremovalASM.AppRemovalASMStates;
import patternMatcher.statemachines.asm.audiomanagerASM.AudioManagerASMStates;
import taintanalyzer.TaintAnalyzer;
import configuration.Config;

public class InitialState extends AppRemovalASMStates {

	private TaintAnalyzer ta;

	public InitialState(TaintAnalyzer taParam) {
		this.ta = taParam;
	}

	public InitialState() {
	}

	@Override
	public State update(AppRemovalASMEvent e) {

		String permStr = Config.getInstance().getCurrCFGPermutationString();

		GenericReport rep = new GenericReport();

		rep.setCompPkgName(ta.getCurrComponentPkgName());
		rep.setCompCallbackMethdName(ta.getCurrComponentCallback());
		rep.setCurrComponentClsName(ta.getCurrComponentName());

		rep.setInstrContainerCls(e.getCurrPkgClsName());
		rep.setInstContainerMthd(e.getCurrMethodName());

		rep.setPermutationStr(permStr);

		String msg = "##### This app can hide itself by removing its app icon from the launcher menu.\n\n";
		rep.setMessage(msg);

		InstructionResponse ir = (InstructionResponse) e.getEventInfo().get(InstructionResponse.CLASS_NAME);
		rep.setSinkAPI(ir.getInstr().getText());

		if (!AttackReporter.getInstance().checkIfAppRemovalReportExists(rep)) {
			AttackReporter.getInstance().getAppRemovalReportList().add(rep);
			rep.printReport();
		}

		return this;
	}
}
