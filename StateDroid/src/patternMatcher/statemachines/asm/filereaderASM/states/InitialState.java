package patternMatcher.statemachines.asm.filereaderASM.states;

import patternMatcher.attackreporter.GenericReport;
import patternMatcher.events.asm.FileReaderASMEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.asm.filereaderASM.FileReaderASMStates;
import taintanalyzer.TaintAnalyzer;
import configuration.Config;

public class InitialState extends FileReaderASMStates {

	private TaintAnalyzer ta;

	public InitialState(TaintAnalyzer taParam) {
		this.ta = taParam;
	}

	public InitialState() {
	}

	@Override
	public State update(FileReaderASMEvent e) {
		String permStr = Config.getInstance().getCurrCFGPermutationString();

		GenericReport rep = new GenericReport();
		rep.setInstrContainerCls(e.getCurrPkgClsName());
		rep.setInstContainerMthd(e.getCurrMethodName());
		rep.setCompPkgName(e.getCurrComponentPkgName());
		rep.setCompCallbackMethdName(e.getCurrCompCallbackMethodName());
		rep.setCurrComponentClsName(e.getCurrComponentName());
		rep.setInstrContainerCls(e.getCurrPkgClsName());
		rep.setInstContainerMthd(e.getCurrMethodName());
		rep.setPermutationStr(permStr);

		return this;
	}

}
