package patternMatcher.statemachines.csm.contentresolver.states;

import java.util.ArrayList;

import configuration.Config;

import models.cfg.InstructionResponse;
import models.cfg.Register;
import models.symboltable.SourceInfo;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;
import patternMatcher.events.Event;
import patternMatcher.events.EventFactory;
import patternMatcher.events.asm.contentprovider.ContentProviderDeletionASMEvent;
import patternMatcher.events.csm.contentresolver.ContentResolverDeleteEvent;
import patternMatcher.events.csm.cursor.CursorGetStringEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.csm.contentresolver.ContentResolverStates;
import taintanalyzer.TaintAnalyzer;

public class SensitiveCursorDefinedState extends ContentResolverStates {

	private String currInstr = "";
	private SymbolSpace localSymSpace = Config.getInstance().getLocalSymbolSpace();

	private TaintAnalyzer ta;

	public SensitiveCursorDefinedState(TaintAnalyzer taParam) {
		this.ta = taParam;
	}

	@Override
	public State update(CursorGetStringEvent e) {
		SymbolTableEntry cursorEntry = null;
		SymbolTableEntry returnEntry = null;

		cursorEntry = (SymbolTableEntry) e.getEventInfo().get("cursorEntry");
		if (cursorEntry != null) {
			State state = cursorEntry.getEntryDetails().getState();
			if (state != null && state instanceof SensitiveCursorDefinedState) {
				InstructionResponse ir = (InstructionResponse) e.getEventInfo().get(InstructionResponse.CLASS_NAME);
				ArrayList<Register> involvedRegisters = ir.getInvolvedRegisters();

				Register retReg = ir.getInvolvedRegisters().get(0);
				returnEntry = localSymSpace.find(retReg.getName());

				if (returnEntry != null) {
					ArrayList<SourceInfo> srcInfoList = returnEntry.getEntryDetails().getSourceInfoList();
					if (cursorEntry.getEntryDetails().isTainted()) {
						ArrayList<SourceInfo> existingSilist = cursorEntry.getEntryDetails().getSourceInfoList();

						if (existingSilist != null && existingSilist.size() > 0) {
							if (srcInfoList == null)
								srcInfoList = new ArrayList<SourceInfo>();
							SourceInfo interestingSI = null;
							for (SourceInfo si : existingSilist) {
								returnEntry.getEntryDetails().setTainted(true);
								if (!srcInfoList.contains(si)) {
									String srcAPI = si.getSrcAPI();
									if (srcAPI.startsWith("uri")) {
										String value = returnEntry.getEntryDetails().getValue();
										if (!value.isEmpty()) {
											srcAPI = "column=" + value + " of " + srcAPI;
											si.setSrcAPI(srcAPI);
										}
									}
									srcInfoList.add(si);
								}
							}
						}
						returnEntry.getEntryDetails().setSourceInfoList(srcInfoList);
					}
				}
			}
		}
		return this;
	}

}
