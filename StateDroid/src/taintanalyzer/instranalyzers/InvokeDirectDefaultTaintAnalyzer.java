package taintanalyzer.instranalyzers;


import java.util.ArrayList;
import java.util.Hashtable;

import models.cfg.InstructionResponse;
import models.cfg.Register;
import models.symboltable.EntryDetails;
import models.symboltable.SourceInfo;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;

import org.apache.log4j.Logger;

import taintanalyzer.TaintAnalyzer;
import configuration.Config;

public class InvokeDirectDefaultTaintAnalyzer  extends BaseTaintAnalyzer{

	
	private InstructionResponse ir;
	private SymbolSpace localSymSpace;
    boolean tainted=false;
    String[] used ;
    String changed;
    ArrayList<Register> involvedRegisters;
    SymbolTableEntry returnEntry ;


    // This class handles all API calls which have no specified handler.
    // It assumes that if input is tainted, output is tainted.

	// If method returns something, it returns and stores via move-result, And if the method is <init>, it creates that object
    // and stores it in the first variable.
    
    /*
     * Case#1
     * 0x1a2 invoke-direct v0, Ljava/lang/StringBuffer;-><init>()V    ====> v0 refers to StringBuilder
     * 
     * Case#2
     * 0x56 invoke-direct v6, v5, Lcom/a/a/a/e/k/f;->a(I)I          ====> the result is stored in v3, see InvokeDirectTaintAnalyzer for details.
	   0x5c move-result v3
     */
    	   
	public InvokeDirectDefaultTaintAnalyzer(TaintAnalyzer ta )
	{
		this.ir = ta.getIr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();
		involvedRegisters = new ArrayList<Register>();
		returnEntry = new SymbolTableEntry();
		 logger = Logger.getLogger(InvokeDirectDefaultTaintAnalyzer.class);			
	}
	
	public Object analyzeInstruction()
	{
		boolean tainted = false;	
		involvedRegisters = ir.getInvolvedRegisters();

	   String returnType = ir.getReturnType();
	   String methodOrObjectName = ir.getMethodOrObjectName();
	   
	   if(involvedRegisters.size() > 0)
	   {
		   Register callereReg = involvedRegisters.get(0);
		   SymbolTableEntry callerEntry; // = this.localSymSpace.find(callereReg.getName());

		   //Case#1
		   if(returnType.trim().equalsIgnoreCase("V"))
		   {
			   if(methodOrObjectName.trim().equalsIgnoreCase("<init>")) //I've seen only <init> with invoke-direct, not init OR <clinit>
			   {

				   // URL instance is stored in v5. This happens only for all sort of constructors.
				   // 0x4e invoke-direct v5, v3, Ljava/net/URL;-><init>(Ljava/lang/String;)V
			   
//					   // We make one exception though, to handle instructions like URL.init(asd). 
//					   // if v5.getType() != currPkgClassName (((( this ))))
//					   String curPkgClassName = ir.getInstr().getCurrPkgClassName();
				   

					/*
					 * 	0x5a new-instance v11, Lorg/apache/http/client/methods/HttpPost;
							0x5e move-object v0, v11
							0x60 move-object/from16 v1, v20  //v1 is tainted here.
							0x64 invoke-direct v0, v1, Lorg/apache/http/client/methods/HttpPost;-><init>(Ljava/lang/String;)V
							// v0 gets tainted.
							 
							0x72 move-object v0, v11    //v11 should have been updated already because of shallow copy.
							0x74 move-object/from16 v1, v16
					 * 
					 * 
					 */
				   Hashtable immutableObjects = Config.getInstance().getImmutableObjects();
				   callerEntry = this.localSymSpace.find(callereReg.getName());
				   SymbolTableEntry destEntry = null;
				   
				   if(callerEntry != null)
				   {

					   destEntry = (SymbolTableEntry) callerEntry.clone() ; //make shallow copy
					   
					   EntryDetails destEntryDetails = destEntry.getEntryDetails();

					   ArrayList<SourceInfo> srcInfoList = destEntryDetails.getSourceInfoList();
					   for(int i=1; i < involvedRegisters.size(); i++)
					   {
						   Register reg = involvedRegisters.get(i);
						   
						   SymbolTableEntry entry = this.localSymSpace.find(reg.getName());
						   
						   if(entry != null)
						   {
							   if(entry.getEntryDetails().isTainted())
							   {
								   tainted = true;
								   ArrayList<SourceInfo> entryInfoList = entry.getEntryDetails().getSourceInfoList();
			    				   if(entryInfoList != null && entryInfoList.size() >0)
			    				   {
			    					   if(srcInfoList == null)
			    						   srcInfoList = new ArrayList<SourceInfo>();
									   for(SourceInfo si : entryInfoList)
									   {
										   if(!srcInfoList.contains(si))
										   {
											   srcInfoList.add(si);
											   //TODO hopefully, a reference would be fine here.
										   }
									   }
			    				   }
							   }
						   }
					   }
					   if(! destEntryDetails.isTainted()) // if not tainted already, 
						   destEntryDetails.setTainted(tainted);
					   
					   	destEntryDetails.setSourceInfoList(srcInfoList);
					   	
					   	//Set type to the existing-child class, if any, instead of parent class.
					   	String childType = callerEntry.getEntryDetails().getType();
					   	if(childType.isEmpty()){
					   		destEntryDetails.setType(ir.getCallerAPIName());
					   	}
					   	destEntry.setEntryDetails(destEntryDetails);
					   	
					   	this.localSymSpace.addEntry(destEntry);
					   	
				   }
				   else
				   {
					   destEntry = new SymbolTableEntry();
					   EntryDetails destEntryDetails = destEntry.getEntryDetails();
	
					   destEntry.setName(callereReg.getName());
					   destEntry.setInstrInfo(ir.getInstr().getText());
					   destEntry.setLineNumber(ir.getLineNumber());
					   
					   ArrayList<SourceInfo> srcInfoList = destEntryDetails.getSourceInfoList();
					   for(int i=1; i < involvedRegisters.size(); i++)
					   {
						   Register reg = involvedRegisters.get(i);
						   
						   SymbolTableEntry entry = this.localSymSpace.find(reg.getName());
						   
						   if(entry != null)
						   {
							   if(entry.getEntryDetails().isTainted())
							   {
								   tainted = true;
								   ArrayList<SourceInfo> entryInfoList = entry.getEntryDetails().getSourceInfoList();
			    				   if(entryInfoList != null && entryInfoList.size() >0)
			    				   {
			    					   if(srcInfoList == null)
			    						   srcInfoList = new ArrayList<SourceInfo>();
									   for(SourceInfo si : entryInfoList)
									   {
										   if(!srcInfoList.contains(si))
										   {
											   srcInfoList.add(si);
											   //TODO hopefully, a reference would be fine here.
										   }
									   }
			    				   }
							   }
						   }
					   }
					   if(! destEntryDetails.isTainted()) // if not tainted already, 
						   destEntryDetails.setTainted(tainted);
					   
					   	destEntryDetails.setSourceInfoList(srcInfoList);
					   	destEntryDetails.setType(ir.getCallerAPIName());
					   	
					   	
					   	destEntry.setEntryDetails(destEntryDetails);
					   	
					   	this.localSymSpace.addEntry(destEntry); //Even if the entry exists, it will be replaced with new one.
				   }
			   }
			   else
			   {
				   // When method name is not <init>, in that case, it just does nothing. Because even if any of the input
				   // params is tainted, no varible is affected.
				   
				   // In this case, we can make the caller as tainted if any of input params is tainted. But we DONT want to taint 'this'
				   //variable because thay may generate a lot of false positives. So we apply this rule for non-this variables.
				   
				   callerEntry=  this.localSymSpace.find(callereReg.getName());
				   
				   String qualifiedAPIName = ir.getCallerAPIName().concat("->").concat(ir.getMethodOrObjectName());
				   
				   String currPkgClassName = ir.getInstr().getCurrPkgClassName();
				   
				   if(!currPkgClassName.equalsIgnoreCase(qualifiedAPIName))
				   {
					   if(callerEntry != null)
					   {
						   EntryDetails callerEntryDetails = callerEntry.getEntryDetails();
						   
						   callerEntry.setName(callereReg.getName());
						   callerEntry.setInstrInfo(ir.getInstr().getText());
						   callerEntry.setLineNumber(ir.getLineNumber());
						   
						   ArrayList<SourceInfo> srcInfoList = callerEntryDetails.getSourceInfoList();
						   
						   for(int i=1; i < involvedRegisters.size(); i++)
						   {
							   Register reg = involvedRegisters.get(i);
							   
							   SymbolTableEntry entry = this.localSymSpace.find(reg.getName());
							   
							   if(entry != null)
							   {
								   if(entry.getEntryDetails().isTainted())
								   {
									   tainted = true;
									   ArrayList<SourceInfo> entryInfoList = entry.getEntryDetails().getSourceInfoList();
				    				   if(entryInfoList != null && entryInfoList.size() >0)
				    				   {
				    					   if(srcInfoList == null)
				    						   srcInfoList = new ArrayList<SourceInfo>();
										   for(SourceInfo si : entryInfoList)
										   {
											   if(!srcInfoList.contains(si))
											   {
												   srcInfoList.add(si);
												   //TODO hopefully, a reference would be fine here.
											   }
										   }
				    				   }
								   }
							   }
						   }
						   if(! callerEntryDetails.isTainted()) // if not tainted already, 
							   callerEntryDetails.setTainted(tainted);
						   
						   	callerEntryDetails.setSourceInfoList(srcInfoList);
						   	callerEntryDetails.setType(ir.getCallerAPIName());
						   	
						   	
						   	callerEntry.setEntryDetails(callerEntryDetails);
						   
					   }
				   }
			   }
		   		logger.debug("\n InvokeDirectDefaultTaintAnalyzer");
			    localSymSpace.logInfoSymbolSpace();
			    
			   return null;
		   }
		   else // if return type is not V.
		   { 
		   
			   // CASE#2
//				     * 0x56 invoke-direct v6, v5, Lcom/a/a/a/e/k/f;->a(I)I
//					   0x5c move-result v3

			   //A special case.
			   
			/*
			 * 	0x5a new-instance v11, Lorg/apache/http/client/methods/HttpPost;
					0x5e move-object v0, v11
					0x60 move-object/from16 v1, v20  //v1 is tainted here.
					0x64 invoke-direct v0, v1, Lorg/apache/http/client/methods/HttpPost;-><init>(Ljava/lang/String;)V
					// v0 gets tainted.
					 
					0x72 move-object v0, v11    //v11 should have been updated already because of shallow copy.
					0x74 move-object/from16 v1, v16
			 * 
			 * 
			 */
//			   	if an entry exists and instruction return type is not one of immutable objects, we will make shallow copy.
			   //otherwise deep copy.
			   
			   Hashtable immutableObjects = Config.getInstance().getImmutableObjects();
			   callerEntry = this.localSymSpace.find(callereReg.getName());
			   
			   SymbolTableEntry destEntry=null; 
			   if(callerEntry != null)
			   {
				   if(immutableObjects.containsKey(returnType.trim()))
				   {
					   destEntry = new SymbolTableEntry(callerEntry); //make deep copy
				   }
				   else
				   {
					   destEntry = (SymbolTableEntry) callerEntry.clone() ; //make shallow copy
				   }
			   }
			   else
			   {
				   destEntry = new SymbolTableEntry();
			   }
			   
			   EntryDetails destEntryDetails = destEntry.getEntryDetails();

			   ArrayList<SourceInfo> srcInfoList = destEntryDetails.getSourceInfoList();
			   
			   for(int i=0; i < involvedRegisters.size(); i++)   //We start from zero. we check all of them if they are tainted.
			   {
				   Register reg = involvedRegisters.get(i);
				   
				   SymbolTableEntry entry = this.localSymSpace.find(reg.getName());
				   
				   if(entry != null)
				   {
					   if(entry.getEntryDetails().isTainted())
					   {
						   tainted = true;
						   ArrayList<SourceInfo> entryInfoList = entry.getEntryDetails().getSourceInfoList();
						   
	    				   if(entryInfoList != null && entryInfoList.size() >0)
	    				   {
	    					   if(srcInfoList == null)
	    						   srcInfoList = new ArrayList<SourceInfo>();

							   for(SourceInfo si : entryInfoList)
							   {
								   if(!srcInfoList.contains(si))
								   {
									   srcInfoList.add(si);
									   //TODO hopefully, a reference would be fine here.?
								   }
							   }
	    				   }
					   }
				   }
			   }
			   destEntryDetails.setTainted(tainted);
	    	   
	    	   destEntry.setName(""); //any name you give here does not matter. It will be set by move instruction.
	    	   destEntry.setLineNumber(ir.getLineNumber());
	    	   destEntry.setInstrInfo(ir.getInstr().getText());

	    	   destEntryDetails.setType(returnType);
	    	   destEntryDetails.setConstant(false);
	    	   destEntryDetails.setField(false);
	    	   destEntryDetails.setRecord(false); 
	    	   
	    	   destEntryDetails.setValue("");
	    	   
	    	   destEntryDetails.setSourceInfoList(srcInfoList);
	    	   destEntry.setEntryDetails(destEntryDetails);
		     	  
			    return destEntry;
				    	   
		   }
   
	   }
	   return null;

	}
}
