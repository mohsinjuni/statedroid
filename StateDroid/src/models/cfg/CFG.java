package models.cfg;

import iterator.BBRPOIterator;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;

import models.symboltable.EntryDetails;
import models.symboltable.SourceInfo;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;

import org.apache.log4j.Logger;

import analyzer.Analyzer;

import configuration.Config;

public class CFG extends CFGComponent implements Iterable<CFGComponent> {

	//	ArrayList<CFGComponent> compCollection;
	private ArrayList<Parameter> paramList;
	private String type = "";
	private MethodSignature signature;
	private boolean isFirstTimeTraversal = true;
	private boolean isRecursive = false;
	private SymbolTableEntry exceptionObject = null;

	public CFG() {
		this.compCollection = new ArrayList<CFGComponent>();
		this.setParamList(new ArrayList<Parameter>());
		logger = Logger.getLogger(CFG.class);
	}

	public CFG(CFG cfg) {
		this.setKey(cfg.getKey());
		this.setText(cfg.getText());
		this.setCurrPkgName(cfg.getCurrPkgName());
		this.setCurrPkgClassName(cfg.getCurrPkgClassName());
		this.setCurrMethodName(cfg.getCurrMethodName());
		this.setCurrClassName(cfg.getCurrClassName());
		this.setCompCollection(cfg.getCompCollection());

		this.setSignature(cfg.getSignature());
		this.setParamList(cfg.getParamList());
		this.setType(cfg.getType());
	}

	public CFG(String keyItem) {
		this.key = keyItem;
		this.compCollection = new ArrayList<CFGComponent>();
		this.setParamList(new ArrayList<Parameter>());
	}

	public void setItem(int index, CFGComponent comp) {
		compCollection.set(index, comp);
	}

	public void nullifyBBOutSets() {

		for (CFGComponent comp : compCollection) {
			BasicBlock bb = (BasicBlock) comp;
			bb.setOUT(null);
			bb.setShadowCopyOfOut(null);
			bb.setNulltoShadowCopyOfGlobalSymTable();
		}
	}

	public String pushInputParamsForCallbacks(SymbolSpace symSpace, ClassObj currCls) {
		Hashtable inputParameters = new Hashtable();
		String currPkgClsName = signature.getPkgClsName();
		String cfgKey = key;

		//        localSymSpace.push(inputParameters);   
		String entryName = "";

		logger.debug("cfg.java" + currPkgClsName);

		// This case is required because in some cases, there is no call to java/lang/Object;->init method call which typically
		// creates a file level entry. So in ObjectInitHandler, we first check the existing entry.

		// In <init> cases, it creates a new entry of type 'CurrPkgClsName'. For all other callbacks, they get only shallow copy of the object 
		//with a new name. So the original object automatically remains updated.

		if (cfgKey.equalsIgnoreCase("<init>") || cfgKey.equalsIgnoreCase("init") || cfgKey.equalsIgnoreCase("<clinit>")) {
			// In all other cases, we will get a shallow copy of CurrPkgClsName type entry.
			SymbolTableEntry fileLevelEntryFromSymTable = symSpace.find(currPkgClsName);

			if (fileLevelEntryFromSymTable == null) {
				// Following chunk removes all entries on the symbolTable and adds a new SymbolTable and file-level entry
				symSpace.removeEntriesUptoInclusiveLevel(0); //TODO I think, we need to remove rest of the entries like what instruction does.
				symSpace.push();

				SymbolTableEntry fileLevelEntry = new SymbolTableEntry(); //First it creates a file-level entry object.
				fileLevelEntry.getEntryDetails().setType(currPkgClsName);
				fileLevelEntry.setName(currPkgClsName);

				//Additionaly, we also add bundle object, though it will only be used by Activity's methods.
				// TODO: We also need to add Intent, Context objects here in the future.
				SymbolTableEntry bundleEntry = new SymbolTableEntry();
				bundleEntry.setName("bundle");
				bundleEntry.getEntryDetails().setType("Landroid/os/Bundle;");
				bundleEntry.getEntryDetails().setRecord(true);

				Hashtable recordFieldList = (Hashtable) fileLevelEntry.getEntryDetails().getRecordFieldList();

				if (recordFieldList == null)
					recordFieldList = new Hashtable();

				recordFieldList.put("bundle", bundleEntry);

				fileLevelEntry.getEntryDetails().setRecordFieldList(recordFieldList);

				symSpace.addEntry(fileLevelEntry);
			}

			// Adding entry for current method.
			fileLevelEntryFromSymTable = symSpace.find(currPkgClsName);

			//We make a shallow copy of file-level entry and rename it for current <init> method. 
			SymbolTableEntry copyForCurrentMethod = (SymbolTableEntry) fileLevelEntryFromSymTable.clone();

			entryName = String.valueOf((signature.getMaxRegNo() - signature.getParams().size()));
			entryName = String.valueOf('v').concat(entryName);
			copyForCurrentMethod.setName(entryName);
			copyForCurrentMethod.setLineNumber("00");

			inputParameters.put(entryName, copyForCurrentMethod);

		} else //if(entry != null)
		{
			// In all other cases, we will get a shallow copy of CurrPkgClsName type entry.
			SymbolTableEntry fileLevelEntryFromSymTable = symSpace.find(currPkgClsName);

			//We make a shallow copy of file-level entry and rename it for current method. 
			if (fileLevelEntryFromSymTable == null) {
				// Following chunk removes all entries on the symbolTable and adds a new SymbolTable and file-level entry
				symSpace.removeEntriesUptoInclusiveLevel(0); //TODO I think, we need to remove rest of the entries like what instruction does.
				symSpace.push();

				SymbolTableEntry fileLevelEntry = new SymbolTableEntry(); //First it creates a file-level entry object.
				fileLevelEntry.getEntryDetails().setType(currPkgClsName);
				fileLevelEntry.setName(currPkgClsName);

				//Additionaly, we also add bundle object, though it will only be used by Activity's methods.
				// TODO: We also need to add Intent, Context objects here in the future.
				SymbolTableEntry bundleEntry = new SymbolTableEntry();
				bundleEntry.setName("bundle");
				bundleEntry.getEntryDetails().setType("Landroid/os/Bundle;");
				bundleEntry.getEntryDetails().setRecord(true);

				Hashtable recordFieldList = (Hashtable) fileLevelEntry.getEntryDetails().getRecordFieldList();

				if (recordFieldList == null)
					recordFieldList = new Hashtable();

				recordFieldList.put("bundle", bundleEntry);

				fileLevelEntry.getEntryDetails().setRecordFieldList(recordFieldList);

				symSpace.addEntry(fileLevelEntry);
			}

			fileLevelEntryFromSymTable = symSpace.find(currPkgClsName);

			SymbolTableEntry copyForCurrentMethod = (SymbolTableEntry) fileLevelEntryFromSymTable.clone();

			entryName = String.valueOf((signature.getMaxRegNo() - signature.getParams().size()));
			entryName = String.valueOf('v').concat(entryName);
			copyForCurrentMethod.setName(entryName);
			copyForCurrentMethod.setLineNumber("00");

			inputParameters.put(entryName, copyForCurrentMethod);

			ArrayList<Parameter> params = signature.getParams();

			// I think, we only have Bundle instance in almost all of the activity callbacks but onReceive() has two input parameters

			int maxRegNo = signature.getMaxRegNo();
			for (int i = params.size() - 1; i >= 0; i--) {

				Parameter param = params.get(i);

				String paramType = param.getType();

				if (paramType.equalsIgnoreCase("Landroid/os/Bundle;")) {
					Hashtable recordFieldList = copyForCurrentMethod.getEntryDetails().getRecordFieldList();

					if (recordFieldList != null && recordFieldList.size() > 0) {
						SymbolTableEntry origBundleEntry = (SymbolTableEntry) recordFieldList.get("bundle");

						//Shallow copy will update itself as well as original object.
						SymbolTableEntry shallowCopyOfBundleEntry = (SymbolTableEntry) origBundleEntry.clone();
						shallowCopyOfBundleEntry.setName(param.getName());

						inputParameters.put(param.getName(), shallowCopyOfBundleEntry);
					}

				}
				//  onReceive ( Landroid/content/Intent;
				else {
					//Just add other parameters as it is.
					SymbolTableEntry newEntry = new SymbolTableEntry();

					entryName = String.valueOf(maxRegNo--);
					entryName = String.valueOf('v').concat(entryName);
					newEntry.setName(entryName);

					newEntry.getEntryDetails().setType(paramType);

					inputParameters.put(newEntry.getName(), newEntry);
				}
			}
		}

		symSpace.push(inputParameters);

		return entryName;

	}

	public void setGlobalSymbolSpace(CFGComponent bb) {
		ArrayList<String> predSet = ((BasicBlock) bb).getPredecessors();

		SymbolSpace globalSymsSpace = Config.getInstance().getGlobalSymbolSpace();
		Hashtable ht = (Hashtable) globalSymsSpace.getEntries().get(0);

		for (int i = predSet.size() - 1; i >= 0; i--) {
			String predecessorKey = predSet.get(i);
			BasicBlock bbObj = getBasicBlockByKey(predecessorKey);
			if (bbObj != null) {
				Hashtable shadowCopy = bbObj.getShadowCopyOfGlobalSymTable();

				if (shadowCopy != null) {
					Enumeration<String> enumKey = shadowCopy.keys();
					while (enumKey.hasMoreElements()) {
						String key = enumKey.nextElement().toString();
						SymbolTableEntry newEnt = (SymbolTableEntry) shadowCopy.get(key);

						if (ht.containsKey(key)) {
							SymbolTableEntry existingEnt = (SymbolTableEntry) ht.get(key);

							if (!existingEnt.getEntryDetails().isTainted() && newEnt.getEntryDetails().isTainted()) {
								ht.put(newEnt.getName(), newEnt);
							}
						}
					}
				}
			}
		}

		globalSymsSpace.getEntries().set(0, ht);

		Config.getInstance().setGlobalSymbolSpace(globalSymsSpace);

	}

	public Hashtable setSymbolTableEntriesFromPredecessors(CFGComponent bb) {
		Hashtable predUniontable = new Hashtable(); // get union of OUT sets of predecessor basic blocks here

		ArrayList<String> predSet = ((BasicBlock) bb).getPredecessors();

		//We set the first predecessor as it is.
		int predCount = predSet.size();
		if (predCount > 0) {

			String lastPredecessorKey = predSet.get(predCount - 1); //getting the last updated predecessor with higher address value.
			BasicBlock lastbbObj = getBasicBlockByKey(lastPredecessorKey);
			if (lastbbObj != null) {
				Hashtable out = lastbbObj.getOUT();
				if (out != null)
					predUniontable = (Hashtable) out.clone();

				for (int i = predCount - 2; i >= 0; i--) {
					String predecessorKey = predSet.get(i);
					BasicBlock bbObj = getBasicBlockByKey(predecessorKey);
					Hashtable outSet = bbObj.getOUT();
					Hashtable shadowCopyOfOut = bbObj.getShadowCopyOfOut();

					if (outSet != null) {
						Enumeration<String> enumKey = outSet.keys();
						while (enumKey.hasMoreElements()) {
							String key = enumKey.nextElement().toString();
							SymbolTableEntry newEnt = (SymbolTableEntry) outSet.get(key);

							if (predUniontable.containsKey(key)) {
								//We got an item, that might have been updated in two basic blocks. We have already added items from
								//last updated predecessor, before the for loop. For this predecessor, we will get its shadow copy and check
								// if item was originally tainted or not. This will cover the case where object modified in both predecessors gets the
								// same value because of shallow copy. SO we needed a shadow copy, in that case.

								SymbolTableEntry existingEnt = (SymbolTableEntry) predUniontable.get(key);

								if (shadowCopyOfOut.containsKey(key)) {
									SymbolTableEntry shadowCopyEnt = (SymbolTableEntry) shadowCopyOfOut.get(key);
									EntryDetails shadowCopyDetails = shadowCopyEnt.getEntryDetails();
									EntryDetails existingEntDetails = existingEnt.getEntryDetails();
									EntryDetails newEntDetails = newEnt.getEntryDetails();

									if (existingEnt.getEntryDetails().isRecord()) {
										//This is going to be tricky here.

										//if newEnt is not record but existing is record, just keep the existing one, and dont do anything
										if (!shadowCopyDetails.isRecord()) //Since newEntDetails would have been modified anyway because of shallow copy.
										{
											//Dont do anything
										} else {
											//when both are records
											// we will check upto two levels to see if anyone is tainted in shadow copy, and not tainted in existingEntry.
											Hashtable shadowRecordFieldList = shadowCopyDetails.getRecordFieldList();
											Hashtable existingRecordFieldList = existingEntDetails.getRecordFieldList();
											if (shadowRecordFieldList != null && existingRecordFieldList != null) {

												Enumeration<String> shadowKeys = shadowRecordFieldList.keys();
												while (shadowKeys.hasMoreElements()) {
													String fieldKey = shadowKeys.nextElement();
													SymbolTableEntry shadowEntField = (SymbolTableEntry) shadowRecordFieldList
															.get(fieldKey);
													SymbolTableEntry existingEntField = (SymbolTableEntry) existingRecordFieldList
															.get(fieldKey);

													EntryDetails shadowEntFieldDetails = shadowEntField.getEntryDetails();
													EntryDetails existingEntFieldDetails = shadowEntField.getEntryDetails();

													if (shadowEntField.getEntryDetails().isRecord()) {
														//Holy crap. OK. we are going to this level only.
														Hashtable shadowFieldRecordFieldList = shadowEntFieldDetails.getRecordFieldList();
														Hashtable existingFieldRecordFieldList = existingEntFieldDetails
																.getRecordFieldList();

														if (shadowFieldRecordFieldList != null && existingFieldRecordFieldList != null) {
															Enumeration<String> shadowFieldKeys = shadowFieldRecordFieldList.keys();
															while (shadowFieldKeys.hasMoreElements()) {
																String fieldFieldKey = shadowFieldKeys.nextElement();
																SymbolTableEntry shadowEntFieldField = (SymbolTableEntry) shadowFieldRecordFieldList
																		.get(fieldFieldKey);
																SymbolTableEntry existingEntFieldField = (SymbolTableEntry) existingFieldRecordFieldList
																		.get(fieldFieldKey);

																EntryDetails shadowEntFieldFieldDetails = shadowEntFieldField
																		.getEntryDetails();
																EntryDetails existingEntFieldFieldDetails = existingEntFieldField
																		.getEntryDetails();

																if (shadowEntFieldField.getEntryDetails().isTainted()) //If new entry is tainted, just update the table.
																{
																	if (existingEntFieldField.getEntryDetails().isTainted()) {
																		//if both tainted, check which one has more elements
																		if (shadowEntFieldFieldDetails.getSourceInfoList().size() != existingEntFieldFieldDetails
																				.getSourceInfoList().size()) {
																			//Since shadowCopy is a deep copy, we dont want to store it in the Hashtable. Lets update the existing one.

																			for (SourceInfo si : shadowEntFieldFieldDetails
																					.getSourceInfoList()) {
																				if (!existingEntFieldFieldDetails.getSourceInfoList()
																						.contains(si))
																					existingEntFieldFieldDetails.getSourceInfoList()
																							.add(si);
																			}
																			existingEntFieldField
																					.setEntryDetails(existingEntFieldFieldDetails);
																		}
																		// If size is equal, just leave them alone. Let them be happy.
																	} else {
																		//If existing is NOT tainted but shadow is tainted. Lets just copy data from shadow.
																		ArrayList<SourceInfo> siList = existingEntFieldFieldDetails
																				.getSourceInfoList();

																		if (siList == null)
																			siList = new ArrayList<SourceInfo>();
																		existingEntFieldFieldDetails.setTainted(true);

																		if (shadowEntFieldFieldDetails.getSourceInfoList() != null) {
																			for (SourceInfo si : shadowEntFieldFieldDetails
																					.getSourceInfoList()) {
																				if (!siList.contains(si)) {
																					siList.add(si);
																				}
																			}
																		}
																		existingEntFieldFieldDetails.setSourceInfoList(siList);
																		existingEntFieldField.setEntryDetails(existingEntFieldFieldDetails);

																	}
																} else {
																	//if shadow is not tainted, just leave it. If existing is tainted/untainted, it will stay the same.
																}

															}
														}
													} else {
														if (shadowEntField.getEntryDetails().isTainted()) //If new entry is tainted, just update the table.
														{
															if (existingEntField != null) {
																if (existingEntField.getEntryDetails().isTainted()) {

																	if (shadowEntFieldDetails != null && existingEntFieldDetails != null
																			&& shadowEntFieldDetails.getSourceInfoList() != null
																			&& existingEntFieldDetails.getSourceInfoList() != null) {
																		//if both tainted, check which one has more elements
																		if (shadowEntFieldDetails.getSourceInfoList().size() != existingEntFieldDetails
																				.getSourceInfoList().size()) {
																			//Since shadowCopy is a deep copy, we dont want to store it in the Hashtable. Lets update the existing one.

																			for (SourceInfo si : shadowEntFieldDetails.getSourceInfoList()) {
																				if (!existingEntFieldDetails.getSourceInfoList().contains(
																						si))
																					existingEntFieldDetails.getSourceInfoList().add(si);
																			}
																			existingEntField.setEntryDetails(existingEntFieldDetails);
																		}
																	}
																	// If size is equal, just leave them alone. Let them be happy.
																} else {
																	//If existing is NOT tainted but shadow is tainted. Lets just copy data from shadow.
																	ArrayList<SourceInfo> siList = existingEntFieldDetails
																			.getSourceInfoList();

																	if (siList == null)
																		siList = new ArrayList<SourceInfo>();
																	existingEntFieldDetails.setTainted(true);

																	if (shadowEntFieldDetails.getSourceInfoList() != null) {
																		for (SourceInfo si : shadowEntFieldDetails.getSourceInfoList()) {
																			if (!siList.contains(si)) {
																				siList.add(si);
																			}
																		}
																	}
																	existingEntFieldDetails.setSourceInfoList(siList);
																	existingEntField.setEntryDetails(existingEntFieldDetails);

																}
															}
														} else {
															//if shadow is not tainted, just leave it. If existing is tainted/untainted, it will stay the same.
														}
													}
												}
											}
										}
										//			      				  if(updateExistingEntry(existingEnt,newEnt ))
										//			      					  predUniontable.put(key, newEnt);
									} else {
										if (shadowCopyEnt.getEntryDetails().isTainted()) //If new entry is tainted, just update the table.
										{
											if (existingEnt.getEntryDetails().isTainted()) {
												//if both tainted, check which one has more elements
												if (shadowCopyDetails.getSourceInfoList() != null
														&& existingEntDetails.getSourceInfoList() != null) {
													//				      						  if(shadowCopyDetails.getSourceInfoList().size() != existingEntDetails.getSourceInfoList().size())
													//				      						  {
													//Since shadowCopy is a deep copy, we dont want to store it in the Hashtable. Lets update the existing one.

													for (SourceInfo si : shadowCopyDetails.getSourceInfoList()) {
														if (!existingEntDetails.getSourceInfoList().contains(si))
															existingEntDetails.getSourceInfoList().add(si);
													}
													existingEnt.setEntryDetails(existingEntDetails);
													//				      						  }
												}
												// If size is equal, just leave them alone. Let them be happy.
											} else {
												//If existing is NOT tainted but shadow is tainted. Lets just copy data from shadow.
												ArrayList<SourceInfo> siList = existingEntDetails.getSourceInfoList();

												if (siList == null)
													siList = new ArrayList<SourceInfo>();
												existingEntDetails.setTainted(true);

												if (shadowCopyDetails.getSourceInfoList() != null) {
													for (SourceInfo si : shadowCopyDetails.getSourceInfoList()) {
														if (!siList.contains(si)) {
															siList.add(si);
														}
													}
												}
												existingEntDetails.setSourceInfoList(siList);
												existingEnt.setEntryDetails(existingEntDetails);

											}
											//			      					  predUniontable.put(key, newEnt);
										} else {
											//if shadow is not tainted, just leave it. If existing is tainted/untainted, it will stay the same.
										}
									}
								}
							} else {
								// if none is tainted, add entry from a basic block down in the CFG i.e. that has greater address value.
								// Precedecessors are given and parsed in this order. [ PREV = postMessengerMessage-BB@0x4e, postMessengerMessage-BB@0x7c] 
								//So we always get the latest entry. No need of this if-else condition but just to make things clear for future.
								predUniontable.put(key, newEnt);
							}
						}
					}
					//	      			  System.out.println(" key ->" + ent.getName() + ", entry -> " + ent.getType() + " , " + ent.isTainted());
				}
			}
		}
		return predUniontable;

	}

	// This function is used to get predecessors of a basic block. For now, it is being used only to
	// get OUT set of predecessors. You can return only OUT set instead of the whole basic blocks.
	public Hashtable findPredecessorsList(CFGComponent bb) {
		HashMap<String, CFGComponent> predecessorBBList = new HashMap<String, CFGComponent>();
		Hashtable predUniontable = new Hashtable(); // get union of OUT sets of predecessor basic blocks here

		ArrayList<String> predSet = ((BasicBlock) bb).getPredecessors();

		for (int i = 0; i < predSet.size(); i++) {
			String predecessorKey = predSet.get(i);
			BasicBlock bbObj = getBasicBlockByKey(predecessorKey);

			Hashtable outSet = bbObj.getOUT();

			if (outSet != null) {
				Enumeration<String> enumKey = outSet.keys();
				while (enumKey.hasMoreElements()) {
					String key = enumKey.nextElement().toString();
					SymbolTableEntry ent = (SymbolTableEntry) outSet.get(key);

					if (predUniontable.containsKey(key)) {
						SymbolTableEntry existingEnt = (SymbolTableEntry) predUniontable.get(key);

						if (existingEnt.getEntryDetails().isRecord()) {
							// If it is a record, its recordFieldList will contain many entries. Hard to find which one is tainted or not.
							//So we just update the damn table.

							//Seems like, we need to make it correct also. :(. 

							//Following function takes two entries, new and old ones and returns true if new to be added otherwise,
							// go with the old one.

							if (updateExistingEntry(existingEnt, ent))
								predUniontable.put(key, ent);

						} else {
							if (ent.getEntryDetails().isTainted()) //If new entry is tainted, just update the table.
							{
								if (existingEnt.getEntryDetails().isTainted()) {
									ArrayList<SourceInfo> siList = existingEnt.getEntryDetails().getSourceInfoList();

									if (ent.getEntryDetails().getSourceInfoList() == null) //This should never happen
										ent.getEntryDetails().setSourceInfoList(new ArrayList<SourceInfo>());

									if (siList != null) {
										for (SourceInfo si : siList) {
											if (!ent.getEntryDetails().getSourceInfoList().contains(si)) {
												ent.getEntryDetails().getSourceInfoList().add(si);
											}
										}
									}
								}
								predUniontable.put(key, ent);
							}
						}
					} else {
						// if none is tainted, add entry from a basic block down in the CFG i.e. that has greater address value.
						// Precedecessors are given and parsed in this order. [ PREV = postMessengerMessage-BB@0x4e, postMessengerMessage-BB@0x7c] 
						//So we always get the latest entry. No need of this if-else condition but just to make things clear for future.
						predUniontable.put(key, ent);
					}
				}
			}
		}

		return predUniontable;

	}

	public boolean updateExistingEntry(SymbolTableEntry oldEnt, SymbolTableEntry newEnt) {

		EntryDetails newEntDetails = newEnt.getEntryDetails();
		EntryDetails oldEntDetails = oldEnt.getEntryDetails();

		if (!newEnt.getEntryDetails().isRecord()) //if old is record, but newEnt is not. Don't update existing entry
			return false;

		if (newEntDetails.getRecordFieldList() != null && oldEntDetails.getRecordFieldList() != null) {
			if (newEntDetails.getRecordFieldList().size() < oldEntDetails.getRecordFieldList().size())
				return false;

			Hashtable oldEntRecordFiledList = oldEntDetails.getRecordFieldList();
			Hashtable newEntRecordFiledList = newEntDetails.getRecordFieldList();

			Enumeration<String> keys = newEntDetails.getRecordFieldList().keys();
			while (keys.hasMoreElements()) {
				String key = (String) keys.nextElement();

				if (oldEntRecordFiledList.containsKey(key)) {
					SymbolTableEntry oldEntChild = (SymbolTableEntry) oldEntRecordFiledList.get(key);
					SymbolTableEntry newEntChild = (SymbolTableEntry) newEntRecordFiledList.get(key);

					if (!newEntChild.getEntryDetails().isTainted() && oldEntChild.getEntryDetails().isTainted())
						return false;

					if (newEntChild.getEntryDetails().isTainted() && !oldEntChild.getEntryDetails().isTainted())
						return true;

				}

			}
		}
		return true;

	}

	public BasicBlock getRoot() {
		BasicBlock root = null;

		for (CFGComponent b : this.compCollection) {
			BasicBlock tmpBB = new BasicBlock();
			tmpBB = (BasicBlock) b;

			if (tmpBB.getBbPosition().equalsIgnoreCase("root")) {
				root = tmpBB;
			}
		}
		return root;

	}

	public BasicBlock getBasicBlockByKey(String key) {
		BasicBlock bb = null;

		for (CFGComponent b : this.compCollection) {
			BasicBlock tmpBB = new BasicBlock();
			tmpBB = (BasicBlock) b;

			if (key.equalsIgnoreCase(tmpBB.getKey())) {
				return tmpBB;
			}
		}

		return bb;
	}

	public void addItem(CFGComponent comp) {
		compCollection.add(comp);
	}

	public boolean removeItem(CFGComponent comp) {
		compCollection.remove(comp);
		return true;
	}

	public CFGComponent getItem(int index) {
		return compCollection.get(index);
	}

	//	@Override
	public Iterator iterator() {
		//		Iterator iterator = instrList.iterator();
		return (Iterator) new BBRPOIterator(this);
	}

	public void accept(Analyzer a) {

		a.analyze(this);

	}

	public void setAnalayzeType() {

	}

	public String getKey() {
		return this.key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public ArrayList<Parameter> getParamList() {
		return paramList;
	}

	public void setParamList(ArrayList<Parameter> paramList) {
		this.paramList = paramList;
	}

	public MethodSignature getSignature() {
		return signature;
	}

	public void setSignature(MethodSignature signature) {
		this.signature = signature;
	}

	public boolean isFirstTimeTraversal() {
		return isFirstTimeTraversal;
	}

	public void setFirstTimeTraversal(boolean isFirstTimeTraversal) {
		this.isFirstTimeTraversal = isFirstTimeTraversal;
	}

	public boolean isRecursive() {
		return isRecursive;
	}

	public void setRecursive(boolean isRecursive) {
		this.isRecursive = isRecursive;
	}

	public SymbolTableEntry getExceptionObject() {
		return exceptionObject;
	}

	public void setExceptionObject(SymbolTableEntry exceptionObject) {
		this.exceptionObject = exceptionObject;
	}

}
