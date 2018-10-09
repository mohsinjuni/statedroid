package apihandlers.android.content.Intent;

import java.util.ArrayList;

import models.cfg.Instruction;
import models.cfg.InstructionResponse;
import models.cfg.Register;
import models.symboltable.SourceInfo;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;

import org.apache.log4j.Logger;

import taintanalyzer.TaintAnalyzer;
import taintanalyzer.instranalyzers.BaseTaintAnalyzer;
import configuration.Config;

public class GetStringExtraAnalyzer extends BaseTaintAnalyzer{
	private InstructionResponse ir;
	private Instruction instr;
	private TaintAnalyzer ta;
	private SymbolSpace localSymSpace;

	public GetStringExtraAnalyzer(TaintAnalyzer ta){
		ir = ta.getIr();
		instr = ta.getIr().getInstr();
		this.ta = ta;
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();	
		logger = Logger.getLogger(GetStringExtraAnalyzer.class);
	}

/*
   	  data = paramIntent.getStringExtra("key");
 * 
 * (non-Javadoc)
 * @see taintanalyzer.instranalyzers.BaseTaintAnalyzer#analyzeInstruction()
 */

	public Object analyzeInstruction(){
//		0x1e const-string v0, 'key'
//		0x22 invoke-virtual v3, v0, Landroid/content/Intent;->getStringExtra(Ljava/lang/String;)Ljava/lang/String;
//		0x28 move-result-object v0
		
		ArrayList<Register> involvedRegisters = ir.getInvolvedRegisters();
		Register intentReg = involvedRegisters.get(0);
		Register keyReg = involvedRegisters.get(1);
		
		SymbolTableEntry intentEntry = this.localSymSpace.find(intentReg.getName());
		SymbolTableEntry keyEntry = this.localSymSpace.find(keyReg.getName());
		SymbolTableEntry returnEntry = new SymbolTableEntry();
		returnEntry.getEntryDetails().setType(ir.getReturnType());

		if(intentEntry != null){
			if(keyEntry != null){
				String keyValue = keyEntry.getEntryDetails().getValue();
				if(!keyValue.isEmpty()){
					returnEntry.getEntryDetails().setValue("([intentSrc]= " + keyValue + ")");
					
					//  String incomingNumber =intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
					// "incoming_number"
					if(keyValue.equalsIgnoreCase("incoming_number")){
						keyEntry.getEntryDetails().setTainted(true);
						SourceInfo si = new SourceInfo();
						si.setSrcAPI("incoming_number");
						si.setSrcInstr("incoming_number");
						ArrayList<SourceInfo> srcInfoList = new ArrayList<SourceInfo>();
						srcInfoList.add(si);
						keyEntry.getEntryDetails().setSourceInfoList(srcInfoList);
					}
					//Since KeyEntry might be already tainted, this 'if' condition covers all cases.
					if(keyEntry.getEntryDetails().isTainted()){
						ArrayList<SourceInfo> intentSrcInfoList = intentEntry.getEntryDetails().getSourceInfoList();
						ArrayList<SourceInfo> srcInfoList = keyEntry.getEntryDetails().getSourceInfoList();
						
						if(srcInfoList != null && srcInfoList.size() > 0){
							if(intentSrcInfoList == null){
								intentSrcInfoList = new ArrayList<SourceInfo>();
							}
							for(SourceInfo si: srcInfoList){
								if(!intentSrcInfoList.contains(si)){
									intentSrcInfoList.add(si);
								}
							}
							intentEntry.getEntryDetails().setSourceInfoList(intentSrcInfoList);
							intentEntry.getEntryDetails().setTainted(true);
						}
					}	
				}
			}	
			ArrayList<SourceInfo> intentSrcInfoList = intentEntry.getEntryDetails().getSourceInfoList();
			ArrayList<SourceInfo> srcInfoList = new ArrayList<SourceInfo>();
							
			if(intentSrcInfoList != null && intentSrcInfoList.size() > 0){
				for(SourceInfo si: intentSrcInfoList){
					if(!srcInfoList.contains(si)){
						srcInfoList.add(si);
					}
				}
				returnEntry.getEntryDetails().setSourceInfoList(srcInfoList);
				returnEntry.getEntryDetails().setTainted(true);
				returnEntry.setInstrInfo(ir.getInstr().getText());
			}
		}
	   return returnEntry;
	}
}
