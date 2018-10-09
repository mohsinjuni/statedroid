package patternMatcher.statemachines.asm.audiomanagerASM.states;

import models.cfg.InstructionResponse;
import models.symboltable.SymbolTableEntry;
import patternMatcher.attackreporter.AttackReporter;
import patternMatcher.attackreporter.GenericReport;
import patternMatcher.events.asm.phonevolume.AudioManagerRingerModeOffASMEvent;
import patternMatcher.events.asm.phonevolume.AudioManagerStreamVolumeChangedASMEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.asm.audiomanagerASM.AudioManagerASMStates;
import taintanalyzer.TaintAnalyzer;
import configuration.Config;

public class InitialState extends AudioManagerASMStates {

	private TaintAnalyzer ta;

	public InitialState(TaintAnalyzer taParam) {
		this.ta = taParam;
	}

	public InitialState() {
	}

	@Override
	public State update(AudioManagerStreamVolumeChangedASMEvent e) {
		String permStr = Config.getInstance().getCurrCFGPermutationString();

		GenericReport rep = new GenericReport();

		rep.setCompPkgName(ta.getCurrComponentPkgName());
		rep.setCompCallbackMethdName(ta.getCurrComponentCallback());
		rep.setCurrComponentClsName(ta.getCurrComponentName());

		rep.setInstrContainerCls(e.getCurrPkgClsName());
		rep.setInstContainerMthd(e.getCurrMethodName());
		rep.setPermutationStr(permStr);

		SymbolTableEntry streamTypeEntry = (SymbolTableEntry) e.getEventInfo().get("streamType");
		SymbolTableEntry streamActionEntry = (SymbolTableEntry) e.getEventInfo().get("streamAction");

		String streamType = streamTypeEntry.getEntryDetails().getValue();
		String streamAction = streamActionEntry.getEntryDetails().getValue();

		String msg = "##### This app can modify the volume settings by \"" + streamAction + "\" of stream \"" + streamType + "\".\n";
		rep.setMessage(msg);

		InstructionResponse ir = (InstructionResponse) e.getEventInfo().get(InstructionResponse.CLASS_NAME);
		rep.setSinkAPI(ir.getInstr().getText());

		if (!AttackReporter.getInstance().checkIfAudioManagerStreamVolumeChangedReportExists(rep)) {
			AttackReporter.getInstance().getAudioManagerStreamVolumeChangedReportList().add(rep);
			rep.printReport();
		}

		return this;
	}

	@Override
	public State update(AudioManagerRingerModeOffASMEvent e) {
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

		//		ringerModeConstants.put("0", "makes the phone silent by setting ringer-mode to RINGER_MODE_SILENT");   
		//		ringerModeConstants.put("1", "makes the phone silent with vibration by setting ringer-mode to RINGER_MODE_VIBRATE");   
		//		ringerModeConstants.put("2", "sets phone's ringer mode to normal (audible) mode using RINGER_MODE_NORMAL.");   

		String msg = "##### This app sets ringer mode value to its OFF state \n";
		rep.setMessage(msg);

		InstructionResponse ir = (InstructionResponse) e.getEventInfo().get(InstructionResponse.CLASS_NAME);
		rep.setSinkAPI(ir.getInstr().getText());

		return this;
	}

}
