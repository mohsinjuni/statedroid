package patternMatcher.statemachines.csm.audiomanager.states;

import java.util.Hashtable;

import models.cfg.InstructionResponse;
import models.cfg.Register;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;
import patternMatcher.attackreporter.AttackReporter;
import patternMatcher.attackreporter.GenericReport;
import patternMatcher.events.Event;
import patternMatcher.events.EventFactory;
import patternMatcher.events.asm.phonevolume.AudioManagerRingerModeOffASMEvent;
import patternMatcher.events.asm.phonevolume.AudioManagerRingerModeOnASMEvent;
import patternMatcher.events.asm.phonevolume.AudioManagerStreamVolumeChangedASMEvent;
import patternMatcher.events.asm.phonevolume.AudioManagerVibrationOffASMEvent;
import patternMatcher.events.asm.phonevolume.AudioManagerVibrationOnASMEvent;
import patternMatcher.events.asm.phonevolume.AudioManagerVibrationRestoreASMEvent;
import patternMatcher.events.csm.SetRingerModeEvent;
import patternMatcher.events.csm.audiomanager.AudioMgrSetVibrateSettingEvent;
import patternMatcher.events.csm.audiomanager.SetStreamVolumeEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.csm.appremoval.AppRemovalStates;
import taintanalyzer.TaintAnalyzer;
import configuration.Config;
import enums.Constants;

public class InitialState extends AppRemovalStates {

	TaintAnalyzer ta;
	private SymbolSpace localSymSpace = Config.getInstance().getLocalSymbolSpace();

	public InitialState(TaintAnalyzer taParam) {
		this.ta = taParam;
	}

	public InitialState() {
	}

	@Override
	public State update(SetStreamVolumeEvent e) {

		InstructionResponse ir = (InstructionResponse) e.getEventInfo().get(InstructionResponse.CLASS_NAME);
		Register audioMgrReg = ir.getInvolvedRegisters().get(0);
		SymbolTableEntry audioMgrEntry = this.localSymSpace.find(audioMgrReg.getName());

		if (audioMgrEntry != null) {
			Hashtable recordFieldList = audioMgrEntry.getEntryDetails().getRecordFieldList();
			if (recordFieldList != null && recordFieldList.size() > 0) {
				String streamType = "";
				String streamOperation = "";
				String flag = "";
				SymbolTableEntry streamTypeEntry = (SymbolTableEntry) recordFieldList.get("streamType");
				if (streamTypeEntry != null) {
					streamType = streamTypeEntry.getEntryDetails().getValue();
				}
				SymbolTableEntry streamOperationEntry = (SymbolTableEntry) recordFieldList.get("streamOperation");
				if (streamOperationEntry != null) {
					streamOperation = streamOperationEntry.getEntryDetails().getValue();
				}
				SymbolTableEntry flagEntry = (SymbolTableEntry) recordFieldList.get("flag");
				if (flagEntry != null) {
					flag = flagEntry.getEntryDetails().getValue();
				}
				//Many events can be generated using this combination of three-string values.
				if (!streamType.isEmpty() && !streamOperation.isEmpty()) {
					Hashtable streamTypes = Constants.getInstance().getAudioManagerStreamTypes();
					Hashtable streamActions = Constants.getInstance().getAudioManagerStreamActions();
					if (streamTypes.containsKey(streamType.trim())) {
						String streamTypeConstant = (String) streamTypes.get(streamType);
						streamTypeEntry.getEntryDetails().setValue(streamTypeConstant);

						String streamActionConstant = "";
						if (streamActions.containsKey(streamOperation.trim())) {
							streamActionConstant = (String) streamActions.get(streamOperation);
						} else {
							streamActionConstant = "changing volume to " + streamOperationEntry.getEntryDetails().getValue();
						}
						streamOperationEntry.getEntryDetails().setValue(streamActionConstant);

						EventFactory.getInstance().registerEvent("audioManagerStreamVolumeChangedEvent",
								new AudioManagerStreamVolumeChangedASMEvent());
						Event audioManagerStreamVolumeChangedEvent = EventFactory.getInstance().createEvent(
								"audioManagerStreamVolumeChangedEvent");
						audioManagerStreamVolumeChangedEvent.setName("audioManagerStreamVolumeChangedEvent");

						audioManagerStreamVolumeChangedEvent.setCurrMethodName(ir.getInstr().getCurrMethodName());
						audioManagerStreamVolumeChangedEvent.setCurrPkgClsName(ir.getInstr().getCurrPkgClassName());

						audioManagerStreamVolumeChangedEvent.setCurrComponentPkgName(ta.getCurrComponentPkgName());
						audioManagerStreamVolumeChangedEvent.setCurrComponentName(ta.getCurrComponentName());
						audioManagerStreamVolumeChangedEvent.setCurrCompCallbackMethodName(ta.getCurrComponentCallback());

						audioManagerStreamVolumeChangedEvent.getEventInfo().put(InstructionResponse.CLASS_NAME, ir);
						audioManagerStreamVolumeChangedEvent.getEventInfo().put("streamType", streamTypeEntry);
						audioManagerStreamVolumeChangedEvent.getEventInfo().put("streamAction", streamOperationEntry);

						ta.setCurrASMEvent(audioManagerStreamVolumeChangedEvent);
					}
				}
			}
		}
		return this; //This does not matter anymore. Each object maintains its state itself.
	}

	@Override
	public State update(SetRingerModeEvent e) {

		InstructionResponse ir = (InstructionResponse) e.getEventInfo().get(InstructionResponse.CLASS_NAME);
		Register audioMgrReg = ir.getInvolvedRegisters().get(0);
		SymbolTableEntry audioMgrEntry = this.localSymSpace.find(audioMgrReg.getName());

		if (audioMgrEntry != null) {
			Hashtable recordFieldList = audioMgrEntry.getEntryDetails().getRecordFieldList();
			if (recordFieldList != null && recordFieldList.size() > 0) {
				String ringerModeValue = "";
				SymbolTableEntry ringerModeEntry = (SymbolTableEntry) recordFieldList.get("ringerModeEntry");
				if (ringerModeEntry != null) {
					ringerModeValue = ringerModeEntry.getEntryDetails().getValue();
				}
				//		ringerModeConstants.put("0", "makes the phone silent by setting ringer-mode to RINGER_MODE_SILENT");   
				//		ringerModeConstants.put("1", "makes the phone silent with vibration by setting ringer-mode to RINGER_MODE_VIBRATE");   
				//		ringerModeConstants.put("2", "sets phone's ringer mode to normal (audible) mode using RINGER_MODE_NORMAL.");   

				if (!ringerModeValue.isEmpty()) {
					String eventName = "";
					String msg = "";
					int ringerMode = -1;
					if (ringerModeValue.contains("1") || ringerModeValue.contains("existing")) {
						ringerMode = 1;
						msg = "##### This app can turn ON AudioManger.ringerMode. \n\n";
						eventName = "audioManagerRingerModeOnASMEvent";
						EventFactory.getInstance().registerEvent(eventName, new AudioManagerRingerModeOnASMEvent());
					} else {
						ringerMode = 0;
						msg = "##### This app can turn OFF AudioManger.ringerMode. \n\n";
						eventName = "audioManagerRingerModeOffASMEvent";
						EventFactory.getInstance().registerEvent(eventName, new AudioManagerRingerModeOffASMEvent());
					}
					Event event = EventFactory.getInstance().createEvent(eventName);
					event.setName(eventName);
					event.setCurrMethodName(ir.getInstr().getCurrMethodName());
					event.setCurrPkgClsName(ir.getInstr().getCurrPkgClassName());
					event.setCurrComponentPkgName(ta.getCurrComponentPkgName());
					event.setCurrComponentName(ta.getCurrComponentName());
					event.setCurrCompCallbackMethodName(ta.getCurrComponentCallback());

					event.getEventInfo().put(InstructionResponse.CLASS_NAME, ir);
					//						event.getEventInfo().put("ringerModeEntry", ringerModeEntry);
					reportRingerModeAttack(ir, msg, ringerMode);
					ta.setCurrASMEvent(event);
				}
			}
		}
		return this; //This does not matter anymore. Each object maintains its state itself.
	}

	public void reportRingerModeAttack(InstructionResponse ir, String msg, int ringerMode) {
		String permStr = Config.getInstance().getCurrCFGPermutationString();

		GenericReport rep = new GenericReport();
		rep.setCompPkgName(ta.getCurrComponentPkgName());
		rep.setCompCallbackMethdName(ta.getCurrComponentCallback());
		rep.setCurrComponentClsName(ta.getCurrComponentName());

		rep.setInstrContainerCls(ir.getInstr().getCurrPkgClassName());
		rep.setInstContainerMthd(ir.getInstr().getCurrMethodName());
		rep.setPermutationStr(permStr);
		rep.setMessage(msg);
		rep.setSinkAPI(ir.getInstr().getText());

		if (ringerMode == 1) {
			if (!AttackReporter.getInstance().checkIfAudioManagerRingerModeOnReportExists(rep)) {
				AttackReporter.getInstance().getAudioManagerRingerModeOnReportList().add(rep);
				rep.printReport();
			}
		} else if (ringerMode == 0) {
			if (!AttackReporter.getInstance().checkIfAudioManagerRingerModeOffReportExists(rep)) {
				AttackReporter.getInstance().getAudioManagerRingerModeOffReportList().add(rep);
				rep.printReport();
			}
		}
	}

	@Override
	public State update(AudioMgrSetVibrateSettingEvent e) {

		InstructionResponse ir = (InstructionResponse) e.getEventInfo().get(InstructionResponse.CLASS_NAME);
		Register audioMgrReg = ir.getInvolvedRegisters().get(0);
		SymbolTableEntry audioMgrEntry = this.localSymSpace.find(audioMgrReg.getName());

		if (audioMgrEntry != null) {
			Hashtable recordFieldList = audioMgrEntry.getEntryDetails().getRecordFieldList();
			if (recordFieldList != null && recordFieldList.size() > 0) {
				String vibrateSettingValue = "";
				SymbolTableEntry vibrateSettingEntry = (SymbolTableEntry) recordFieldList.get("vibrateSettingEntry");
				if (vibrateSettingEntry != null) {
					vibrateSettingValue = vibrateSettingEntry.getEntryDetails().getValue();
				}

				if (!vibrateSettingValue.isEmpty()) {
					String eventName = "";
					String msg = "";
					int vibrateSetting = -1;
					if (vibrateSettingValue.equalsIgnoreCase("1")) {
						vibrateSetting = 1;
						msg = "##### This app can turn ON AudioManger.vibration setting. \n\n";
						eventName = "audioManagerVibrationOnASMEvent";
						EventFactory.getInstance().registerEvent(eventName, new AudioManagerVibrationOnASMEvent());
					} else if (vibrateSettingValue.contains("0") || vibrateSettingValue.contains("2")) {
						vibrateSetting = 0;
						msg = "##### This app can turn OFF AudioManger.vibration setting. \n\n";
						eventName = "audioManagerVibrationOffASMEvent";
						EventFactory.getInstance().registerEvent(eventName, new AudioManagerVibrationOffASMEvent());
					} else {
						vibrateSetting = 3;
						msg = "##### This app can restore AudioManger.vibration setting to its existing value. \n\n";
						eventName = "audioManagerVibrationRestoreASMEvent";
						EventFactory.getInstance().registerEvent(eventName, new AudioManagerVibrationRestoreASMEvent());
					}
					Event event = EventFactory.getInstance().createEvent(eventName);
					event.setName(eventName);

					event.setCurrMethodName(ir.getInstr().getCurrMethodName());
					event.setCurrPkgClsName(ir.getInstr().getCurrPkgClassName());
					event.setCurrComponentPkgName(ta.getCurrComponentPkgName());
					event.setCurrComponentName(ta.getCurrComponentName());
					event.setCurrCompCallbackMethodName(ta.getCurrComponentCallback());

					event.getEventInfo().put(InstructionResponse.CLASS_NAME, ir);
					reportVibrateSettingAttack(ir, msg, vibrateSetting);

					ta.setCurrASMEvent(event);
				}
			}
		}
		return this; //This does not matter anymore. Each object maintains its state itself.
	}

	public void reportVibrateSettingAttack(InstructionResponse ir, String msg, int vibrateSetting) {
		String permStr = Config.getInstance().getCurrCFGPermutationString();

		GenericReport rep = new GenericReport();
		rep.setCompPkgName(ta.getCurrComponentPkgName());
		rep.setCompCallbackMethdName(ta.getCurrComponentCallback());
		rep.setCurrComponentClsName(ta.getCurrComponentName());

		rep.setInstrContainerCls(ir.getInstr().getCurrPkgClassName());
		rep.setInstContainerMthd(ir.getInstr().getCurrMethodName());
		rep.setPermutationStr(permStr);

		rep.setMessage(msg);
		rep.setSinkAPI(ir.getInstr().getText());

		if (vibrateSetting == 1) {
			if (!AttackReporter.getInstance().checkIfAudioMgrVibrationOnReportExists(rep)) {
				AttackReporter.getInstance().getAudioMgrVibrationOnReportList().add(rep);
				rep.printReport();
			}
		} else if (vibrateSetting == 0) {
			if (!AttackReporter.getInstance().checkIfAudioMgrVibrationOffReportExists(rep)) {
				AttackReporter.getInstance().getAudioMgrVibrationOffReportList().add(rep);
				rep.printReport();
			}
		} else if (vibrateSetting == 3) {
			if (!AttackReporter.getInstance().checkIfAudioMgrVibrationRestoreReportExists(rep)) {
				AttackReporter.getInstance().getAudioMgrVibrationRestoreReportList().add(rep);
				rep.printReport();
			}
		}
	}
}
