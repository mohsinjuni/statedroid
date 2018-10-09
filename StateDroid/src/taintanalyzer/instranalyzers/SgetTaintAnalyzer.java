package taintanalyzer.instranalyzers;


import handler.InvokeStaticHandler;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Properties;

import models.cfg.APK;
import models.cfg.CFG;
import models.cfg.Instruction;
import models.cfg.InstructionResponse;
import models.cfg.MethodSignature;
import models.cfg.Register;
import models.symboltable.SourceInfo;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;

import org.apache.log4j.Logger;

import taintanalyzer.TaintAnalyzer;
import configuration.Config;
import enums.Constants;

public class SgetTaintAnalyzer  extends BaseTaintAnalyzer {

	
	private InstructionResponse ir;
    boolean tainted=false;
    String[] used ;
    String changed;
    SymbolTableEntry destEntry;
    SymbolTableEntry destGlobalEntry;
    Register destReg;
    TaintAnalyzer ta;
    
	public SgetTaintAnalyzer(TaintAnalyzer ta )
	{
		this.ir = ta.getIr();
		 logger = Logger.getLogger(SgetTaintAnalyzer.class);		
		 this.ta = ta;
	}

// 	0x2 sget-object v2, Landroid/os/Build;->DEVICE Ljava/lang/String;		
	/*
	 *  Sget gets static fields of built-in or user-defined classes. So we store such fields in global symbol space. There are going to be
	 *  many ones and global symbol space is going to consume a lot of memory. 
	 * 
	 *   
	 * (non-Javadoc)
	 * @see analyzer.BaseTaintAnalyzer#analyzeInstruction()
	 */
	
//	0x22 sget-object v2, Lcom/geinimi/c/j;->a Ljava/lang/String;
	public Object analyzeInstruction()
	{

		
		
		SymbolSpace localSymSpace = Config.getInstance().getLocalSymbolSpace();
		SymbolSpace globalSymSpace = Config.getInstance().getGlobalSymbolSpace();

		
	   destReg = ir.getInvolvedRegisters().get(0); //v2

       String srcDestType = "";
       SymbolTableEntry field = null ; //= new SymbolTableEntry();
       
       String qualifiedAPIName = ir.getCallerAPIName().trim().concat("->").concat(ir.getMethodOrObjectName().trim());

	   Properties sourceSinkAPIMap = Config.getInstance().getSourceSinkAPIMap();
		
	   String apiInfo = "";
	   
	   Instruction instr = ir.getInstr();
	   apiInfo = String.valueOf(" [PkgClass] = ").concat(instr.getCurrPkgClassName()
			 .concat (" , [method] = ").concat(instr.getCurrMethodName()) );
		
//		0x22 sget-object v2, Lcom/geinimi/c/j;->a Ljava/lang/String;

	   Hashtable sensitiveDbUris = Constants.getInstance().getSensitiveDbUris();
		
	   //Check for source data.
	    if(sourceSinkAPIMap.containsKey(qualifiedAPIName))
	    {
			destEntry = new SymbolTableEntry(); 
			destEntry.setInstrInfo(ir.getInstr().getText());
			destEntry.setLineNumber(ir.getLineNumber());
			destEntry.setName(destReg.getName());
			destEntry.getEntryDetails().setType(ir.getReturnType()); // Just to make sure.
			
			destEntry.getEntryDetails().setTainted(true);
			
			SourceInfo srcInfo = new SourceInfo();
			srcInfo.setSrcAPI(qualifiedAPIName);
			srcInfo.setSrcInstr(ir.getInstr().getText());
			
//			destEntry.getEntryDetails().getSourceInfoList().add(srcInfo);
			
	 	    ArrayList<SourceInfo> siList = destEntry.getEntryDetails().getSourceInfoList();
	 	    if(siList == null)
	 		   siList = new ArrayList<SourceInfo>();
	 	   
	 	    if(!siList.contains(srcInfo))
	 		   siList.add(srcInfo);
	 	   
	 	     destEntry.getEntryDetails().setSourceInfoList(siList);
	 	     
			localSymSpace.addEntry(destEntry);
		}
	    else if(sensitiveDbUris.containsKey(qualifiedAPIName)) //I dont remember how this case will ever be true. I don't remember the example now
	    {
			destEntry = new SymbolTableEntry(); 
			destEntry.setInstrInfo(ir.getInstr().getText());
			destEntry.setLineNumber(ir.getLineNumber());
			destEntry.setName(destReg.getName());
			destEntry.getEntryDetails().setType(ir.getReturnType()); // Just to make sure.
			
			destEntry.getEntryDetails().setValue(qualifiedAPIName);
			destEntry.getEntryDetails().setTainted(false);
									
			localSymSpace.addEntry(destEntry);
	    }
	    else
	    {
		    SymbolTableEntry srcEntry = globalSymSpace.find(qualifiedAPIName);
		   
			if(srcEntry!= null)
			{
				destEntry = new SymbolTableEntry(srcEntry); // deep copy
				destEntry.setInstrInfo(ir.getInstr().getText());
				destEntry.setLineNumber(ir.getLineNumber());
				destEntry.setName(destReg.getName());
				destEntry.getEntryDetails().setType(ir.getReturnType()); // Just to make sure.
				
				localSymSpace.addEntry(destEntry);
				
			}
			else
			{
				// If symboltable entry is not available, find that class and its clinit method and analyze it for analysis.
				/*
				 * 
				 * 
				 * [currClass]=Lcom/google/android/v54new/sqlite/call/Call; : [parentClass]=Ljava/lang/Object;
					Lcom/google/android/v54new/sqlite/call/Call; <clinit> ()V 0
					<clinit>-BB@0x0 0x0 0x12[ NEXT =  ] [ PREV = ] 
						0x0 const-string v0, 'content://call_log/calls'
						0x4 invoke-static v0, Landroid/net/Uri;->parse(Ljava/lang/String;)Landroid/net/Uri;
						0xa move-result-object v0
						0xc sput-object v0, Lcom/google/android/v54new/sqlite/call/Call;->CONTENT_URI Landroid/net/Uri;
						0x10 return-void 
				 * 
				 * 
				 * 
				 * 
				 */
				
//				System.out.println(ir.getInstr().getText());
				String text = "		0xcc invoke-static " + ir.getCallerAPIName() + "-><clinit>()V";

				Instruction newInstr = new Instruction();
				newInstr.setText(text);
				InvokeStaticHandler invokeHandler = new InvokeStaticHandler(newInstr, null);
				InstructionResponse newIR = invokeHandler.execute();

				logger.debug("<isntr> = " + text);
				MethodHandler mHandler = new MethodHandler(ta);
				mHandler.setIr(newIR);
				
		   	    MethodSignature ms = mHandler.getMethodSignatureFromCurrInstruction(newIR);

		   	    APK apk = ta.getApk();
		   	    if(ms != null)
		 	    {
		 		   CFG cfg = apk.findMethodBySignature(ms);
		 		   if(cfg != null)
		 		   {
		 			   logger.debug("cfg key -> " + cfg.getKey());
		 			   logger.trace("[InvokeTaintAnalyzer] from caller instr:: " + ms.getParams().size());
		 			   logger.trace("[InvokeTaintAnalyzer] from apk found cfg:: " + cfg.getSignature().getParams().size());

		  			   boolean result = mHandler.handleMethodCall(cfg);
		 	 			  
		  			   //<clinit>() methods don't return anything, usually at least.
		  			   //So, after invoking its method, we check again for existence of the source entry.
		  			   
		  			    srcEntry = globalSymSpace.find(qualifiedAPIName);
		  				if(srcEntry!= null)
		  				{
		  					String returnType = ir.getReturnType();
		  					
	  						// It needs deep copy.
	  						destEntry = new SymbolTableEntry(srcEntry); // deep copy
	  						destEntry.setInstrInfo(ir.getInstr().getText());
	  						destEntry.setLineNumber(ir.getLineNumber());
	  						destEntry.setName(destReg.getName());
	  						destEntry.getEntryDetails().setType(ir.getReturnType()); // Just to make sure.
	  						
	  						localSymSpace.addEntry(destEntry);
		  			   
		  				}
		  				else
		  				{
		  					//if it is still null, then add a dummy entry.
		  					destEntry = new SymbolTableEntry(); 
		  					destEntry.setInstrInfo(ir.getInstr().getText());
		  					destEntry.setLineNumber(ir.getLineNumber());
		  					destEntry.setName(destReg.getName());
		  					destEntry.getEntryDetails().setType(ir.getReturnType()); // Just to make sure.
		  					
		  					localSymSpace.addEntry(destEntry);

		  				}
		 		   	}	 	    
		 		}
			}
	    }

	    logger.debug("\n SgetTaintAnalyzer");
       logger.debug("\n Printing Global SymSpace");
       globalSymSpace.logInfoSymbolSpace();
        return null;
	}
	

}
