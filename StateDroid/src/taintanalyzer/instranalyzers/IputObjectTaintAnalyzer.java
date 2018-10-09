package taintanalyzer.instranalyzers;


import java.util.Hashtable;

import models.cfg.InstructionResponse;
import models.cfg.Register;
import models.symboltable.Context;
import models.symboltable.EntryDetails;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;

import org.apache.log4j.Logger;

import configuration.Config;

import taintanalyzer.TaintAnalyzer;

public class IputObjectTaintAnalyzer  extends BaseTaintAnalyzer{


	private InstructionResponse ir;
	private SymbolSpace localSymSpace;
	boolean tainted=false;
	String[] used ;
	String changed;
	SymbolTableEntry destLocalEntry;
	SymbolTableEntry srcLocalEntry;
	SymbolTableEntry destGlobalEntry;
	SymbolTableEntry srcGlobalEntry;
	Register srcReg;
	Register destReg;


	public IputObjectTaintAnalyzer(TaintAnalyzer ta )
	{
		this.ir = ta.getIr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();
		logger = Logger.getLogger(IputObjectTaintAnalyzer.class);			
	}
	//	0x8 iput-object v0, v1, Lcom/test/maliciousactivity/MainActivity;->user Lcom/test/maliciousactivity/User;	
	/*
	 * 		0x7bc const/4 v0, 0
		0x7be iput-object v0, v12, Lcom/google/ads/internal/c;->o Lcom/google/ads/AdSize;

				0x34 const/4 v1, 0
		0x36 iput-object v1, v12, Lcom/google/ads/internal/d;->c Lcom/google/ads/internal/c;

				0x6 const/4 v0, 0
		0x8 iput-object v0, v1, Lcom/google/ads/mediation/admob/AdMobAdapterServerParameters;->allowHouseAds Ljava/lang/String;


		0xb4 iput-object v3, v7, Lcom/google/analytics/tracking/android/EasyTracker;->mSampleRate Ljava/lang/Double;

				0x4 const/4 v0, 0
		0x6 iput-object v0, v1, Landroid/support/v4/app/BackStackRecord;->mBreadCrumbTitleText Ljava/lang/CharSequence;


		All java/lang/<classes> which are final classes are immutable. So even if you get their reference, you can't modify their internal data
		So they use/need deep copy, which make them independent from each other. 


		http://www.tutorialspoint.com/java/lang/java_lang_string.htm


	 * 
	 * (non-Javadoc)
	 * @see taintanalyzer.BaseTaintAnalyzer#analyzeInstruction()
	 */
	// we want to put value of v0=source into v1;-> user object

	// 0x320 iput-object v2, v10, Lcom/google/ads/AdActivity;->e Lg;
	public Object analyzeInstruction()
	{
		String instrText = ir.getInstr().getText();
		logger.debug(instrText);
		srcReg = ir.getInvolvedRegisters().get(0);
		destReg = ir.getInvolvedRegisters().get(1);

		srcLocalEntry = localSymSpace.find(srcReg.getName());
		destLocalEntry = localSymSpace.find(destReg.getName());

		logger.debug("\n IputTaintAnalyzer--Before performing analysis");
		localSymSpace.logInfoSymbolSpace();

		boolean isDeepCopyRequested = false; //By default, we will go with shallow copy.

		String objectName = ir.getMethodOrObjectName();

		/* A very special case.
		 * 		0x2d8 move-object/from16 v1, v25
				0x2dc iput-object v0, v1, Lt4t/power/management/activity/SmsReceiver;->content Ljava/lang/String;

				v25 contains 'this' variable. Everytime, system wants to use it, it copies its reference in some variable (v1 here)
				and use it to set fields. Effectively, above lines set v25.content. So, if v1 is of type currentPkgCls, this means that
				it contains 'this' variable. We proccess it accordingly then

				Edit: This is now handled by shallow-copy of v25. :)
		 */


		/*	    		   A very special case#2
		 * 
		 * 		0x4 invoke-direct v3, Lcom/qbiki/seattleclouds/SCActivity;-><init>()V
		0xa iput-boolean v2, v3, Lcom/qbiki/modules/voicerecord/VoiceRecordPickerActivity;->sendonly Z


		First line actually means that VoiceRecordPickerActiviy extends SCActivity but invoke-direct returns object of type SCActivity, while it
		should be of type VoiceRecordActivity. Dalvik byte code assumes that v3 is of type VoiceRecordPickerActivity and uses it as it is in the next line.

//TODO		So temporary fix is that make v3 of type "Lcom/qbiki/modules/voicerecord/VoiceRecordPickerActivity;" even if it is not so earlier.
		 * 			This will affect other iputs also, but that should not be a problem.
		 * 
		 * 
		 * 
		 */	



		Hashtable immutableObjects = Config.getInstance().getImmutableObjects(); 

		String returnType = ir.getReturnType();
		SymbolTableEntry field = null ; //= new SymbolTableEntry();
		if(srcLocalEntry != null)  //v0 value to be stored 
		{

			EntryDetails srcEntryDetails = srcLocalEntry.getEntryDetails();
			String srcEntryValue = srcEntryDetails.getValue();
			String srcType = srcEntryDetails.getType();


			// Case#1 when null value is assigned to an object. 
			// this.user = myUser;
			// this.user=null ... in this case, Only this.user needs to be null.

			/*
			 * In most of the case, dalivk defines a string const or int constant to set variables. That's when we need 
			 * deep copy or fresh copy of the symbol table.
			 * 
			 * const/4 v0 0
			 * const-string v-0 ''
			 * 
			 * 0x3e iput-object v1, v3, Lcom/qbiki/modules/voicerecord/VoiceRecordPickerActivity;->recordRed Landroid/graphics/drawable/Drawable;
			 */
			if(srcEntryValue != null 
					&& srcEntryValue.trim().equalsIgnoreCase("0") 
					&& srcEntryDetails.getType().equalsIgnoreCase("I")
					) // Only case when we can be sure, it is being set to null.
			{
				if( destLocalEntry != null )
				{
					// Making a deep copy for copy-by-value items
					//Since we are putting a new object, it will just replace the existing item if there is any.

					// Just prepare a fresh copy and set it for this=destEntry, user=field
					field =  new SymbolTableEntry();

					field.setName(objectName);
					field.setInstrInfo(this.ir.getInstr().getText());
					field.setLineNumber(ir.getLineNumber());

					//srcLocalEntry may not have defined type, so we also set it explicitly.

					EntryDetails fieldEntryDetails = field.getEntryDetails();

					// fieldEntryDetails.setType(ir.getReturnType()); This is not true, sometimes it sets parent class but we need to
					// set child (original) class name.

					fieldEntryDetails.setValue(srcEntryValue);
					fieldEntryDetails.setConstant(true);
					//					   fieldEntryDetails.setType(srcEntryDetails.getType()); // This will set the type to "I", instead of its original type.
					fieldEntryDetails.setType(returnType);

					field.setEntryDetails(fieldEntryDetails);

					EntryDetails destEntryDetails = destLocalEntry.getEntryDetails();

					// Precautionary measures.
					destEntryDetails.setRecord(true);
					destEntryDetails.setType(ir.getCallerAPIName());

					Hashtable recordFieldList = (Hashtable) destEntryDetails.getRecordFieldList();

					if(recordFieldList == null) 
						recordFieldList = new Hashtable();

					recordFieldList.put(field.getName(), field);

					destEntryDetails.setRecordFieldList(recordFieldList);

					destLocalEntry.setEntryDetails(destEntryDetails);
				}
				else
				{
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

					field =  new SymbolTableEntry();

					field.setName(objectName);
					field.setInstrInfo(this.ir.getInstr().getText());
					field.setLineNumber(ir.getLineNumber());

					//srcLocalEntry may not have defined type, so we also set it explicitly.
					EntryDetails fieldEntryDetails = field.getEntryDetails();
					fieldEntryDetails.setType(ir.getReturnType());
					fieldEntryDetails.setValue("0");
					fieldEntryDetails.setConstant(true);
					fieldEntryDetails.setType(srcEntryDetails.getType());
					//That's it.

					Hashtable recordFieldList = (Hashtable) destEntryDetails.getRecordFieldList();

					if(recordFieldList == null) 
						recordFieldList = new Hashtable();

					recordFieldList.put(field.getName(), field);

					destEntryDetails.setRecordFieldList(recordFieldList);

					destLocalEntry.setEntryDetails(destEntryDetails);

					this.localSymSpace.addEntry(destLocalEntry);

				}
			}

			//CASE#2 
			// If we need deep copy.
			// http://stackoverflow.com/questions/5124012/examples-of-immutable-classes
			// Other options are there but not so important.
			else if( immutableObjects.containsKey(srcType)
					|| immutableObjects.containsKey(returnType))
			{
				if( destLocalEntry != null )
				{
					// Making a deep copy for copy-by-value items
					//Since we are putting a new object, it will just replace the existing item if there is any.

					field =  new SymbolTableEntry(srcLocalEntry); //deep copy

					field.setName(objectName);
					field.setInstrInfo(this.ir.getInstr().getText());
					field.setLineNumber(ir.getLineNumber());

					//srcLocalEntry may not have defined type, so we also set it explicitly.
					field.getEntryDetails().setType(ir.getReturnType());
					//That's it.

					EntryDetails destEntryDetails = destLocalEntry.getEntryDetails();

					//Precautionary measures.
					destEntryDetails.setRecord(true);
					destEntryDetails.setType(ir.getCallerAPIName());

					//		    		   destEntryDetails.getRecordFieldList().put(field.getName(), field);
					Hashtable recordFieldList = (Hashtable) destEntryDetails.getRecordFieldList();

					if(recordFieldList == null) 
						recordFieldList = new Hashtable();

					recordFieldList.put(field.getName(), field);

					destEntryDetails.setRecordFieldList(recordFieldList);

					destLocalEntry.setEntryDetails(destEntryDetails);
				}
				else
				{
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

					field =  new SymbolTableEntry(srcLocalEntry);

					field.setName(objectName);
					field.setInstrInfo(this.ir.getInstr().getText());
					field.setLineNumber(ir.getLineNumber());

					//srcLocalEntry may not have defined type, so we also set it explicitly.
					field.getEntryDetails().setType(ir.getReturnType());
					//That's it.

					//					   destEntryDetails.getRecordFieldList().put(field.getName(), field);

					Hashtable recordFieldList = (Hashtable) destEntryDetails.getRecordFieldList();

					if(recordFieldList == null) 
						recordFieldList = new Hashtable();

					recordFieldList.put(field.getName(), field);

					destEntryDetails.setRecordFieldList(recordFieldList);

					destLocalEntry.setEntryDetails(destEntryDetails);

					this.localSymSpace.addEntry(destLocalEntry);

				}
			} //
			//CASE#3
			// shallow copy
			else 
			{
				if( destLocalEntry != null )
				{

					//field = (SymbolTableEntry) srcLocalEntry.clone();

					field = (SymbolTableEntry) srcLocalEntry.copy();


					field.setName(objectName);
					field.setInstrInfo(this.ir.getInstr().getText());
					field.setLineNumber(ir.getLineNumber());

					//					   System.out.println(ir.getInstr().getText());

					//srcLocalEntry may not have defined type, so we also set it explicitly.
					String apiReturnType = ir.getReturnType();

					if(srcType != null && !srcType.isEmpty())
						field.getEntryDetails().setType(srcType);
					else
						field.getEntryDetails().setType(ir.getReturnType());


					EntryDetails destEntryDetails = destLocalEntry.getEntryDetails();

					//Precautionary measures.
					destEntryDetails.setRecord(true);

					String callerAPIName = ir.getCallerAPIName();
					destEntryDetails.setType(callerAPIName);

					//		    		   destEntryDetails.getRecordFieldList().put(field.getName(), field);

					Hashtable<String, SymbolTableEntry> recordFieldList = (Hashtable) destEntryDetails.getRecordFieldList();

					if(recordFieldList == null) 
						recordFieldList = new Hashtable<String, SymbolTableEntry>();

					recordFieldList.put(field.getName(), field);

					destEntryDetails.setRecordFieldList(recordFieldList);

					destLocalEntry.setEntryDetails(destEntryDetails);
				}
				else
				{
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

					field = (SymbolTableEntry) srcLocalEntry.clone(); // Shallow copy.

					field.setName(objectName);
					field.setInstrInfo(this.ir.getInstr().getText());
					field.setLineNumber(ir.getLineNumber());

					//srcLocalEntry may not have defined type, so we also set it explicitly.
					field.getEntryDetails().setType(ir.getReturnType());
					Hashtable recordFieldList = (Hashtable) destEntryDetails.getRecordFieldList();
					if(recordFieldList == null) 
						recordFieldList = new Hashtable();

					recordFieldList.put(field.getName(), field);
					destEntryDetails.setRecordFieldList(recordFieldList);
					destLocalEntry.setEntryDetails(destEntryDetails);
					this.localSymSpace.addEntry(destLocalEntry);

				}

			}
		}

		Context ctxt = Config.getInstance().getPrevMethodContext();
		ctxt.printContext();


		logger.debug("\n IputTaintAnalyzer");
		localSymSpace.logInfoSymbolSpace();

		return null;
	}

}
