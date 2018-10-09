package patternMatcher.statemachines.csm.url.states;

import java.util.Hashtable;

import models.cfg.InstructionResponse;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;
import patternMatcher.events.csm.url.UrlInitEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.csm.url.urlStates;
import taintanalyzer.TaintAnalyzer;
import configuration.Config;

public class InitialState extends urlStates {

	private TaintAnalyzer ta;
	private SymbolSpace localSymbolSpace;

	public InitialState(TaintAnalyzer taParam) {
		this.ta = taParam;
		this.localSymbolSpace = Config.getInstance().getLocalSymbolSpace();
	}

	public InitialState() {
	}

	private String currInstr = "";

	// <InitState> => UrlDefinedState => UrlOpenConnectionState => UrlGetOutputStreamState

	/*
	 * // 0xfa new-instance v34, Ljava/net/URL;
	 * // 0xfe const-string v36,
	 * 'https://132.72.23.126:443/archiveArtifact/rest/test'
	 * // 0x102 move-object/from16 v0, v34
	 * // 0x106 move-object/from16 v1, v36
	 * // 0x10a invoke-direct v0, v1,
	 * Ljava/net/URL;-><init>(Ljava/lang/String;)V
	 */

	//This method should set state of the URI and generate a new event if needed. UriPasedEvent is generated from move-result-object instruction.
	@Override
	public State update(UrlInitEvent e) {
		InstructionResponse ir = (InstructionResponse) e.getEventInfo().get(InstructionResponse.CLASS_NAME);
		String instrText = ir.getInstr().getText();
		SymbolTableEntry urlEntry = (SymbolTableEntry) e.getEventInfo().get("urlEntry");

		if (urlEntry != null) {
			Hashtable recordFieldList = urlEntry.getEntryDetails().getRecordFieldList();
			UrlDefinedState state = new UrlDefinedState(this.ta);
			if (recordFieldList != null) {
				SymbolTableEntry urlStringEntry = (SymbolTableEntry) recordFieldList.get("urlStringEntry");
				if (urlStringEntry != null) {
					String urlValue = urlStringEntry.getEntryDetails().getValue();
					if (urlValue != null && !urlValue.isEmpty()) {
						urlEntry.getEntryDetails().setValue(urlValue);
					}
				}
			}
			urlEntry.getEntryDetails().setState(state);
		}
		return this;
	}
}
