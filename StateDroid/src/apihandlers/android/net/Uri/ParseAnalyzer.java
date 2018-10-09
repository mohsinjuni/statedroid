package apihandlers.android.net.Uri;

import java.util.ArrayList;
import java.util.Hashtable;

import models.cfg.Instruction;
import models.cfg.InstructionResponse;
import models.cfg.InstructionReturnValue;
import models.cfg.Register;
import models.symboltable.SourceInfo;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;

import org.apache.log4j.Logger;

import patternMatcher.events.Event;
import patternMatcher.events.EventFactory;
import patternMatcher.events.csm.uri.UriParsedEvent;
import taintanalyzer.TaintAnalyzer;
import taintanalyzer.instranalyzers.BaseTaintAnalyzer;
import configuration.Config;

public class ParseAnalyzer extends BaseTaintAnalyzer {

	private InstructionResponse ir;
	private Instruction instr;
	private TaintAnalyzer ta;
	private SymbolSpace localSymSpace;

	public ParseAnalyzer(TaintAnalyzer ta) {
		ir = ta.getIr();
		instr = ta.getIr().getInstr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();
		logger = Logger.getLogger(ParseAnalyzer.class);
	}

	//	0xe invoke-static v1, Landroid/net/Uri;->parse(Ljava/lang/String;) Landroid/net/Uri;

	public Object analyzeInstruction() {

		SymbolTableEntry uriStringEntry = null;
		SymbolTableEntry uriEntry = new SymbolTableEntry();

		Register destReg = ir.getInvolvedRegisters().get(0);

		SymbolTableEntry inputParamEntry = localSymSpace.find(destReg.getName());

		// // Original prototype: public static Uri parse (String uriString), though it should not be hardcoded.
		if (inputParamEntry != null) {
			//Since string returns its value, we need deep copy.
			uriStringEntry = new SymbolTableEntry(inputParamEntry);
			uriStringEntry.setName("uriString");

			uriEntry.getEntryDetails().setType(ir.getReturnType()); // it should be Landroid/net/Uri or something like that.
			uriEntry.getEntryDetails().setConstant(false);
			uriEntry.getEntryDetails().setField(false);
			uriEntry.getEntryDetails().setRecord(true);
			uriEntry.setLineNumber(ir.getLineNumber());

			if (uriStringEntry.getEntryDetails().isTainted()) {
				uriEntry.getEntryDetails().setTainted(true);

				ArrayList<SourceInfo> existingSiList = uriStringEntry.getEntryDetails().getSourceInfoList();
				ArrayList<SourceInfo> siList = uriEntry.getEntryDetails().getSourceInfoList();

				if (existingSiList != null && existingSiList.size() > 0) {
					if (siList == null)
						siList = new ArrayList<SourceInfo>();

					for (SourceInfo si : existingSiList) {
						if (!siList.contains(si))
							siList.add(si);
					}
				}
				uriEntry.getEntryDetails().setSourceInfoList(siList);
			}

			uriEntry.setInstrInfo(ir.getInstr().getText());

			Hashtable recordFieldList = (Hashtable) uriEntry.getEntryDetails().getRecordFieldList();

			if (recordFieldList == null)
				recordFieldList = new Hashtable();

			recordFieldList.put("uriString", uriStringEntry);

			uriEntry.getEntryDetails().setRecordFieldList(recordFieldList);

			EventFactory.getInstance().registerEvent("uriParsedEvent", new UriParsedEvent());
			Event uriParsedEvent = EventFactory.getInstance().createEvent("uriParsedEvent");
			uriParsedEvent.setName("uriParsedEvent");

			Hashtable<String, Object> eventInfo = uriParsedEvent.getEventInfo();

			InstructionReturnValue instrAnalysisReponse = new InstructionReturnValue(uriEntry, uriParsedEvent);

			return instrAnalysisReponse;

		} else // This case should never arise.
		{
			uriEntry = new SymbolTableEntry();

			uriEntry.setName("");

			uriEntry.getEntryDetails().setType(ir.getReturnType());
			uriEntry.setLineNumber(ir.getLineNumber());
			uriEntry.setInstrInfo(ir.getInstr().getText());

		}
		logger.debug("\n Uri.ParseAnalyzer");
		return uriEntry;
	}
}
