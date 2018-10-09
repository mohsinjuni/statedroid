package patternMatcher.statemachines.asm.audiovideorecorderASM.states;

import java.util.Enumeration;
import java.util.Hashtable;

import models.cfg.InstructionResponse;
import patternMatcher.attackreporter.AttackReporter;
import patternMatcher.attackreporter.AudioVideoRecorderReport;
import patternMatcher.events.asm.audiovideo.AudioRecorderASMEvent;
import patternMatcher.events.asm.audiovideo.VideoRecorderASMEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.asm.audiovideorecorderASM.AudioVideoRecorderASMStates;
import taintanalyzer.TaintAnalyzer;
import configuration.Config;

public class InitialState extends AudioVideoRecorderASMStates {

	private TaintAnalyzer ta;

	public InitialState(TaintAnalyzer taParam) {
		this.ta = taParam;
	}

	public InitialState() {
	}

	@Override
	public State update(AudioRecorderASMEvent e) {

		String permStr = Config.getInstance().getCurrCFGPermutationString();

		AudioVideoRecorderReport rep = new AudioVideoRecorderReport();
		rep.setCompPkgName(ta.getCurrComponentPkgName());
		rep.setCompCallbackMethdName(ta.getCurrComponentCallback());
		rep.setCurrComponentClsName(ta.getCurrComponentName());

		rep.setInstrContainerCls(e.getCurrPkgClsName());
		rep.setInstContainerMthd(e.getCurrMethodName());

		rep.setPermutationStr(permStr);
		String msg = "##### This app can record audio files with following settings. \n";

		Hashtable attackParams = (Hashtable) e.getEventInfo().get("attackParameters");
		Enumeration enumr = attackParams.keys();

		while (enumr.hasMoreElements()) {
			String key = (String) enumr.nextElement();
			String value = (String) attackParams.get(key);
			msg += key + " == " + value + " \n";
		}

		rep.setMessage(msg);

		InstructionResponse ir = (InstructionResponse) e.getEventInfo().get(InstructionResponse.CLASS_NAME);
		rep.setSinkAPI(ir.getInstr().getText());

		if (!AttackReporter.getInstance().checkIfAudioVideoRecorderReportExists(rep)) {
			AttackReporter.getInstance().getAudioVideoRecorderReportList().add(rep);
			rep.printReport();
		}

		return (State) new InitialState(ta);
	}

	@Override
	public State update(VideoRecorderASMEvent e) {

		String permStr = Config.getInstance().getCurrCFGPermutationString();

		AudioVideoRecorderReport rep = new AudioVideoRecorderReport();
		rep.setInstrContainerCls(e.getCurrPkgClsName());
		rep.setInstContainerMthd(e.getCurrMethodName());

		rep.setCompPkgName(ta.getCurrComponentPkgName());
		rep.setCompCallbackMethdName(ta.getCurrComponentCallback());
		rep.setCurrComponentClsName(ta.getCurrComponentName());

		rep.setPermutationStr(permStr);
		String msg = "##### This app can record video files with following settings. \n";

		Hashtable attackParams = (Hashtable) e.getEventInfo().get("attackParameters");
		Enumeration enumr = attackParams.keys();

		while (enumr.hasMoreElements()) {
			String key = (String) enumr.nextElement();
			String value = (String) attackParams.get(key);
			msg += key + " == " + value + " \n";
		}

		rep.setMessage(msg);

		InstructionResponse ir = (InstructionResponse) e.getEventInfo().get(InstructionResponse.CLASS_NAME);
		rep.setSinkAPI(ir.getInstr().getText());

		if (!AttackReporter.getInstance().checkIfAudioVideoRecorderReportExists(rep)) {
			AttackReporter.getInstance().getAudioVideoRecorderReportList().add(rep);
			rep.printReport();
		}

		return (State) new InitialState(ta);
	}

}
