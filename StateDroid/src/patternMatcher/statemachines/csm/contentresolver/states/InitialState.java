package patternMatcher.statemachines.csm.contentresolver.states;

import java.util.ArrayList;
import java.util.Hashtable;

import models.cfg.Instruction;
import models.cfg.InstructionResponse;
import models.cfg.Register;
import models.symboltable.SourceInfo;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;
import patternMatcher.events.Event;
import patternMatcher.events.EventFactory;
import patternMatcher.events.asm.contentprovider.ContentProviderDeletionASMEvent;
import patternMatcher.events.asm.contentprovider.ContentProviderInsertASMEvent;
import patternMatcher.events.asm.contentprovider.ContentProviderUpdateASMEvent;
import patternMatcher.events.csm.contentresolver.ContentResolverApplyBatchEvent;
import patternMatcher.events.csm.contentresolver.ContentResolverDeleteEvent;
import patternMatcher.events.csm.contentresolver.ContentResolverInsertEvent;
import patternMatcher.events.csm.contentresolver.ContentResolverQueryEvent;
import patternMatcher.events.csm.contentresolver.ContentResolverUpdateEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.csm.contentresolver.ContentResolverStates;
import patternMatcher.statemachines.csm.uri.states.SensitiveUriContentProviderDefinedState;
import taintanalyzer.TaintAnalyzer;
import configuration.Config;

public class InitialState extends ContentResolverStates {

	private String currInstr = "";
	private SymbolSpace localSymSpace = Config.getInstance().getLocalSymbolSpace();

	private TaintAnalyzer ta;

	public InitialState(TaintAnalyzer taParam) {
		this.ta = taParam;
	}

	public InitialState() {
	}

	@Override
	public State update(ContentResolverQueryEvent e) {

		SymbolTableEntry cursorEntry = null;
		InstructionResponse ir = (InstructionResponse) e.getEventInfo().get(InstructionResponse.CLASS_NAME);
		ArrayList<Register> involvedRegisters = ir.getInvolvedRegisters();

		Register uriReg = ir.getInvolvedRegisters().get(0);
		cursorEntry = localSymSpace.find(uriReg.getName());

		if (cursorEntry != null) {
			Hashtable recordFiedList = cursorEntry.getEntryDetails().getRecordFieldList();
			if (recordFiedList != null) {
				SymbolTableEntry uriEntry = (SymbolTableEntry) recordFiedList.get("uri");
				if (uriEntry != null) {
					State uriState = uriEntry.getEntryDetails().getState();
					if (uriState != null && uriState instanceof SensitiveUriContentProviderDefinedState) {
						String dbInfo = uriEntry.getEntryDetails().getValue();
						cursorEntry.getEntryDetails().setTainted(true);

						ArrayList<SourceInfo> srcInfoList = cursorEntry.getEntryDetails().getSourceInfoList();
						if (srcInfoList == null) {
							srcInfoList = new ArrayList<SourceInfo>();
						}
						SourceInfo si = new SourceInfo();
						dbInfo = "uri -> " + dbInfo;
						si.setSrcAPI(dbInfo);
						if (!srcInfoList.contains(si)) {
							srcInfoList.add(si);
						}
						cursorEntry.getEntryDetails().setSourceInfoList(srcInfoList);
						State state = new SensitiveCursorDefinedState(ta);
						cursorEntry.getEntryDetails().setState(state);
					}
				}
			}
		}
		return this;
	}

	@Override
	public State update(ContentResolverDeleteEvent e) {

		SymbolTableEntry crEntry = null;
		InstructionResponse ir = (InstructionResponse) e.getEventInfo().get(InstructionResponse.CLASS_NAME);
		ArrayList<Register> involvedRegisters = ir.getInvolvedRegisters();

		Register crReg = involvedRegisters.get(0);
		crEntry = localSymSpace.find(crReg.getName());

		if (crEntry != null) {
			Hashtable recordFiedList = crEntry.getEntryDetails().getRecordFieldList();
			if (recordFiedList != null) {
				SymbolTableEntry uriEntry = (SymbolTableEntry) recordFiedList.get("uri");
				if (uriEntry != null) {
					State uriState = uriEntry.getEntryDetails().getState();
					if (uriState != null && uriState instanceof SensitiveUriContentProviderDefinedState) {
						String dbInfo = uriEntry.getEntryDetails().getValue();
						//					crEntry.getEntryDetails().setTainted(true);

						EventFactory.getInstance().registerEvent("contentProviderDeletionEvent", new ContentProviderDeletionASMEvent());
						Event contentProviderDeletionEvent = EventFactory.getInstance().createEvent("contentProviderDeletionEvent");
						contentProviderDeletionEvent.setName("contentProviderDeletionEvent");

						contentProviderDeletionEvent.setCurrComponentName(ta.getCurrComponentName());
						Instruction instr = ir.getInstr();

						contentProviderDeletionEvent.setCurrPkgClsName(instr.getCurrPkgClassName());
						contentProviderDeletionEvent.setCurrMethodName(instr.getCurrMethodName());
						contentProviderDeletionEvent.setCurrComponentPkgName(ta.getCurrComponentPkgName());
						contentProviderDeletionEvent.setCurrCompCallbackMethodName(ta.getCurrComponentCallback());

						contentProviderDeletionEvent.getEventInfo().put("dbInfo", (String) dbInfo);
						contentProviderDeletionEvent.getEventInfo().put(InstructionResponse.CLASS_NAME, (InstructionResponse) ir);

						ta.setCurrASMEvent(contentProviderDeletionEvent);
					}
				}
			}
		}
		return this;
	}

	@Override
	public State update(ContentResolverUpdateEvent e) {

		SymbolTableEntry crEntry = null;
		InstructionResponse ir = (InstructionResponse) e.getEventInfo().get(InstructionResponse.CLASS_NAME);
		ArrayList<Register> involvedRegisters = ir.getInvolvedRegisters();

		Register crReg = involvedRegisters.get(0);
		crEntry = localSymSpace.find(crReg.getName());

		if (crEntry != null) {
			Hashtable recordFiedList = crEntry.getEntryDetails().getRecordFieldList();
			if (recordFiedList != null) {
				SymbolTableEntry uriEntry = (SymbolTableEntry) recordFiedList.get("uri");
				State uriState = uriEntry.getEntryDetails().getState();
				if (uriState != null && uriState instanceof SensitiveUriContentProviderDefinedState) {
					String dbInfo = uriEntry.getEntryDetails().getValue();

					EventFactory.getInstance().registerEvent("contentProviderUpdateASMEvent", new ContentProviderUpdateASMEvent());
					Event contentProviderUpdateASMEvent = EventFactory.getInstance().createEvent("contentProviderUpdateASMEvent");
					contentProviderUpdateASMEvent.setName("contentProviderUpdateASMEvent");

					contentProviderUpdateASMEvent.setCurrComponentName(ta.getCurrComponentName());
					Instruction instr = ir.getInstr();

					contentProviderUpdateASMEvent.setCurrPkgClsName(instr.getCurrPkgClassName());
					contentProviderUpdateASMEvent.setCurrMethodName(instr.getCurrMethodName());
					contentProviderUpdateASMEvent.setCurrComponentPkgName(ta.getCurrComponentPkgName());
					contentProviderUpdateASMEvent.setCurrCompCallbackMethodName(ta.getCurrComponentCallback());

					contentProviderUpdateASMEvent.getEventInfo().put("dbInfo", (String) dbInfo);
					contentProviderUpdateASMEvent.getEventInfo().put(InstructionResponse.CLASS_NAME, (InstructionResponse) ir);

					ta.setCurrASMEvent(contentProviderUpdateASMEvent);
				}
			}
		}
		return this;
	}

	@Override
	public State update(ContentResolverInsertEvent e) {
		SymbolTableEntry crEntry = null;
		InstructionResponse ir = (InstructionResponse) e.getEventInfo().get(InstructionResponse.CLASS_NAME);
		ArrayList<Register> involvedRegisters = ir.getInvolvedRegisters();

		Register crReg = involvedRegisters.get(0);
		crEntry = localSymSpace.find(crReg.getName());

		if (crEntry != null) {
			Hashtable recordFiedList = crEntry.getEntryDetails().getRecordFieldList();
			if (recordFiedList != null) {
				SymbolTableEntry uriEntry = (SymbolTableEntry) recordFiedList.get("uri");
				State uriState = uriEntry.getEntryDetails().getState();
				if (uriState != null && uriState instanceof SensitiveUriContentProviderDefinedState) {
					String dbInfo = uriEntry.getEntryDetails().getValue();

					EventFactory.getInstance().registerEvent("contentProviderInsertASMEvent", new ContentProviderInsertASMEvent());
					Event contentProviderInsertASMEvent = EventFactory.getInstance().createEvent("contentProviderInsertASMEvent");
					contentProviderInsertASMEvent.setName("contentProviderInsertASMEvent");

					contentProviderInsertASMEvent.setCurrComponentName(ta.getCurrComponentName());
					Instruction instr = ir.getInstr();

					contentProviderInsertASMEvent.setCurrPkgClsName(instr.getCurrPkgClassName());
					contentProviderInsertASMEvent.setCurrMethodName(instr.getCurrMethodName());
					contentProviderInsertASMEvent.setCurrComponentPkgName(ta.getCurrComponentPkgName());
					contentProviderInsertASMEvent.setCurrCompCallbackMethodName(ta.getCurrComponentCallback());

					contentProviderInsertASMEvent.getEventInfo().put("dbInfo", (String) dbInfo);
					contentProviderInsertASMEvent.getEventInfo().put(InstructionResponse.CLASS_NAME, (InstructionResponse) ir);

					ta.setCurrASMEvent(contentProviderInsertASMEvent);
				}
			}
		}
		return this;
	}

	@Override
	public State update(ContentResolverApplyBatchEvent e) {
		return (State) new InitialState(this.ta);
	}

}
