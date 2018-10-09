package apihandlers.android.content.Intent;

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
import patternMatcher.events.csm.GetExtrasBundleEvent;
import taintanalyzer.TaintAnalyzer;
import taintanalyzer.instranalyzers.BaseTaintAnalyzer;
import configuration.Config;
import enums.ComponentTypes;


public class GetExtrasAnalyzer extends BaseTaintAnalyzer{
	private InstructionResponse ir;
	private Instruction instr;
	private TaintAnalyzer ta;
	private SymbolSpace localSymSpace;

	public GetExtrasAnalyzer(TaintAnalyzer ta){
		ir = ta.getIr();
		instr = ta.getIr().getInstr();
		this.ta = ta;
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();	
		logger = Logger.getLogger(GetExtrasAnalyzer.class);
	}

/*
 * 		0x0 invoke-virtual v13, Landroid/content/Intent;->getExtras()Landroid/os/Bundle;
		0x6 move-result-object v0
 * 
 * (non-Javadoc)
 * @see taintanalyzer.instranalyzers.BaseTaintAnalyzer#analyzeInstruction()
 */
	
	public Object analyzeInstruction(){
		
		String pkgClsName = ir.getCallerAPIName();
		String methdObjectName = ir.getMethodOrObjectName();
		String qualifiedAPIName = pkgClsName.concat("->").concat(methdObjectName);
	
		SymbolTableEntry returnEntry = new SymbolTableEntry();
		EntryDetails returnEntryDetails = returnEntry.getEntryDetails();
 		 
 		returnEntryDetails.setConstant(false);
 		returnEntryDetails.setField(false);
 		returnEntryDetails.setRecord(false);
	 	   returnEntryDetails.setTainted(false);
	 	   
		AndroidManifest am = Config.getInstance().getAndroidManifest();
		String currPkgClsInfo = instr.getCurrPkgClassName();
		ComponentManifest compInfo = am.findComponentManifest(currPkgClsInfo);
		ArrayList<String> permList = am.getUsedPermStrList();
		
		if(compInfo != null){
			if(compInfo.getType().equalsIgnoreCase(ComponentTypes.broadcastReceiver.toString())
					&& (ta.getCurrComponentName().equalsIgnoreCase("onReceive"))
					){
		 	   returnEntryDetails.setTainted(true);

		 	   SourceInfo si = new SourceInfo();
		 	   si.setSrcAPI(qualifiedAPIName);
		 	   si.setSrcInstr(ir.getInstr().getText());
			 
		 	   ArrayList<SourceInfo> siList = returnEntryDetails.getSourceInfoList(); 
		 	   if(siList == null)
		 		   siList = new ArrayList<SourceInfo>();
		 	    siList.add(si); //It is a new symboltableEntry
			}
		}
 	    returnEntryDetails.setType(ir.getReturnType());
 	    returnEntry.setInstrInfo(ir.getInstr().getText());
 	    returnEntryDetails.setValue("");
	  	   
 	    returnEntry.setEntryDetails(returnEntryDetails);

 	    localSymSpace.logInfoSymbolSpace();
	   
	    return returnEntry;
	}
}
