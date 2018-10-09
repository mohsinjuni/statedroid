package patternMatcher.statemachines.csm.smssender.states;

import java.util.ArrayList;
import java.util.Hashtable;

import javax.print.URIException;

import models.cfg.Instruction;
import models.cfg.InstructionResponse;
import models.cfg.Register;
import models.manifest.AndroidManifest;
import models.manifest.ComponentManifest;
import models.symboltable.EntryDetails;
import models.symboltable.SourceInfo;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;
import patternMatcher.attackreporter.AttackReporter;
import patternMatcher.attackreporter.PremiumSmsSenderReport;
import patternMatcher.events.Event;
import patternMatcher.events.EventFactory;
import patternMatcher.events.asm.SmsSenderASMEvent;
import patternMatcher.events.asm.contentprovider.ContentProviderDeletionASMEvent;
import patternMatcher.events.csm.SmsSenderEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.csm.smssender.SmsSenderStates;
import taintanalyzer.TaintAnalyzer;
import configuration.Config;

public class InitialState extends SmsSenderStates {

	private TaintAnalyzer ta;

	public InitialState(TaintAnalyzer taParam) {
		this.ta = taParam;
	}

	public InitialState() {
	}

	//		0x46 invoke-virtual/range v0 ... v5, Landroid/telephony/SmsManager;->sendTextMessage
	//		(Ljava/lang/String; Ljava/lang/String; Ljava/lang/String; Landroid/app/PendingIntent; Landroid/app/PendingIntent;)V

	@Override
	public State update(SmsSenderEvent e) {

		org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(InitialState.class);

		AndroidManifest am = Config.getInstance().getAndroidManifest();
		String currPkgClsInfo = e.getCurrPkgClsName();
		ComponentManifest compInfo = am.findComponentManifest(currPkgClsInfo);
		ArrayList<String> permList = am.getUsedPermStrList();

		Hashtable eventInfo = e.getEventInfo();
		ArrayList<String> currCFGPermutation = (ArrayList<String>) eventInfo.get("currCFGPermutation");
		String instr = (String) eventInfo.get("instrText");

		InstructionResponse ir = (InstructionResponse) eventInfo.get(InstructionResponse.CLASS_NAME);
		SymbolSpace localSymSpace = Config.getInstance().getLocalSymbolSpace();
		ArrayList<Register> involvedRegisters = ir.getInvolvedRegisters();
		Register phoneNoReg = involvedRegisters.get(1);
		Register smsReg = involvedRegisters.get(3);

		SymbolTableEntry phoneNoEntry = localSymSpace.find(phoneNoReg.getName());
		SymbolTableEntry smsEntry = localSymSpace.find(smsReg.getName());

		//si starts with "uri -> "
		if (phoneNoEntry != null) {

			EntryDetails userED = phoneNoEntry.getEntryDetails();
			String entryValue = userED.getValue().trim();
			String msg = "";

			//userED.isConstant() &&	
			if (phoneNoEntry.getEntryDetails().isTainted()) {

				//Ideally, it should have only one SourceInfo, so we will check to find for sensitiveDBs only.
				ArrayList<SourceInfo> siList = phoneNoEntry.getEntryDetails().getSourceInfoList();
				SourceInfo srcInfo = null;

				for (SourceInfo si : siList) {
					String siName = si.getSrcAPI();
					if (siName.contains("uri")) {
						srcInfo = si;
						break;
					}
				}
				if (srcInfo != null) {

					String uriName = srcInfo.getSrcAPI();
					msg = " ##### This App can send SMS to a number obtained from  " + uriName;
					entryValue = uriName;
				}

			} else if (msg.isEmpty() && (entryValue.equalsIgnoreCase("Landroid/telephony/gsm/SmsMessage;->getOriginatingAddress"))
					|| (entryValue.equalsIgnoreCase("Landroid/telephony/SmsMessage;->getOriginatingAddress"))
					|| (entryValue.equalsIgnoreCase("Landroid/telephony/gsm/SmsMessage;->getDisplayOriginatingAddress"))
					|| (entryValue.equalsIgnoreCase("Landroid/telephony/SmsMessage;->getDisplayOriginatingAddress"))) {
				msg = " ##### This App can send a reply to an incoming SMS message.";

			} else if (msg.isEmpty() && !entryValue.isEmpty() && !entryValue.equalsIgnoreCase("0")) {
				msg = " ##### This App can send SMS to hard-coded (premium-rate) number::";
			}

			if (!msg.isEmpty()) {
				String info1 = phoneNoEntry.getInstrInfo() + ", \n [instr]" + instr;
				String permStr = Config.getInstance().getCurrCFGPermutationString();

				PremiumSmsSenderReport rep = new PremiumSmsSenderReport();
				rep.setCompPkgName(ta.getCurrComponentPkgName());
				rep.setCompCallbackMethdName(ta.getCurrComponentCallback());
				rep.setCurrComponentClsName(ta.getCurrComponentName());
				rep.setSinkAPI(info1);
				rep.setRecpNo(entryValue);
				rep.setPermutationStr(permStr);
				rep.setMessage(msg);

				if (smsEntry != null) {
					String value = smsEntry.getEntryDetails().getValue();
					if (!value.isEmpty()) {
						rep.setSmsContent(value);
					}
				}
				if (!AttackReporter.getInstance().checkIfPremiumSmsSenderExists(rep)) {
					AttackReporter.getInstance().getPremiumSmsReportList().add(rep);
					rep.printReport();
				}
				generateAndSendEvent(ir);
			}
		}

		return this;
	}

	public void generateAndSendEvent(InstructionResponse ir) {
		EventFactory.getInstance().registerEvent("smsSenderASMEvent", new SmsSenderASMEvent());
		Event event = EventFactory.getInstance().createEvent("smsSenderASMEvent");
		event.setName("smsSenderASMEvent");

		event.setCurrComponentName(ta.getCurrComponentName());
		Instruction instr = ir.getInstr();

		event.setCurrPkgClsName(instr.getCurrPkgClassName());
		event.setCurrMethodName(instr.getCurrMethodName());
		event.setCurrComponentPkgName(ta.getCurrComponentPkgName());
		event.setCurrCompCallbackMethodName(ta.getCurrComponentCallback());

		event.getEventInfo().put(InstructionResponse.CLASS_NAME, (InstructionResponse) ir);

		ta.setCurrASMEvent(event);
	}

}
