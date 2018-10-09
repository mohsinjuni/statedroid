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
import patternMatcher.events.csm.contentresolver.ContentResolverUpdateEvent;
import taintanalyzer.TaintAnalyzer;
import taintanalyzer.instranalyzers.BaseTaintAnalyzer;
import configuration.Config;
import enums.Constants;

public class UpdateAnalyzer extends BaseTaintAnalyzer{
	private InstructionResponse ir;
	private Instruction instr;
	private TaintAnalyzer ta;
	private SymbolSpace localSymSpace;

	public UpdateAnalyzer(TaintAnalyzer ta){
		ir = ta.getIr();
		instr = ta.getIr().getInstr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();	
		logger = Logger.getLogger(UpdateAnalyzer.class);
		this.ta = ta;
	}

//	0xce invoke-virtual v2, v4, v12, v0, v1, Landroid/content/ContentResolver;->update (Landroid/net/Uri; 
//	Landroid/content/ContentValues; Ljava/lang/String; [Ljava/lang/String;)I
	
// Prototypes:
//    update(Uri uri, ContentValues values, String where, String[] selectionArgs)
//	  
//	Update row(s) in a content URI.
//
//	
//	v2 = ContentResolver, v4 = Uri, v12= ContentValues, v0= where,  v1=selectionArgs
/*
 * 
 * 
 * There are three ways to update a table in Android
 * 1) contentResolver.update(...)
 * 2) contentResolver.applyBatch(...)
 * 3) contentResolver.bulkInsert() 
 *        bulkInsert is typically overrided to perform faster operations as compared to applyBatch().
 * 
 * 	(non-Javadoc)
 * @see taintanalyzer.instranalyzers.BaseTaintAnalyzer#analyzeInstruction()
 */
	
	public Object analyzeInstruction(){
		
		ArrayList<Register> involvedRegisters = ir.getInvolvedRegisters();
		Register cResolverReg = involvedRegisters.get(0);
		Register uriReg = involvedRegisters.get(1);
		Register cValuesReg = involvedRegisters.get(2);
		Register whereReg = involvedRegisters.get(3);
		Register selecReg = involvedRegisters.get(4);
		
		SymbolTableEntry cResolverEntry = localSymSpace.find(cResolverReg.getName());
		SymbolTableEntry uriEntry = localSymSpace.find(uriReg.getName());
		SymbolTableEntry cValuesEntry = localSymSpace.find(cValuesReg.getName());
		SymbolTableEntry selecEntry = localSymSpace.find(selecReg.getName());

		SymbolTableEntry returnEntry = new SymbolTableEntry();
		EntryDetails returnEntryDetails = returnEntry.getEntryDetails();
		
		SymbolTableEntry uriStringEntry;
		String dbName="";
	    String uriValue = uriEntry.getEntryDetails().getValue();

	    Hashtable recordFieldList;
		if(cResolverEntry != null){
			recordFieldList = cResolverEntry.getEntryDetails().getRecordFieldList();
    		if(recordFieldList == null){
    			 recordFieldList = new Hashtable();
    		}
    		 
		    if(uriEntry != null){
	        	recordFieldList.put("uri", uriEntry);   // Uri uri
		    }
		    cResolverEntry.getEntryDetails().setRecordFieldList(recordFieldList);
			   
			EventFactory.getInstance().registerEvent("ContentResolverUpdateEvent", new ContentResolverUpdateEvent());
			Event event = EventFactory.getInstance().createEvent("ContentResolverUpdateEvent");
			event.setName("ContentResolverUpdateEvent");
			event.getEventInfo().put("contentResolverEntry", cResolverEntry);
			event.getEventInfo().put(InstructionResponse.CLASS_NAME, ir);
		}
			
	    returnEntry.setInstrInfo(ir.getInstr().getText());
	    returnEntryDetails.setType("I");
	    returnEntry.setEntryDetails(returnEntryDetails);
  
        logger.debug("\n ContentResolver.UpdateAnalyzer");
       return returnEntry;
	}
}
