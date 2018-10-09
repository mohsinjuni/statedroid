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
import patternMatcher.events.csm.contentresolver.ContentResolverApplyBatchEvent;
import patternMatcher.events.csm.contentresolver.ContentResolverUpdateEvent;
import taintanalyzer.TaintAnalyzer;
import taintanalyzer.instranalyzers.BaseTaintAnalyzer;
import configuration.Config;
import enums.Constants;

public class ApplyBatchAnalyzer extends BaseTaintAnalyzer{
	private InstructionResponse ir;
	private Instruction instr;
	private TaintAnalyzer ta;
	private SymbolSpace localSymSpace;

	public ApplyBatchAnalyzer(TaintAnalyzer ta){
		ir = ta.getIr();
		instr = ta.getIr().getInstr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();	
		logger = Logger.getLogger(ApplyBatchAnalyzer.class);
		this.ta = ta;
	}

/*
 *  
 * There are three ways to update a table in Android
 * 1) contentResolver.update(...)
 * 2) contentResolver.applyBatch(...)
 * 3) contentResolver.bulkInsert() 
 *        bulkInsert is typically overrided to perform faster operations as compared to applyBatch().
 *        See answer given by Viren: http://stackoverflow.com/questions/5596354/insertion-of-thousands-of-contact-entries-using-applybatch-is-slow
 * 		  It is more advanced operation, we will handle it laters.
 * 
 * getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
 * 
 *  ContactsContract.AUTHORITY = "com.android.contacts"
 *  
 *  	0x9c const-string v8, 'com.android.contacts'
		0xa0 invoke-virtual v7, v8, v3, Landroid/content/ContentResolver;->applyBatch(Ljava/lang/String; Ljava/util/ArrayList;)
		[Landroid/content/ContentProviderResult;
		
		
 * 	(non-Javadoc)
 * @see taintanalyzer.instranalyzers.BaseTaintAnalyzer#analyzeInstruction()
 */
	
	
	public Object analyzeInstruction(){
		
		ArrayList<Register> involvedRegisters = ir.getInvolvedRegisters();
		Register contentResolverReg = involvedRegisters.get(0);
		Register contractAuthorityReg = involvedRegisters.get(1);
		Register operationsReg = involvedRegisters.get(2);
		
		SymbolTableEntry contentResolverEntry = localSymSpace.find(contentResolverReg.getName());
		SymbolTableEntry contractAuthorityEntry = localSymSpace.find(contractAuthorityReg.getName());
		SymbolTableEntry operationsEntry = localSymSpace.find(operationsReg.getName());

		SymbolTableEntry returnEntry = new SymbolTableEntry();
		EntryDetails returnEntryDetails = returnEntry.getEntryDetails();
		
		SymbolTableEntry uriStringEntry;
		String dbName="";
	    String uriValue = contractAuthorityEntry.getEntryDetails().getValue();
	    
		EventFactory.getInstance().registerEvent("ContentResolverApplyBatchEvent", new ContentResolverApplyBatchEvent());
		Event event = EventFactory.getInstance().createEvent("ContentResolverApplyBatchEvent");
		event.setCurrMethodName(instr.getCurrMethodName());
		event.setCurrPkgClsName(instr.getCurrPkgClassName());
		event.setName("ContentResolverApplyBatchEvent");
		
		event.setCurrCompCallbackMethodName(ta.getCurrComponentCallback());
		event.setCurrComponentName(ta.getCurrComponentName());
		event.setCurrComponentPkgName(ta.getCurrComponentPkgName());
		
		
		if(operationsEntry != null){
			event.getEventInfo().put("operations", operationsEntry);
		    if(operationsEntry.getEntryDetails().isTainted()){
		    	returnEntryDetails.setTainted(true);
		    	
		    	ArrayList<SourceInfo> existingSiList = operationsEntry.getEntryDetails().getSourceInfoList();
		    	ArrayList<SourceInfo> siList = returnEntryDetails.getSourceInfoList();
		    	
		    	if(existingSiList != null && existingSiList.size()> 0){
		    		if(siList == null)
		    			siList = new ArrayList<SourceInfo>();
			    	for(SourceInfo si: existingSiList){
			    		if(!siList.contains(si))
			    			siList.add(si);
			    	}
		    	}
		    	returnEntryDetails.setSourceInfoList(siList);
		    }
		}
		if(contentResolverEntry != null){ // Though contentReslverEntry will be untainted, but just in case.
		    if(contentResolverEntry.getEntryDetails().isTainted()){
		    	returnEntryDetails.setTainted(true);
		    	ArrayList<SourceInfo> existingSiList = contentResolverEntry.getEntryDetails().getSourceInfoList();
		    	ArrayList<SourceInfo> siList = returnEntryDetails.getSourceInfoList();
		    	
		    	if(existingSiList != null && existingSiList.size()> 0){
		    		if(siList == null)
		    			siList = new ArrayList<SourceInfo>();
			    	for(SourceInfo si: existingSiList){
			    		if(!siList.contains(si))
			    			siList.add(si);
			    	}
		    	}
		    	returnEntryDetails.setSourceInfoList(siList);
		    }
		}
		
		Hashtable sensitiveDBUris = Constants.getInstance().getSensitiveDbUris();
		if(sensitiveDBUris.containsKey(dbName)){
			ta.setCurrCSMEvent(event);
		}
	    returnEntry.setInstrInfo(ir.getInstr().getText());
	    returnEntryDetails.setType(ir.getReturnType());
	    returnEntry.setEntryDetails(returnEntryDetails);
        logger.debug("\n ContentResolver.QueryAnalyzer");
	    return returnEntry;
	}
}
