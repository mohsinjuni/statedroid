package taintanalyzer.instranalyzers;

import java.util.Hashtable;

import models.cfg.InstructionResponse;
import models.cfg.Register;
import models.symboltable.Context;
import models.symboltable.EntryDetails;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;

import org.apache.log4j.Logger;

import taintanalyzer.TaintAnalyzer;
import configuration.Config;

public class IputTaintAnalyzer extends BaseTaintAnalyzer {

	private InstructionResponse ir;
	private SymbolSpace localSymSpace;
	boolean tainted = false;
	String[] used;
	String changed;
	SymbolTableEntry destLocalEntry;
	SymbolTableEntry srcLocalEntry;
	SymbolTableEntry destGlobalEntry;
	SymbolTableEntry srcGlobalEntry;
	Register srcReg;
	Register destReg;

	public IputTaintAnalyzer(TaintAnalyzer ta) {
		this.ir = ta.getIr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();
		logger = Logger.getLogger(IputTaintAnalyzer.class);
	}

	//	0x8 iput-object v0, v1, Lcom/test/maliciousactivity/MainActivity;->user Lcom/test/maliciousactivity/User;	
	/*
	 * 0x7bc const/4 v0, 0
	 * 0x7be iput-object v0, v12, Lcom/google/ads/internal/c;->o
	 * Lcom/google/ads/AdSize;
	 * 
	 * 0x34 const/4 v1, 0
	 * 0x36 iput-object v1, v12, Lcom/google/ads/internal/d;->c
	 * Lcom/google/ads/internal/c;
	 * 
	 * 0x6 const/4 v0, 0
	 * 0x8 iput-object v0, v1,
	 * Lcom/google/ads/mediation/admob/AdMobAdapterServerParameters
	 * ;->allowHouseAds Ljava/lang/String;
	 * 
	 * 
	 * 0x6 const/4 v0, 0
	 * 0x8 iput-object v0, v1,
	 * Lcom/google/ads/mediation/customevent/CustomEventServerParameters
	 * ;->parameter Ljava/lang/String;
	 * 
	 * 
	 * 0xc const/4 v0, -1
	 * 0xe iput v0, v1, Landroid/support/v4/app/BackStackRecord;->mIndex I
	 * 
	 * (non-Javadoc)
	 * 
	 * @see taintanalyzer.BaseTaintAnalyzer#analyzeInstruction()
	 */
	// we want to put value of v0=source into v1;->mIndex 
	public Object analyzeInstruction() {
		srcReg = ir.getInvolvedRegisters().get(0);
		destReg = ir.getInvolvedRegisters().get(1);

		srcLocalEntry = localSymSpace.find(srcReg.getName());
		destLocalEntry = localSymSpace.find(destReg.getName());

		//Since it is copy by value, and after copying, both work as independent, we use deep copy to make them indepndent.

		String objectName = ir.getMethodOrObjectName();

		/*
		 * A special case#1
		 * 
		 * 0x4 invoke-direct v3, Lcom/qbiki/seattleclouds/SCActivity;-><init>()V
		 * 0xa iput-boolean v2, v3,
		 * Lcom/qbiki/modules/voicerecord/VoiceRecordPickerActivity;->sendonly Z
		 * 
		 * 
		 * First line actually means that VoiceRecordPickerActiviy extends
		 * SCActivity but invoke-direct returns object of type SCActivity, while
		 * it
		 * should be of type VoiceRecordActivity. Dalvik byte code assumes that
		 * v3 is of type VoiceRecordPickerActivity and uses it as it is in the
		 * next line.
		 * 
		 * //TODO So temporary fix is that make v3 of type
		 * "Lcom/qbiki/modules/voicerecord/VoiceRecordPickerActivity;" even if
		 * it is not so earlier.
		 * This will affect other iputs also, but that should not be a problem.
		 */

		SymbolTableEntry field = null; //= new SymbolTableEntry();
		if (srcLocalEntry != null) //v0 value to be stored 
		{
			if (destLocalEntry != null) {
				// Making a deep copy for copy-by-value items
				//Since we are putting a new object, it will just replace the existing item if there is any.

				field = new SymbolTableEntry(srcLocalEntry);

				field.setName(objectName);
				field.setInstrInfo(this.ir.getInstr().getText());
				field.setLineNumber(ir.getLineNumber());

				//srcLocalEntry may not have defined type, so we also set it explicitly.
				field.getEntryDetails().setType(ir.getReturnType());
				//That's it.

				EntryDetails destEntryDetails = destLocalEntry.getEntryDetails();

				destEntryDetails.setRecord(true);
				destEntryDetails.setType(ir.getCallerAPIName());

				//	    		   destEntryDetails.getRecordFieldList().put(field.getName(), field);

				Hashtable recordFieldList = (Hashtable) destEntryDetails.getRecordFieldList();

				if (recordFieldList == null)
					recordFieldList = new Hashtable();

				recordFieldList.put(field.getName(), field);

				destEntryDetails.setRecordFieldList(recordFieldList);

				destLocalEntry.setEntryDetails(destEntryDetails);

			} else {
				//Ideally this case should never arise, but just in case.

				destLocalEntry = new SymbolTableEntry();
				EntryDetails destEntryDetails = destLocalEntry.getEntryDetails();

				destLocalEntry.setName(destReg.getName());
				destLocalEntry.setLineNumber(ir.getLineNumber());

				destEntryDetails.setConstant(false);
				destEntryDetails.setField(false);
				destEntryDetails.setRecord(true);
				destEntryDetails.setType(ir.getCallerAPIName());
				destEntryDetails.setValue("");

				field = new SymbolTableEntry(srcLocalEntry);

				field.setName(objectName);
				field.setInstrInfo(this.ir.getInstr().getText());
				field.setLineNumber(ir.getLineNumber());

				//srcLocalEntry may not have defined type, so we also set it explicitly.
				field.getEntryDetails().setType(ir.getReturnType());
				//That's it.

				//				   destEntryDetails.getRecordFieldList().put(field.getName(), field);

				Hashtable recordFieldList = (Hashtable) destEntryDetails.getRecordFieldList();

				if (recordFieldList == null)
					recordFieldList = new Hashtable();

				recordFieldList.put(field.getName(), field);

				destEntryDetails.setRecordFieldList(recordFieldList);

				destLocalEntry.setEntryDetails(destEntryDetails);

				this.localSymSpace.addEntry(destLocalEntry);

			}
		}

		Context ctxt = Config.getInstance().getPrevMethodContext();
		ctxt.printContext();

		logger.debug("\n IputTaintAnalyzer");
		localSymSpace.logInfoSymbolSpace();

		return null;
	}

}
