package taintanalyzer.instranalyzers;

import java.util.ArrayList;

import models.cfg.InstructionResponse;
import models.cfg.Register;
import models.symboltable.EntryDetails;
import models.symboltable.SourceInfo;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;

import org.apache.log4j.Logger;

import taintanalyzer.TaintAnalyzer;
import configuration.Config;

public class InvokeVirtualDefaultTaintAnalyzer extends BaseTaintAnalyzer {

	private InstructionResponse ir;
	private SymbolSpace localSymSpace;
	boolean tainted = false;
	String[] used;
	String changed;
	ArrayList<Register> involvedRegisters;
	SymbolTableEntry returnEntry;

	/*
	 * This class handles all API calls which have no specified handler.
	 * It assumes that if input is tainted, output is tainted.
	 * 
	 * invoke-virtual v9, Ljava/util/ArrayList;->size()I
	 * 
	 * v9 should not be modified. Instead a new entry should be created and sent
	 * back.
	 * 
	 * 
	 * 
	 * Case#1
	 * 0x64 invoke-virtual v7, v3, v5, Landroid/graphics/Canvas;->translate(F
	 * F)V
	 * 0x6a if-eqz v1, +11
	 * 
	 * Case#2
	 * 0xd8 invoke-virtual v3, Ljava/util/ArrayList;->size()I
	 * 0xde move-result v3
	 */

	public InvokeVirtualDefaultTaintAnalyzer(TaintAnalyzer ta) {
		this.ir = ta.getIr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();
		involvedRegisters = new ArrayList<Register>();
		returnEntry = new SymbolTableEntry();
		logger = Logger.getLogger(InvokeVirtualDefaultTaintAnalyzer.class);
	}

	public Object analyzeInstruction() {
		boolean tainted = false;
		involvedRegisters = ir.getInvolvedRegisters();

		String returnType = ir.getReturnType();

		if (involvedRegisters.size() > 0) {
			Register callereReg = involvedRegisters.get(0);
			SymbolTableEntry callerEntry = this.localSymSpace.find(callereReg.getName());

			if (callerEntry != null) {
				EntryDetails callerEntryDetails = callerEntry.getEntryDetails();

				//Case#1
				if (returnType.trim().equalsIgnoreCase("V")) {
					// cant do anything even if input is tainted. Because this method returns nothing.
					// 0x4e invoke-direct v5, v3, Ljava/net/URL;-><init>(Ljava/lang/String;)V

					// in case of invoke-virtual, caller object is NOT of type 'this' but still both are analyzed the same way.

					// We mark the caller object as tainted, if any of the input variable is tainted.
					//TODO need to investigate further.

					ArrayList<SourceInfo> srcInfoList = callerEntryDetails.getSourceInfoList();

					for (int i = 1; i < involvedRegisters.size(); i++) {
						Register reg = involvedRegisters.get(i);

						SymbolTableEntry entry = this.localSymSpace.find(reg.getName());

						if (entry != null && entry.getEntryDetails().isTainted()) {
							tainted = true;
							ArrayList<SourceInfo> entryInfoList = entry.getEntryDetails().getSourceInfoList();

							if (entryInfoList != null && entryInfoList.size() > 0) {
								if (srcInfoList == null)
									srcInfoList = new ArrayList<SourceInfo>();
								for (SourceInfo si : entryInfoList) {
									if (!srcInfoList.contains(si)) {
										srcInfoList.add(si);
										//TODO hopefully, a reference would be fine here.
									}
								}
							}
						}
					}
					if (!callerEntryDetails.isTainted()) // if not tainted already, 
						callerEntryDetails.setTainted(tainted);

					callerEntryDetails.setSourceInfoList(srcInfoList);

					callerEntry.setEntryDetails(callerEntryDetails);

					logger.debug("\n InvokeDirectDefaultTaintAnalyzer");
					localSymSpace.logInfoSymbolSpace();

					return null;
				} else // if return type is not V.
				{

					// CASE#2
					//			 		0xd8 invoke-virtual v3, Ljava/util/ArrayList;->size()I
					//					0xde move-result v3

					// Special case. When we also want to caller object as tainted.
					// 0xbe invoke-interface v11, v0, Ljava/util/List;->add(Ljava/lang/Object;)Z

					SymbolTableEntry destEntry = new SymbolTableEntry();
					EntryDetails destEntryDetails = destEntry.getEntryDetails();

					ArrayList<SourceInfo> srcInfoList = destEntryDetails.getSourceInfoList();

					for (int i = 0; i < involvedRegisters.size(); i++) //We start from zero. we check all of them if they are tainted.
					{
						Register reg = involvedRegisters.get(i);

						SymbolTableEntry entry = this.localSymSpace.find(reg.getName());

						if (entry != null) {
							if (entry.getEntryDetails().isTainted()) {
								tainted = true;
								ArrayList<SourceInfo> entryInfoList = entry.getEntryDetails().getSourceInfoList();
								if (entryInfoList != null && entryInfoList.size() > 0) {
									if (srcInfoList == null)
										srcInfoList = new ArrayList<SourceInfo>();
									for (SourceInfo si : entryInfoList) {
										if (!srcInfoList.contains(si)) {
											srcInfoList.add(si);
											//TODO hopefully, a reference would be fine here.?
										}
									}
								}
							}
						}
					}
					destEntryDetails.setTainted(tainted);

					destEntry.setName(""); //any name you give here does not matter. It will be set by move instruction anyway.
					destEntry.setLineNumber(ir.getLineNumber());
					destEntry.setInstrInfo(ir.getInstr().getText());

					destEntryDetails.setType(returnType);
					destEntryDetails.setConstant(false);
					destEntryDetails.setField(false);
					destEntryDetails.setRecord(false);

					destEntryDetails.setSourceInfoList(srcInfoList);

					destEntryDetails.setValue("");

					if (!callerEntryDetails.isTainted()) // if not tainted already, 
						callerEntryDetails.setTainted(tainted);

					callerEntryDetails.setSourceInfoList(srcInfoList);

					callerEntry.setEntryDetails(callerEntryDetails);
					destEntry.setEntryDetails(destEntryDetails);

					return destEntry;

				}
			}

		}
		return null;

	}
}
