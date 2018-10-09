package apihandlers.android.telephony.SmsMessage;

import java.util.ArrayList;

import models.cfg.Instruction;
import models.cfg.InstructionResponse;
import models.manifest.AndroidManifest;
import models.manifest.ComponentManifest;
import models.symboltable.EntryDetails;
import models.symboltable.SourceInfo;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;

import org.apache.log4j.Logger;

import patternMatcher.events.Event;
import patternMatcher.events.EventFactory;
import patternMatcher.events.csm.AbortBroadcastEvent;
import patternMatcher.events.csm.CreateFromPduEvent;
import patternMatcher.events.csm.GetExtrasBundleEvent;

import taintanalyzer.TaintAnalyzer;
import taintanalyzer.instranalyzers.BaseTaintAnalyzer;
import configuration.Config;

public class CreateFromPduAnalyzer extends BaseTaintAnalyzer{

	
	private InstructionResponse ir;
	private Instruction instr;
	private TaintAnalyzer ta;
	private SymbolSpace localSymSpace;

	public CreateFromPduAnalyzer(TaintAnalyzer ta)
	{
		ir = ta.getIr();
		instr = ta.getIr().getInstr();
		this.ta = ta;
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();	
		logger = Logger.getLogger(CreateFromPduAnalyzer.class);
	}

//	0x2e invoke-static v8, Landroid/telephony/SmsMessage;->createFromPdu([B)Landroid/telephony/SmsMessage;
//	0x34 move-result-object v1
//	
	
	public Object analyzeInstruction()
	{

		String pkgClsName = ir.getCallerAPIName();
		String methdObjectName = ir.getMethodOrObjectName();
		String qualifiedAPIName = pkgClsName.concat("->").concat(methdObjectName);

		EventFactory.getInstance().registerEvent("CreateFromPduEvent", new CreateFromPduEvent());
		
		Event event = EventFactory.getInstance().createEvent("CreateFromPduEvent");
	
		event.setCurrMethodName(instr.getCurrMethodName());
		event.setCurrPkgClsName(instr.getCurrPkgClassName());
		event.setName("CreateFromPduEvent");
		
		event.setCurrCompCallbackMethodName(ta.getCurrComponentCallback());
		event.setCurrComponentName(ta.getCurrComponentName());
		event.setCurrComponentPkgName(ta.getCurrComponentPkgName());
		
		ta.setCurrCSMEvent(event);

	
		SymbolTableEntry returnEntry = new SymbolTableEntry();
		EntryDetails returnEntryDetails = returnEntry.getEntryDetails();
		
		String apiInfo = "";
	    apiInfo = String.valueOf(" [SrcPkgClass] = ").concat(instr.getCurrPkgClassName()
 				 .concat (" , [SrcMethod] = ").concat(instr.getCurrMethodName()).concat(", [sourceAPIName] = ").concat(qualifiedAPIName) );
 		 logger.error("This is a source API" + apiInfo + ", [api] = " + instr.getText());
 		 
 		returnEntryDetails.setTainted(true);
 		returnEntryDetails.setConstant(false);
 		returnEntryDetails.setField(false);
 		returnEntryDetails.setRecord(false);
 		
 	   SourceInfo si = new SourceInfo();
 	   si.setSrcAPI(qualifiedAPIName);
 	   si.setSrcInstr(ir.getInstr().getText());
 	   
	   	ArrayList<SourceInfo> siList = returnEntryDetails.getSourceInfoList();
	   	
   		if(siList == null)
   			siList = new ArrayList<SourceInfo>();
	   		
		if(!siList.contains(si))
			siList.add(si);

		returnEntryDetails.setSourceInfoList(siList);
	   	
 	   returnEntryDetails.setType(ir.getReturnType());
 	   returnEntry.setInstrInfo(ir.getInstr().getText());
 	   returnEntryDetails.setValue(qualifiedAPIName);
	 	   
 	   returnEntry.setEntryDetails(returnEntryDetails);
	 
// 	   localSymSpace.put(returnEntry.getName(), returnEntry) // will be set by move- instruction.

 	   localSymSpace.logInfoSymbolSpace();
	   
	   return returnEntry;
	}
}
