package taintanalyzer.instranalyzers;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Stack;

import models.cfg.InstructionResponse;
import models.cfg.Register;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;

import org.apache.log4j.Logger;

import configuration.Config;

import taintanalyzer.TaintAnalyzer;

public class ReturnTaintAnalyzer  extends BaseTaintAnalyzer{

	
	private InstructionResponse ir;
	private SymbolSpace localSymSpace;
    boolean tainted=false;
    String[] used ;
    String changed;
    TaintAnalyzer ta;
    
	
	// 0x00 return-void ,0x1c return-object v0, 0x23 return v0	
	public ReturnTaintAnalyzer(TaintAnalyzer ta )
	{
		this.ir = ta.getIr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();
		this.ta = ta;
		 logger = Logger.getLogger(ReturnTaintAnalyzer.class);			
	}

//TODO In some cases, where return-object is called in a basic block other than the ending basic block, current implementation may not produce the correct
	// returned value for the obvious reasons. That BB is visited already, so return value from that function is also missed
	// by the taint analyzer.
	
	public Object analyzeInstruction()
	{
		String instrType = ir.getInstr().getTypeBySyntax();
		String text = ir.getInstr().getText();
//		if(text.contains("0xac return-object v3")){
//			System.out.println("STOPPPPPP");
//		}

		logger.debug(ir.getInstr().getText());
		
		// 0x00 return-void ,0x1c return-object v0, 0x23 return v0	
		if(instrType.equalsIgnoreCase("return-void"))
		{
			//Typically all of the <init> methods return void while producing file level or method level variables. While method
			// level variables are handled separately, we need to store file level variables for level=0 in symbolspace. 
			
			SymbolTableEntry returnEntry = null;
			
			// It's TaintAnalyzer.java that ultimately decides what to do after any of init function calls.
			if(ir.getInstr().getCurrMethodName().trim().equalsIgnoreCase("<init>")
					|| ir.getInstr().getCurrMethodName().trim().equalsIgnoreCase("init" )
					|| ir.getInstr().getCurrMethodName().trim().equalsIgnoreCase("<clinit>" ) )
			{
//				if(!ir.getInstr().getCurrClassType().equalsIgnoreCase(CLASS_TYPES.Activity.toString()))
//				{  
				// We don't need this check because all of the data will be field anyway for both Activity and Utility class.
					
				returnEntry = getRecord(ir.getInstr().getCurrPkgClassName());
					
//				}
//				else // if it is an activity.
//				{
//					
//				}
			}
			
			// TODO TODO Why do we have to return an entry from <init> method call. Existing references should 
			// already be modified and placed at level=0. 
			
			return returnEntry;
		}
		else
		{
			Register destReg = ir.getInvolvedRegisters().get(0);
	        SymbolTableEntry entry = localSymSpace.find(destReg.getName());

	       if(entry != null)
	       {
	    	   return entry;
	       }
	       else
	    	   return null;
		}
	}
	
	private SymbolTableEntry getRecord(String currPkgClsName)
	{
		SymbolTableEntry returnEntry = new SymbolTableEntry();
		
		// I guess, it should only check in hashtables under the scope of <init> function. 
		//Ideally, Hashtable should have been empty before start of <init> function.
		
//TODO Since there are three types of functions: <clinit>, <init>, and init. Need to find which order they are called.		
		Stack entries = this.localSymSpace.getEntries();
		returnEntry.getEntryDetails().setType(currPkgClsName);
		
		 for(int i=entries.size()-1; i>=0; i--) {
	        	Hashtable ht = (Hashtable) entries.get(i);
	        	if(ht!= null)
	        	{
			    	Enumeration<String> enumKey = ht.keys();
		       		while (enumKey.hasMoreElements())
		       		{
		       			  String key = enumKey.nextElement().toString();
		       			  SymbolTableEntry ent = (SymbolTableEntry) ht.get(key);
		       			  
		       			  // It is not necessarily to be a record.
//		       			  if(ent.isRecord() && ent.getType().equalsIgnoreCase(currPkgClsName)) 

		       			  // ** Since activity-><init> itself creates a variable of type 'v' but when <init> is returned,
		       			  // its name is set according to its type. So it's ok to check type here.
		       			  if(ent != null && ent.getEntryDetails().getType() != null)
		       			  {
		       				  if(ent.getEntryDetails().getType().equalsIgnoreCase(currPkgClsName)) // There should be a check for name btw.
			       			  {
				       				//We need deep copy because after returning from a function, we will remove its symboltable from the stack
		       					  
		       					  // That does not matter, I guess... because it will still hold the memory address even if we pop from the stack.
									returnEntry = (SymbolTableEntry) ent.clone(); // We can probably just return it as it is. We may not need to clone it either. 
				       				
				       				logger.debug(" stack items count after <init> methodCall " + entries.size());
				       				return returnEntry;
			       			  }
		       			  }
		       		}
	        	}
  	    }
		logger.debug(" stack size after <init> methodCall " + entries.size());
		return returnEntry;
	}

}
