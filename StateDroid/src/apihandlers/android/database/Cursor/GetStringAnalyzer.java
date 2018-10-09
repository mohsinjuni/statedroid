package apihandlers.android.database.Cursor;

import java.util.ArrayList;
import java.util.Hashtable;

import models.cfg.Instruction;
import models.cfg.InstructionResponse;
import models.cfg.InstructionReturnValue;
import models.cfg.Register;
import models.symboltable.EntryDetails;
import models.symboltable.SourceInfo;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;

import org.apache.log4j.Logger;

import com.sun.java.accessibility.util.EventID;

import patternMatcher.events.Event;
import patternMatcher.events.EventFactory;
import patternMatcher.events.csm.contentresolver.ContentResolverQueryEvent;
import patternMatcher.events.csm.cursor.CursorGetStringEvent;


import taintanalyzer.TaintAnalyzer;
import taintanalyzer.instranalyzers.BaseTaintAnalyzer;
import configuration.Config;
import enums.Constants;

public class GetStringAnalyzer extends BaseTaintAnalyzer{

	private InstructionResponse ir;
	private Instruction instr;
	private TaintAnalyzer ta;
	private SymbolSpace localSymSpace;

	public GetStringAnalyzer(TaintAnalyzer ta)
	{
		ir = ta.getIr();
		instr = ta.getIr().getInstr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();	
		logger = Logger.getLogger(GetStringAnalyzer.class);
	}

//	0x54 invoke-interface v6, v1, Landroid/database/Cursor;->getString(I)Ljava/lang/String;
// 	0x58 move-result-object v1
	
// Prototypes:
	// public abstract String getString (int columnIndex)

	public Object analyzeInstruction()
	{
		SymbolTableEntry returnEntry = new SymbolTableEntry();
		EntryDetails returnEntryDetails = returnEntry.getEntryDetails();
		String text = ir.getInstr().getText();
		
		String cursorRegName = ir.getInvolvedRegisters().get(0).getName();
		String columnIndexRegName = ir.getInvolvedRegisters().get(1).getName();

		SymbolTableEntry cursorEntry = localSymSpace.find(cursorRegName);
		SymbolTableEntry colmIndexEntry = localSymSpace.find(columnIndexRegName);
		
		if(cursorEntry != null)
		{
			EntryDetails cursorEntryDetails = cursorEntry.getEntryDetails();
		    Hashtable cursorFieldList = cursorEntryDetails.getRecordFieldList();
		    String dbName = "";
		   
		   if(colmIndexEntry!= null){
			    if(cursorFieldList == null){
			    	cursorFieldList = new Hashtable();
			    }
 			    String indexValue = colmIndexEntry.getEntryDetails().getValue();
			    cursorFieldList.put("columIndexValue", colmIndexEntry);
			    returnEntry.getEntryDetails().setValue(indexValue);
		   }
		   cursorEntryDetails.setRecordFieldList(cursorFieldList);
		   cursorEntry.setEntryDetails(cursorEntryDetails);

			EventFactory.getInstance().registerEvent("cursorGetStringEvent", new CursorGetStringEvent());
			Event cursorGetStringEvent = EventFactory.getInstance().createEvent("cursorGetStringEvent");
			cursorGetStringEvent.setName("cursorGetStringEvent");

			Hashtable eventInfo = cursorGetStringEvent.getEventInfo();
			eventInfo.put("cursorEntry", cursorEntry);
			
			InstructionReturnValue irValue = new InstructionReturnValue(returnEntry, cursorGetStringEvent);
		   
//			logger.debug("\n Cursor.GetStringAnalyzer");
			return irValue;
		}
		return null;
	}
}
