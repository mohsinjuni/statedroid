package apihandlers.android.content.ContentResolver;

import java.util.ArrayList;
import java.util.Hashtable;

import models.cfg.Instruction;
import models.cfg.InstructionResponse;
import models.cfg.Register;
import models.symboltable.EntryDetails;
import models.symboltable.SourceInfo;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;

import org.apache.log4j.Logger;


import patternMatcher.events.Event;
import patternMatcher.events.EventFactory;
import patternMatcher.events.csm.contentresolver.ContentResolverInsertEvent;
import patternMatcher.events.csm.contentresolver.ContentResolverUpdateEvent;
import taintanalyzer.TaintAnalyzer;
import taintanalyzer.instranalyzers.BaseTaintAnalyzer;
import configuration.Config;
import enums.Constants;

public class InsertAnalyzer extends BaseTaintAnalyzer{
	private InstructionResponse ir;
	private Instruction instr;
	private TaintAnalyzer ta;
	private SymbolSpace localSymSpace;

	public InsertAnalyzer(TaintAnalyzer ta){
		ir = ta.getIr();
		instr = ta.getIr().getInstr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();	
		logger = Logger.getLogger(InsertAnalyzer.class);
		this.ta = ta;
	}

//		0x56 sget-object v3, Landroid/provider/Browser;->BOOKMARKS_URI Landroid/net/Uri;
//		0x5a invoke-virtual v2, v3, v1, Landroid/content/ContentResolver;->insert(Landroid/net/Uri; Landroid/content/ContentValues;)Landroid/net/Uri;
	
	public Object analyzeInstruction(){
		
		ArrayList<Register> involvedRegisters = ir.getInvolvedRegisters();
		
		Register cResolverReg = involvedRegisters.get(0);
		Register uriReg = involvedRegisters.get(1);
		Register cValuesReg = involvedRegisters.get(2);
		
		SymbolTableEntry cResolverEntry = localSymSpace.find(cResolverReg.getName());
		SymbolTableEntry uriEntry = localSymSpace.find(uriReg.getName());
		SymbolTableEntry cValuesEntry = localSymSpace.find(cValuesReg.getName());

		SymbolTableEntry returnEntry = new SymbolTableEntry();
		EntryDetails returnEntryDetails = returnEntry.getEntryDetails();
		
		SymbolTableEntry uriStringEntry;
		String dbName="";

	    Hashtable recordFieldList;
		if(cResolverEntry != null){
			recordFieldList = cResolverEntry.getEntryDetails().getRecordFieldList();
    		if(recordFieldList == null){
    			 recordFieldList = new Hashtable();
    		}
        	//NULL check would be at the retrieval anyway.
    		recordFieldList.put("uri", uriEntry);   
        	recordFieldList.put("contentValues", cValuesEntry);
        	
		    cResolverEntry.getEntryDetails().setRecordFieldList(recordFieldList);
			   
			EventFactory.getInstance().registerEvent("contentResolverInsertEvent", new ContentResolverInsertEvent());
			Event event = EventFactory.getInstance().createEvent("contentResolverInsertEvent");
			event.setName("contentResolverInsertEvent");
			
			event.getEventInfo().put("contentResolverEntry", cResolverEntry);
			event.getEventInfo().put(InstructionResponse.CLASS_NAME, ir);
			ta.setCurrCSMEvent(event);
		}
			
	    returnEntry.setInstrInfo(ir.getInstr().getText());
	    returnEntryDetails.setType(ir.getReturnType());
	    returnEntry.setEntryDetails(returnEntryDetails);
  
        logger.debug("\n ContentResolver.UpdateAnalyzer");
       return returnEntry;
	}
}
