package patternMatcher.statemachines.csm.uri.states;

import java.util.HashSet;
import java.util.Hashtable;

import models.cfg.InstructionResponse;
import models.cfg.Register;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;
import patternMatcher.events.csm.uri.UriParsedEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.csm.uri.uriStates;
import taintanalyzer.TaintAnalyzer;
import configuration.Config;
import enums.Constants;

public class InitialState extends uriStates {

	private TaintAnalyzer ta;
	private SymbolSpace localSymbolSpace;

	public InitialState(TaintAnalyzer taParam) {
		this.ta = taParam;
		this.localSymbolSpace = Config.getInstance().getLocalSymbolSpace();
	}

	public InitialState() {
	}

	private String currInstr = "";

	/*
	 * 0x42 invoke-static v1,
	 * Landroid/net/Uri;->parse(Ljava/lang/String;)Landroid/net/Uri;
	 * 0x48 move-result-object v1
	 */

	//This method should set state of the URI and generate a new event if needed. UriPasedEvent is generated from move-result-object instruction.
	@Override
	public State update(UriParsedEvent e) {
		InstructionResponse ir = (InstructionResponse) e.getEventInfo().get(InstructionResponse.CLASS_NAME);
		String instrText = ir.getInstr().getText();

		if (instrText.contains("move-result-object")) {
			Register uriReg = ir.getInvolvedRegisters().get(0);
			SymbolTableEntry uriEntry = this.localSymbolSpace.find(uriReg.getName());

			if (uriEntry != null) {
				Hashtable recordFieldList = uriEntry.getEntryDetails().getRecordFieldList();
				if (recordFieldList != null) {
					SymbolTableEntry inputUriEntry = (SymbolTableEntry) recordFieldList.get("uriString");
					String uriValue = inputUriEntry.getEntryDetails().getValue();
					if (uriValue != null && !uriValue.isEmpty()) {
						if (doesContainCallForwardingCode(uriValue)) {
							uriEntry.getEntryDetails().setValue(uriValue);
							UriCallForwardingDefinedState state = new UriCallForwardingDefinedState();
							uriEntry.getEntryDetails().setState(state);
						} else if (uriValue.contains("tel")) {
							uriEntry.getEntryDetails().setValue(uriValue);
							UriTelDefinedState state = new UriTelDefinedState();
							uriEntry.getEntryDetails().setState(state);
						} else if (isSensitiveDBUri(uriValue)) {
							uriEntry.getEntryDetails().setValue(uriValue);
							SensitiveUriContentProviderDefinedState state = new SensitiveUriContentProviderDefinedState();
							uriEntry.getEntryDetails().setState(state);
						}
					}
				}
			}
		}
		return this;
	}

	public boolean doesContainCallForwardingCode(String uriValue) {
		boolean isCallForwarding = false;
		HashSet<String> callForwardingCodeSet = Config.getInstance().getCallForwardingCodes();
		for (String s : callForwardingCodeSet) {
			if (uriValue.contains(s)) {
				return true;
			}
		}
		return isCallForwarding;
	}

	public boolean isSensitiveDBUri(String uriString) {
		boolean isSensitive = false;

		String finalUriString = "'";
		if (!uriString.startsWith("'")) {
			finalUriString += uriString + "'";
		} else {
			finalUriString = uriString;
		}
		Hashtable dbUris = Constants.getInstance().getSensitiveDbUris();
		if (dbUris.containsKey(finalUriString)) {
			isSensitive = true;
		}
		return isSensitive;
	}

}
