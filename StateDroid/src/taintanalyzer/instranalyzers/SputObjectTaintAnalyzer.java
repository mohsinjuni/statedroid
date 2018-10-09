package taintanalyzer.instranalyzers;


import java.util.Hashtable;

import models.cfg.APK;
import models.cfg.ClassObj;
import models.cfg.InstructionResponse;
import models.cfg.Register;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;

import org.apache.log4j.Logger;

import taintanalyzer.TaintAnalyzer;
import configuration.Config;

public class SputObjectTaintAnalyzer  extends BaseTaintAnalyzer{

	
	private InstructionResponse ir;
    boolean tainted=false;
    String[] used ;
    String changed;
    SymbolTableEntry destEntry;
    SymbolTableEntry srcEntry;
    Register srcReg;
    APK apk;


	public SputObjectTaintAnalyzer(TaintAnalyzer ta )
	{
		this.ir = ta.getIr();
		 logger = Logger.getLogger(SputObjectTaintAnalyzer.class);			
		 apk = ta.getApk();
	}
//	0xa sput-object v0, Lcom/geinimi/a/d;->a Ljava/lang/String;
// we want to put value of v0=source into Lcom/geinimi/a/d;->a object

	public Object analyzeInstruction()
	{
		SymbolSpace localSymSpace = Config.getInstance().getLocalSymbolSpace();
		SymbolSpace globalSymSpace = Config.getInstance().getGlobalSymbolSpace();
		

		   srcReg = ir.getInvolvedRegisters().get(0);

		   String objectName = ir.getCallerAPIName();
		   String fieldName = ir.getMethodOrObjectName();
	       String qualifiedAPIName = ir.getCallerAPIName().trim().concat("->").concat(ir.getMethodOrObjectName().trim());
		   
	       Hashtable immutableObjects = Config.getInstance().getImmutableObjects();
	       
	       srcEntry = localSymSpace.find(srcReg.getName());
	       
	       if(srcEntry != null)  //v0 value to be stored 
	       {
	    	   String returnType = ir.getReturnType().trim();
	    	   
	    	   if(immutableObjects.containsKey(returnType))
		       {
	    	    	 destEntry = new SymbolTableEntry(srcEntry); //deep copy
		    	}
	    	    else
	    	    {
	    	    	destEntry = (SymbolTableEntry) srcEntry.clone(); //shallow copy
	    	    }
	       }
	       else
	       {
	    	    destEntry = new SymbolTableEntry(); 
	       }	   
			destEntry.setInstrInfo(ir.getInstr().getText());
			destEntry.setLineNumber(ir.getLineNumber());
			destEntry.setName(qualifiedAPIName);
//			destEntry.getEntryDetails().setType(ir.getReturnType());
			
			//if there is some type available already, just use that as return type. Best would be to check if there is a parent-child relationship.
			// if yes, put the child as existing one. But parent could be Object of everything.
			if(srcEntry != null){
				String srcEntryType = srcEntry.getEntryDetails().getType();
				if(!srcEntryType.isEmpty()){
					destEntry.getEntryDetails().setType(srcEntryType);
					
				}else{
					destEntry.getEntryDetails().setType(ir.getReturnType());
				}
			}else{
				destEntry.getEntryDetails().setType(ir.getReturnType());
			}
			
			globalSymSpace.addEntry(destEntry);
	       
	       logger.debug("\n SputObject-TaintAnalyzer");
	       logger.debug("\n Printing Global SymSpace");
	       globalSymSpace.logInfoSymbolSpace();
	       
	       return null;
	}

}
