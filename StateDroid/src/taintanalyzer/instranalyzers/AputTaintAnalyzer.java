package taintanalyzer.instranalyzers;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Random;

import models.cfg.InstructionResponse;
import models.cfg.Register;
import models.symboltable.EntryDetails;
import models.symboltable.SourceInfo;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;

import org.apache.log4j.Logger;

import taintanalyzer.TaintAnalyzer;
import configuration.Config;

public class AputTaintAnalyzer extends BaseTaintAnalyzer {

	private InstructionResponse ir;
	private SymbolSpace localSymSpace;
	boolean tainted = false;
	String[] used;
	String changed;
	SymbolTableEntry destLocalEntry;
	SymbolTableEntry srcLocalEntry;
	SymbolTableEntry indexLocalEntry;
	SymbolTableEntry destGlobalEntry;
	SymbolTableEntry srcGlobalEntry;
	Register srcReg;
	Register destReg;
	Register indexReg;

	public AputTaintAnalyzer(TaintAnalyzer ta) {
		this.ir = ta.getIr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();
		logger = Logger.getLogger(AputTaintAnalyzer.class);
	}

	//	// 		0x74 aput-object v0, v5, v1   ==> v0=source, v5=destination, v1= index

	//I am going to mark the whole array as tainted if any of the index or the src entry is tainted. I will also mark it as record= false onwards.
	// No point of storing in recordFieldList.
	public Object analyzeInstruction() {
		srcReg = ir.getInvolvedRegisters().get(0);
		destReg = ir.getInvolvedRegisters().get(1);
		indexReg = ir.getInvolvedRegisters().get(2);

		srcLocalEntry = localSymSpace.find(srcReg.getName());
		destLocalEntry = localSymSpace.find(destReg.getName());
		indexLocalEntry = localSymSpace.find(indexReg.getName());

		if (srcLocalEntry != null) {
			if (destLocalEntry != null) {
				EntryDetails destEntryDetails = destLocalEntry.getEntryDetails();

				destEntryDetails.setValue(" ");

				destEntryDetails.setType(ir.getReturnType()); // Lcom/test/maliciousactivity/User;	
				destLocalEntry.setLineNumber(ir.getLineNumber());

				ArrayList<SourceInfo> srcInfoList = destEntryDetails.getSourceInfoList();

				if (indexLocalEntry != null) {
					if (indexLocalEntry.getEntryDetails().isTainted()) {
						destEntryDetails.setTainted(true);

						ArrayList<SourceInfo> existingSilist = indexLocalEntry.getEntryDetails().getSourceInfoList();

						if (existingSilist != null && existingSilist.size() > 0) {
							if (srcInfoList == null)
								srcInfoList = new ArrayList<SourceInfo>();
							for (SourceInfo si : existingSilist) {
								if (!srcInfoList.contains(si)) {
									srcInfoList.add(si);
								}
							}
						}

					}
				}

				if (srcLocalEntry.getEntryDetails().isTainted()) {
					destEntryDetails.setTainted(true);
					ArrayList<SourceInfo> existingSilist = srcLocalEntry.getEntryDetails().getSourceInfoList();

					if (existingSilist != null && existingSilist.size() > 0) {
						if (srcInfoList == null)
							srcInfoList = new ArrayList<SourceInfo>();
						for (SourceInfo si : existingSilist) {
							if (!srcInfoList.contains(si)) {
								srcInfoList.add(si);
							}
						}
					}
				}

				destEntryDetails.setSourceInfoList(srcInfoList);
				destEntryDetails.setField(false);

				destEntryDetails.setRecord(false);

				destLocalEntry.setEntryDetails(destEntryDetails);

			} else {

				destLocalEntry = new SymbolTableEntry();
				EntryDetails destEntryDetails = destLocalEntry.getEntryDetails();

				destEntryDetails.setValue(" ");

				destEntryDetails.setType(ir.getReturnType()); // Lcom/test/maliciousactivity/User;	
				destLocalEntry.setLineNumber(ir.getLineNumber());

				ArrayList<SourceInfo> srcInfoList = destEntryDetails.getSourceInfoList();

				if (indexLocalEntry != null) {
					if (indexLocalEntry.getEntryDetails().isTainted()) {
						destEntryDetails.setTainted(true);

						ArrayList<SourceInfo> existingSilist = indexLocalEntry.getEntryDetails().getSourceInfoList();

						if (existingSilist != null && existingSilist.size() > 0) {
							if (srcInfoList == null)
								srcInfoList = new ArrayList<SourceInfo>();
							for (SourceInfo si : existingSilist) {
								if (!srcInfoList.contains(si)) {
									srcInfoList.add(si);
								}
							}
						}

					}
				}

				if (srcLocalEntry.getEntryDetails().isTainted()) {
					destEntryDetails.setTainted(true);
					ArrayList<SourceInfo> existingSilist = srcLocalEntry.getEntryDetails().getSourceInfoList();

					if (existingSilist != null && existingSilist.size() > 0) {
						if (srcInfoList == null)
							srcInfoList = new ArrayList<SourceInfo>();
						for (SourceInfo si : existingSilist) {
							if (!srcInfoList.contains(si)) {
								srcInfoList.add(si);
							}
						}
					}

				}

				destEntryDetails.setSourceInfoList(srcInfoList);
				destEntryDetails.setField(false);

				destEntryDetails.setRecord(false);

				destLocalEntry.setEntryDetails(destEntryDetails);

				localSymSpace.addEntry(destLocalEntry);

			}
		}

		logger.debug("\n AputTaintAnalyzer");
		localSymSpace.logInfoSymbolSpace();

		return null;
	}

}
