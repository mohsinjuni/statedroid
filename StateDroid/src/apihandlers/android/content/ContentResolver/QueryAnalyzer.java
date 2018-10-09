package apihandlers.android.content.ContentResolver;

import java.util.ArrayList;
import java.util.Hashtable;

import models.cfg.Instruction;
import models.cfg.InstructionResponse;
import models.cfg.InstructionReturnValue;
import models.cfg.Register;
import models.symboltable.EntryDetails;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;

import org.apache.log4j.Logger;

import patternMatcher.events.Event;
import patternMatcher.events.EventFactory;
import patternMatcher.events.csm.contentresolver.ContentResolverDeleteEvent;
import patternMatcher.events.csm.contentresolver.ContentResolverQueryEvent;

import taintanalyzer.TaintAnalyzer;
import taintanalyzer.instranalyzers.BaseTaintAnalyzer;
import configuration.Config;

public class QueryAnalyzer extends BaseTaintAnalyzer{
	private InstructionResponse ir;
	private Instruction instr;
	private TaintAnalyzer ta;
	private SymbolSpace localSymSpace;

	public QueryAnalyzer(TaintAnalyzer ta){
		ir = ta.getIr();
		instr = ta.getIr().getInstr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();	
		logger = Logger.getLogger(QueryAnalyzer.class);
	}

//	0x1c invoke-virtual/range v0 ... v5, Landroid/content/ContentResolver;->query (Landroid/net/Uri;
//	[Ljava/lang/String; Ljava/lang/String; [Ljava/lang/String; Ljava/lang/String;)Landroid/database/Cursor;
	
// Prototypes:
	// public final Cursor query (Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder)
	// public final Cursor query (Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder, CancellationSignal cancellationSignal)

	public Object analyzeInstruction(){
		
		SymbolTableEntry cursorEntry = new SymbolTableEntry();
		Hashtable recordFieldList = new Hashtable();

		// We will set each field one by one into the Cursor object.
		ArrayList<Register> involvedRegisters = ir.getInvolvedRegisters();
		
		//Setting up first parameter (uri)
		Register uriReg = ir.getInvolvedRegisters().get(1);
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
		boolean projectEntryTainted = false;
     	   
        if(projectionEntry != null){
        	SymbolTableEntry clonedProjectionEntry=null;
        	projectEntryTainted = projectionEntry.getEntryDetails().isTainted();
       		clonedProjectionEntry = (SymbolTableEntry) projectionEntry.clone();
        	recordFieldList.put("projection", clonedProjectionEntry);   
        }
        
        //Setting up third parameter (selection)
		Register selectionReg = ir.getInvolvedRegisters().get(3);
		SymbolTableEntry selectionEntry = localSymSpace.find(selectionReg.getName());
		boolean selectionEntryTainted = false;
     	   
        if(selectionEntry != null){
        
        	SymbolTableEntry clonedSelectionEntry=null;
        	selectionEntryTainted = selectionEntry.getEntryDetails().isTainted();
        	clonedSelectionEntry = (SymbolTableEntry) selectionEntry.clone();
        	recordFieldList.put("selection", clonedSelectionEntry);   
        }
        
        //Setting up fourth parameter (selectionArgs)
		Register selectionArgsReg = ir.getInvolvedRegisters().get(4);
		SymbolTableEntry selectionArgsEntry = localSymSpace.find(selectionArgsReg.getName());
		boolean selectionArgsEntryTainted = false;
		
        if(selectionArgsEntry != null){
        	SymbolTableEntry clonedSelectionArgsEntry=null;
        	selectionArgsEntryTainted = selectionArgsEntry.getEntryDetails().isTainted();
    		clonedSelectionArgsEntry = (SymbolTableEntry) selectionArgsEntry.clone();
        	recordFieldList.put("selectionArgs", clonedSelectionArgsEntry);  
        }
        
        //Setting up fifth parameter (sortOrder), though this entry does not matter much as of now.
		Register sortOrderReg = ir.getInvolvedRegisters().get(5);
		SymbolTableEntry sortOrderEntry = localSymSpace.find(sortOrderReg.getName());
		boolean sortOrderEntryTainted = false;
		
        if(sortOrderEntry != null){
        	SymbolTableEntry clonedSortOrderEntry=null;
        	sortOrderEntryTainted = sortOrderEntry.getEntryDetails().isTainted();
    		clonedSortOrderEntry =(SymbolTableEntry) sortOrderEntry.clone();
        	recordFieldList.put("sortOrder", clonedSortOrderEntry);  
        }

		boolean cancelSigEntryTainted = false;
        // For the second prototype.
        if(involvedRegisters.size() == 7){
	        //Setting up second parameter (cancellationSignal)
			Register cancellationSignalReg = ir.getInvolvedRegisters().get(6);
			SymbolTableEntry cancellationSignalEntry = localSymSpace.find(cancellationSignalReg.getName());
			
	        if(cancellationSignalEntry != null){
	        	SymbolTableEntry clonedCancellationSignalEntry=null;
	        	cancelSigEntryTainted = cancellationSignalEntry.getEntryDetails().isTainted();
        		clonedCancellationSignalEntry = (SymbolTableEntry) cancellationSignalEntry.clone();
	        	recordFieldList.put("cancellationSignal", clonedCancellationSignalEntry);  
	        }
        }

        // Setting up the cursor object
        //  cursorEntry.setName(destReg.getName());   Name will be set by 'move' instruction
        SymbolTableEntry calledApiEntry = localSymSpace.find(involvedRegisters.get(0).getName());
        EntryDetails cursorEntryDetails = cursorEntry.getEntryDetails();
        
        cursorEntry.setLineNumber(ir.getLineNumber());
        cursorEntry.setInstrInfo("");
        cursorEntryDetails.setType(ir.getReturnType());
        
        cursorEntryDetails.setRecordFieldList(recordFieldList);
        cursorEntryDetails.setRecord(true);
        cursorEntry.setEntryDetails(cursorEntryDetails);
        
		EventFactory.getInstance().registerEvent("contentResolverQueryEvent", new ContentResolverQueryEvent());
		Event contentResolverQueryEvent = EventFactory.getInstance().createEvent("contentResolverQueryEvent");
		contentResolverQueryEvent.setName("contentResolverQueryEvent");

		InstructionReturnValue irValue = new InstructionReturnValue(cursorEntry, contentResolverQueryEvent);

        logger.debug("\n ContentResolver.QueryAnalyzer");
       return irValue;
	}
}
