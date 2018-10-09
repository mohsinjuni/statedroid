package patternMatcher.statemachines.asm.settingstogglerASM.states;
import models.cfg.InstructionResponse;
import patternMatcher.attackreporter.AttackReporter;
import patternMatcher.attackreporter.GenericReport;
import patternMatcher.events.asm.settings.DeviceAirPlaneModeModificationASMEvent;
import patternMatcher.events.asm.settings.DeviceBrightnessModificationASMEvent;
import patternMatcher.events.asm.settings.MobileDataTogglerASMEvent;
import patternMatcher.events.asm.settings.WifiTogglerASMEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.asm.settingstogglerASM.SettingsTogglerASMStates;
import taintanalyzer.TaintAnalyzer;
import configuration.Config;

public class InitialState extends SettingsTogglerASMStates{

	private TaintAnalyzer ta;
	public InitialState(){}
	public InitialState(TaintAnalyzer ta){
		this.ta = ta;
	}
	
	@Override
	public State update(WifiTogglerASMEvent e) {

		GenericReport rep = new GenericReport();
		rep.setInstrContainerCls(e.getCurrPkgClsName());
		rep.setInstContainerMthd(e.getCurrMethodName());

		rep.setCompPkgName(ta.getCurrComponentPkgName());
		rep.setCompCallbackMethdName(ta.getCurrComponentCallback());
		rep.setCurrComponentClsName(ta.getCurrComponentName());
		
		String permStr = Config.getInstance().getCurrCFGPermutationString();
 		rep.setPermutationStr(permStr);
 		rep.setMessage(" ##### This app can can programmatically enable or disable wifi on your device  ##########");
 		
 		InstructionResponse ir = (InstructionResponse) e.getEventInfo().get(InstructionResponse.CLASS_NAME);
 	    rep.setSinkAPI(ir.getInstr().getText());
 	    
 		if(!AttackReporter.getInstance().checkIfWifiToggleReportExists(rep)){
 			AttackReporter.getInstance().getWifiToggleReportList().add(rep);
 			rep.printReport();
 		}
		return this;
	}
	
	@Override
	public State update(DeviceBrightnessModificationASMEvent e) {

		GenericReport rep = new GenericReport();
		rep.setInstrContainerCls(e.getCurrPkgClsName());
		rep.setInstContainerMthd(e.getCurrMethodName());
		rep.setCompPkgName(e.getCurrComponentPkgName());
		rep.setCompCallbackMethdName(e.getCurrCompCallbackMethodName());
		
		String permStr = Config.getInstance().getCurrCFGPermutationString();
 		rep.setCurrComponentClsName(e.getCurrComponentName());
 		rep.setPermutationStr(permStr);
 		rep.setMessage(" ##### This app can programmatically reduce/increase device brightness and (probably can do harmful activities in the dark  ##########");
 		
 		InstructionResponse ir = (InstructionResponse) e.getEventInfo().get(InstructionResponse.CLASS_NAME);
 	    rep.setSinkAPI(ir.getInstr().getText());
 	    
 		if(!AttackReporter.getInstance().checkIfDeviceBrightnessReportExists(rep)){
 			AttackReporter.getInstance().getDeviceBrightnessReportList().add(rep);
 			rep.printReport();
 		}
		return this;
	}
	
	@Override
	public State update(DeviceAirPlaneModeModificationASMEvent e) {
		GenericReport rep = new GenericReport();
		rep.setInstrContainerCls(e.getCurrPkgClsName());
		rep.setInstContainerMthd(e.getCurrMethodName());
		rep.setCompPkgName(e.getCurrComponentPkgName());
		rep.setCompCallbackMethdName(e.getCurrCompCallbackMethodName());
		
		String permStr = Config.getInstance().getCurrCFGPermutationString();
 		rep.setCurrComponentClsName(e.getCurrComponentName());
 		rep.setPermutationStr(permStr);
 		rep.setMessage(" ##### This app can programmatically enable or disable airplane mode of the device and can disrupt the user communication  ##########");
 		
 		InstructionResponse ir = (InstructionResponse) e.getEventInfo().get(InstructionResponse.CLASS_NAME);
 	    rep.setSinkAPI(ir.getInstr().getText());
 	    
 		if(!AttackReporter.getInstance().checkIfDeviceAirPlaneModeReporttExists(rep)){
 			AttackReporter.getInstance().getDeviceAirPlaneModeReportList().add(rep);
 			rep.printReport();
 		}
		return this;
	}

	@Override
	public State update(MobileDataTogglerASMEvent e) {
		
		GenericReport rep = new GenericReport();
		rep.setInstrContainerCls(e.getCurrPkgClsName());
		rep.setInstContainerMthd(e.getCurrMethodName());
		rep.setCompPkgName(e.getCurrComponentPkgName());
		rep.setCompCallbackMethdName(e.getCurrCompCallbackMethodName());
		
		String permStr = Config.getInstance().getCurrCFGPermutationString();
 		rep.setCurrComponentClsName(e.getCurrComponentName());
 		rep.setPermutationStr(permStr);
 		rep.setMessage(" ##### This app can programmatically enable or disable mobile data (4G, LTE etc.) of the device and can use that data to leak out private information ##########");
 		
 		InstructionResponse ir = (InstructionResponse) e.getEventInfo().get(InstructionResponse.CLASS_NAME);
 	    rep.setSinkAPI(ir.getInstr().getText());
 	    
 		if(!AttackReporter.getInstance().checkIfMobileDataTogglerReportExists(rep)){
 			AttackReporter.getInstance().getMobileDataTogglerReportList().add(rep);
 			rep.printReport();
 		}
		return this;
	}
}
