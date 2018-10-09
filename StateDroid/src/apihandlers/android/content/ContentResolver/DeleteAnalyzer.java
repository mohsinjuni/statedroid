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
import patternMatcher.events.csm.contentresolver.ContentResolverDeleteEvent;
import patternMatcher.events.csm.reflection.ITelephonyEndCallEvent;


import taintanalyzer.TaintAnalyzer;
import taintanalyzer.instranalyzers.BaseTaintAnalyzer;
import configuration.Config;
import enums.Constants;

public class DeleteAnalyzer extends BaseTaintAnalyzer{
	private InstructionResponse ir;
	private Instruction instr;
	private TaintAnalyzer ta;
	private SymbolSpace localSymSpace;
	
	public DeleteAnalyzer(TaintAnalyzer ta){
		ir = ta.getIr();
		this.ta = ta;
		instr = ta.getIr().getInstr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();	
		logger = Logger.getLogger(DeleteAnalyzer.class);
	}

//	public final int delete (Uri url, String where, String[] selectionArgs)
//	e.g.
//
//	ContentResolver cr = ...;
//	String where = "nameid=?";
//	String[] args = new String[] { "george" };
//	cr.delete( Stuff.CONTENT_URI, where, args );

	public Object analyzeInstruction(){
		
		//There will be 4 arguments. 1 for callerAPI and 3 for arguments.
//		0x2f6 const-string v27, ''
//		0x2fa invoke-virtual/range v26 ... v27, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;
//		0x300 move-result-object v26
//		0x302 invoke-virtual/range v26, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;
//		0x308 move-result-object v26
//		0x30a aput-object v26, v24, v25
//		0x30e invoke-virtual/range v21 ... v24, Landroid/content/ContentResolver;->delete(Landroid/net/Uri; Ljava/lang/String; [Ljava/lang/String;)I
		
		Register callerAPIReg = ir.getInvolvedRegisters().get(0);
		Register uriReg = ir.getInvolvedRegisters().get(1);
		
		SymbolTableEntry contentResolverEntry = localSymSpace.find(callerAPIReg.getName());

		//Setting up first parameter (uri)
		Hashtable sensitiveDBUris = Constants.getInstance().getSensitiveDbUris();
		Hashtable recordFieldList;
		SymbolTableEntry uriStringEntry = null;
		
    	if(contentResolverEntry != null){
    		 recordFieldList = contentResolverEntry.getEntryDetails().getRecordFieldList();
    		 if(recordFieldList == null){
    			 recordFieldList = new Hashtable();
    		 }
    		 
			SymbolTableEntry uriEntry = localSymSpace.find(uriReg.getName());
			boolean uriEntryTainted = false;
	     	   
	        if(uriEntry != null){
	        	//Since Uri is an object, we will pass shallow copy.
	        	SymbolTableEntry clonedUriEntry=null;
	        	uriEntryTainted = uriEntry.getEntryDetails().isTainted();
	
	        	clonedUriEntry = (SymbolTableEntry) uriEntry.clone();
	        	recordFieldList.put("uri", clonedUriEntry);   // Uri uri
	        }
        
			//Setting up second parameter (projection)
			Register projectionReg = ir.getInvolvedRegisters().get(2);
			SymbolTableEntry projectionEntry = localSymSpace.find(projectionReg.getName());
	        if(projectionEntry != null){
	        
	        	SymbolTableEntry clonedProjectionEntry=null;
	       		clonedProjectionEntry = (SymbolTableEntry) projectionEntry.clone();
	        	recordFieldList.put("projection", clonedProjectionEntry);   
	        }
        
			Register selectionReg = ir.getInvolvedRegisters().get(3);
			SymbolTableEntry selectionEntry = localSymSpace.find(selectionReg.getName());
     	   
	        if(selectionEntry != null){
	        
	        	SymbolTableEntry clonedSelectionEntry=null;
	        	clonedSelectionEntry = (SymbolTableEntry) selectionEntry.clone();
	        	recordFieldList.put("selection", clonedSelectionEntry);   
	        }
	        contentResolverEntry.getEntryDetails().setRecordFieldList(recordFieldList);

	        EventFactory.getInstance().registerEvent("contentResolverDeleteEvent", new ContentResolverDeleteEvent());
			Event contentResolverDeleteEvent = EventFactory.getInstance().createEvent("contentResolverDeleteEvent");
			contentResolverDeleteEvent.setName("contentResolverDeleteEvent");
			contentResolverDeleteEvent.getEventInfo().put("contentResolverEntry", contentResolverEntry);
			contentResolverDeleteEvent.getEventInfo().put(InstructionResponse.CLASS_NAME, ir);
				
			ta.setCurrCSMEvent(contentResolverDeleteEvent);   
    	}
    	return null;
	}
}
