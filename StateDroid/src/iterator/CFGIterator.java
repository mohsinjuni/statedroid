package iterator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;

import models.cfg.CFG;
import models.cfg.CFGComponent;
import models.cfg.ClassObj;
import models.cfg.Scenario;

import org.apache.log4j.Logger;

import com.sun.org.apache.xml.internal.utils.SuballocatedByteVector;

import utility.ArrayPermuter;
import configuration.Config;
import enums.ComponentTypes;
import enums.MethodTypes;


public class CFGIterator implements Iterator<Object>{

	private ClassObj currComp;
	private int currPosition;
	private ArrayList<CFGComponent> currCFGCollection;
	private ArrayList<CFGComponent> itrCFGCollection;
	private ArrayList<String> currCFGNamesColl;
	private static Logger logger;
	private Hashtable scenarioHT;
	private ArrayList<ArrayList<CFGComponent>> permSeq;
	private Hashtable eventSpecificCallbacks;

	public CFGIterator(CFGComponent myComp)
	{

		logger = Logger.getLogger(CFGIterator.class);
		currComp = (ClassObj) myComp;
		currCFGCollection = myComp.getCompCollection();
		currPosition = 0;

		itrCFGCollection = new ArrayList<CFGComponent>();
		eventSpecificCallbacks = new Hashtable();

		getCFGNames();

		int r = Config.getPermutationorder();
		setOnePermutation(r);
	}

	public void setOnePermutation(int r)
	{

		ArrayList<String> inputListForPerm = new ArrayList<String>();
		ArrayList<String> layoutMethods = ((ClassObj) currComp).getLayoutMethods();
		setEventSpecificCallbacks();
		ArrayList<String> otherCallBacks = new ArrayList<String>();

		permSeq = generateOnePermutationSeq();
		scenarioHT = new Hashtable();

		for(int k=1; k < permSeq.size(); k++)
		{
			String sequNo = String.valueOf("seqNo").concat("-").concat(String.valueOf(k));
			scenarioHT.put(sequNo, permSeq.get(k));
			inputListForPerm.add(sequNo);
		}

		HashSet<String> callBacksFromPermSeq = getUniqueCallbacks(permSeq);
		Hashtable<String, ArrayList<String>> additionalCallbacks = Config.getInstance().getAdditionalCallbacks();

		ArrayList<String> compAdditionalCallbacks = additionalCallbacks.get(currComp.getType());

		for(CFGComponent comp: currCFGCollection)
		{
			//While generating permutations, we only consider activity callback methods but for receives and service components,
			// we only marks their types as Activity but their methods are not added automatically.
			CFG cfg = (CFG) comp;
			if(cfg != null)
			{
				if( 
						(cfg.getKey().startsWith("on") 
								&& !cfg.getType().equalsIgnoreCase(MethodTypes.componentCallback.toString())
								&& Config.isIsonclickedhandlingenabled())

								|| 
								(layoutMethods != null && layoutMethods.contains(cfg.getKey()))
								||
								(compAdditionalCallbacks != null && compAdditionalCallbacks.contains(cfg.getKey()))

						)
				{

					String cfgKey = cfg.getKey();
					if(!callBacksFromPermSeq.contains(cfgKey)){
						inputListForPerm.add(cfgKey);
						callBacksFromPermSeq.add(cfgKey);
					}
					logger.info("The other callback: " + cfg.getKey());
				}
			}

		}



		//TODO		// t-way All permutation sequences
		generateAllPermutations(inputListForPerm, otherCallBacks, r);
	}

	HashSet<String> getUniqueCallbacks(ArrayList<ArrayList<CFGComponent>> listOfLists){
		HashSet<String> allCallbacks = new HashSet<String>();
		for(ArrayList<CFGComponent> list: listOfLists){
			for(CFGComponent cfg: list){
				if(!allCallbacks.contains(cfg.getKey())){
					allCallbacks.add(cfg.getKey());
				}
			}
		}
		return allCallbacks;
	}

	public void generateAllPermutations(ArrayList<String> inputListForPerm, ArrayList<String> otherCallBacks, int r)
	{

		if(inputListForPerm.size() < 1 )
		{
			//We have nothing for permutations, we just add <init> methods and scenario-0 for all components.
			CFGComponent cfgComp = (CFGComponent) getCFGByKey("<clinit>");

			String onePermStr = "";
			//Add <init> method if it exists. Typically it does.
			if(cfgComp != null)
			{
				CFGComponent emptyComp = new CFGComponent();
				emptyComp.setKey(cfgComp.getKey());
				itrCFGCollection.add(emptyComp);

				onePermStr += cfgComp.getKey() + " ";

				//				itrCFGCollection.add(cfgComp);
				itrCFGCollection.add(createBorderCFG());
			}

			CFGComponent initComp2 = (CFGComponent) getCFGByKey("<init>");

			//Add <init> method if it exists. Typically it does.
			if(initComp2 != null)
			{
				CFGComponent emptyComp = new CFGComponent();
				emptyComp.setKey(initComp2.getKey());
				itrCFGCollection.add(emptyComp);

				onePermStr += initComp2.getKey() + " ";

				//				itrCFGCollection.add(cfgComp);
				itrCFGCollection.add(createBorderCFG());
			}

			CFGComponent initComp3 = (CFGComponent) getCFGByKey("init");

			//Add <init> method if it exists. Typically it does.
			if(initComp3 != null)
			{
				CFGComponent emptyComp = new CFGComponent();
				emptyComp.setKey(initComp3.getKey());
				itrCFGCollection.add(emptyComp);

				onePermStr += initComp3.getKey() + " ";

				//				itrCFGCollection.add(cfgComp);
				itrCFGCollection.add(createBorderCFG());
			}
			//Add scenario-0. We don't want its permutations. But we need to save two types of contexts. One after <init> and one after scenario-0.
			logger.trace("<<<Scenario-0>>>");

			if(permSeq.size() > 0)
			{
				for(CFGComponent comp : permSeq.get(0))
				{
					CFGComponent emptyComp = new CFGComponent();
					if(comp != null)
					{
						emptyComp.setKey(comp.getKey());
						itrCFGCollection.add(emptyComp);

						onePermStr += comp.getKey() + " ";
						//						itrCFGCollection.add(comp);
						itrCFGCollection.add(createBorderCFG()); // It removes all junk entries from the symbolTable after a function call is over.

					}
				}
			}else{
				if(otherCallBacks != null && otherCallBacks.size()>0)
				{
					for(String st: otherCallBacks){

						CFGComponent emptyComp = new CFGComponent();
						emptyComp.setKey(st);
						itrCFGCollection.add(emptyComp);

						onePermStr += emptyComp.getKey() + " ";
						itrCFGCollection.add(createBorderCFG()); // It removes all junk entries from the symbolTable after a function call is over.
					}

				}
			}

			logger.warn("[NO permutations]" + onePermStr);

		}
		else
		{
			for(int p=1; p<=r; p++)
			{
				ArrayPermuter arrPerm = new ArrayPermuter();

				ArrayList<ArrayList<String>> permSequences = arrPerm.performNWayPermutations(inputListForPerm, p);

				logger.debug("returned from array permuter");

				if(permSequences.size() > 0)
				{
					generateNWayPermutations(permSequences, otherCallBacks, p);

					logger.error("P way permutation sequence added with P = " + p);
					itrCFGCollection.add(createBorderNWayPermutation());
				}
				else
				{
					logger.error("No permutation is produced = " + p);
				}

			}
		}
	}


	public void generateNWayPermutations(ArrayList<ArrayList<String>> permSequences, ArrayList<String> otherCallBacks, int p)
	{

		// This will be used to save context of scenario-1 and then retrieve after each scenario
		// Since other scenarios will affect global variables, we need to reset the global variables according to post-scenario-1 state
		//		itrCFGCollection.add(createBorderPermutation()); 

		//Add rest of the Permutations now.
		for(ArrayList<String> tmpPermSeq : permSequences)
		{
			String onePermStr = "";

			//we would like to re-analyze first scenario for each permutation. Due to issue of deep-copying of objects, we are going to do this way now.
			CFGComponent cfgComp = (CFGComponent) getCFGByKey("<clinit>");

			//Add <init> method if it exists. Typically it does.
			if(cfgComp != null)
			{
				CFGComponent emptyComp = new CFGComponent();
				emptyComp.setKey(cfgComp.getKey());
				itrCFGCollection.add(emptyComp);

				onePermStr += cfgComp.getKey() + " ";

				//				itrCFGCollection.add(cfgComp);
				itrCFGCollection.add(createBorderCFG());
			}

			CFGComponent initComp2 = (CFGComponent) getCFGByKey("<init>");

			//Add <init> method if it exists. Typically it does.
			if(initComp2 != null)
			{
				CFGComponent emptyComp = new CFGComponent();
				emptyComp.setKey(initComp2.getKey());
				itrCFGCollection.add(emptyComp);

				onePermStr += initComp2.getKey() + " ";

				//				itrCFGCollection.add(cfgComp);
				itrCFGCollection.add(createBorderCFG());
			}

			CFGComponent initComp3 = (CFGComponent) getCFGByKey("init");

			//Add <init> method if it exists. Typically it does.
			if(initComp3 != null)
			{
				CFGComponent emptyComp = new CFGComponent();
				emptyComp.setKey(initComp3.getKey());
				itrCFGCollection.add(emptyComp);

				onePermStr += initComp3.getKey() + " ";

				//				itrCFGCollection.add(cfgComp);
				itrCFGCollection.add(createBorderCFG());
			}
			//Add scenario-0. We don't want its permutations. But we need to save two types of contexts. One after <init> and one after scenario-0.

			logger.trace("<<<Scenario-0>>>");

			if(permSeq.size() > 0)
			{
				for(CFGComponent comp : permSeq.get(0))
				{
					//			logger.info(comp.getKey());

					CFGComponent emptyComp = new CFGComponent();
					if(comp != null)
					{
						emptyComp.setKey(comp.getKey());
						itrCFGCollection.add(emptyComp);

						onePermStr += comp.getKey() + " ";
						//						itrCFGCollection.add(comp);
						itrCFGCollection.add(createBorderCFG()); // It removes all junk entries from the symbolTable after a function call is over.

					}
				}
			}
			for (String permUnit : tmpPermSeq)
			{
				if(scenarioHT.containsKey(permUnit))
				{
					// This is a scenario. We need to all the CFGs now.
					ArrayList<CFGComponent> compList = (ArrayList<CFGComponent>) scenarioHT.get(permUnit); //ScenarioHT returns an arraylist of CFG-keys

					//					logger.trace(" <========  permUnit =========> " );
					for(CFGComponent comp : compList)
					{
						// I am adding tempCFGs with a key but with empty collections. In this way, I can reduce the size of itrCFGCollection
						// and in taintAnalyzer, I will call getCFGByKey and get this cfg item from there.

						CFGComponent emptyComp = new CFGComponent();
						emptyComp.setKey(comp.getKey());
						itrCFGCollection.add(emptyComp);

						onePermStr += comp.getKey() + " ";
						itrCFGCollection.add(createBorderCFG());
					}
				}
				else
				{
					CFGComponent tmpComp = (CFGComponent) getCFGByKey(permUnit);
					if(tmpComp != null)
					{
						CFGComponent emptyComp = new CFGComponent();
						emptyComp.setKey(tmpComp.getKey());
						itrCFGCollection.add(emptyComp);

						onePermStr += tmpComp.getKey() + " ";
						itrCFGCollection.add(createBorderCFG());
					}
				}
			}
			itrCFGCollection.add(createBorderPermutation()); //Will be used to get old context in TaintAnalyzer
			logger.error(onePermStr);
		}

	}
	public void printCurrScenarios()
	{

		logger.error("<<<<<<Scenarios start >>>>>");

		int scenCount= 0;
		for (ArrayList<CFGComponent> compList : permSeq)
		{
			logger.error("++++++ Scenario-" + scenCount + " ++++");
			for(CFGComponent comp : compList)
			{
				logger.error(comp.getKey());
			}
			scenCount++;
		}

		logger.error("<<<<<</Scenarios End >>>>>");

	}

	public void logInfoFinalCFGList()
	{
		for(CFGComponent comp : itrCFGCollection)
		{
			logger.debug(" key" + comp.getKey());
		}
	}
	public void getCFGNames()
	{
		currCFGNamesColl = new ArrayList<String>();

		for(CFGComponent comp : currCFGCollection)
		{
			currCFGNamesColl.add(comp.getKey());
		}
	}


	//TODO: Ideally it should generate these sequences for each subcomponent types: an activity has different types of child-activities in which each one has
	// different or additional callbacks. Activity, ListActivity, PreferencesActivity.
	// Soln: Each component has its type set by its parent class. Based on each component's type, gets its callback sequences.

	public ArrayList<ArrayList<CFGComponent>> generateOnePermutationSeq()
	{
		ArrayList<Scenario> scenarioKeyColl = new ArrayList<Scenario>();

		String compType = this.currComp.getType();

		if(compType.equalsIgnoreCase(ComponentTypes.activity.toString()))
		{
			scenarioKeyColl = generateActivityScenarios();
		}
		else if(compType.equalsIgnoreCase(ComponentTypes.service.toString()))
		{
			scenarioKeyColl = generateServiceScenarios();
		}
		else if(compType.equalsIgnoreCase(ComponentTypes.broadcastReceiver.toString()))
		{
			scenarioKeyColl = generateBroadcastReceiverScenarios();
		}
		else if(compType.equalsIgnoreCase(ComponentTypes.task.toString()))
		{
			scenarioKeyColl = generateAsynchTaskScenarios();
		}
		else if(compType.equalsIgnoreCase(ComponentTypes.thread.toString()))
		{
			scenarioKeyColl = generateThreadScenarios();
		}
		else if(compType.equalsIgnoreCase(ComponentTypes.application.toString()))
		{
			scenarioKeyColl = generateApplicationScenarios();
		}
		else if(compType.equalsIgnoreCase(ComponentTypes.contentProvider.toString()))
		{
			scenarioKeyColl = generateContentProviderScenarios();
		}
		else if(compType.equalsIgnoreCase(ComponentTypes.contentObserver.toString()))
		{
			scenarioKeyColl = generateContentObserverScenarios();
		}
		else if(compType.equalsIgnoreCase(ComponentTypes.adapter.toString()))
		{
			scenarioKeyColl = generateAdapterScenarios();
		}
		else if(compType.equalsIgnoreCase(ComponentTypes.viewGroup.toString()))
		{
			scenarioKeyColl = generateViewGroupScenarios();
		}

		ArrayList<ArrayList<CFGComponent>> scenarioColl = new ArrayList<ArrayList<CFGComponent>>(); 
		ArrayList<ArrayList<String>> scenarioKeysList= new ArrayList<ArrayList<String>>(); 

		for (Scenario scen : scenarioKeyColl){

			ArrayList<String> currScenKeys = new ArrayList<String>();

			ArrayList<String> scenarTemplate = scen.getSequenceItems();
			boolean isListNotEmptry = false;
			String lastMthdName = "";

			for(int i=0; i < scenarTemplate.size(); i++)
			{
				String mthdName = scenarTemplate.get(i);
				if(currCFGNamesColl.contains(mthdName) && !mthdName.equalsIgnoreCase(lastMthdName))
				{
					currScenKeys.add(mthdName);
					lastMthdName = mthdName;
					isListNotEmptry = true;
				}
			}
			if(isListNotEmptry)
			{
				if(isKeySequenceUnique(scenarioKeysList, currScenKeys))
				{
					scenarioKeysList.add(currScenKeys);
				}
			}
		}

		if(scenarioKeysList.size() > 0) {
			ArrayList<String> scenarioZero = scenarioKeysList.get(0);
			scenarioKeysList.remove(0);
			ArrayList<CFGComponent> currScenCFGs = new ArrayList<CFGComponent>();

			for(int i=0; i < scenarioZero.size(); i++)
			{
				String mthdName = scenarioZero.get(i);
				CFGComponent cfgComp = getCFGByKey(mthdName);
				currScenCFGs.add(cfgComp);
			}
			scenarioColl.add(currScenCFGs);

			ArrayList<ArrayList<String>> uniqueScenarioKeys = removeSubsetScenarios(scenarioKeysList);
			for (ArrayList<String> scen : uniqueScenarioKeys)
			{
				currScenCFGs = new ArrayList<CFGComponent>();
				for(int i=0; i < scen.size(); i++)
				{
					String mthdName = scen.get(i);
					CFGComponent cfgComp = getCFGByKey(mthdName);
					currScenCFGs.add(cfgComp);
				}
				scenarioColl.add(currScenCFGs);
			}
		}
		return scenarioColl;
	}

	public ArrayList<ArrayList<String>> removeSubsetScenarios(ArrayList<ArrayList<String>> scenarioColl){
		Collections.sort(scenarioColl, new Comparator<ArrayList<String>>(){
			@Override
			public int compare(ArrayList<String> list1, ArrayList<String> list2){
				return list1.size() - list2.size();
			}
		});

		ArrayList<ArrayList<String>> uniqueScenarioKeys = new ArrayList<ArrayList<String>>(); 
		int scenCount = scenarioColl.size();
		for(int i=0; i< scenCount; i++){
			boolean isSubsumable = false;
			for(int j= i+1; j < scenCount; j++){
				if(isSubsumable(scenarioColl.get(i), scenarioColl.get(j))){
					isSubsumable = true;
					break;
				}
			}
			if(!isSubsumable){
				uniqueScenarioKeys.add(scenarioColl.get(i));
			}
		}
		return uniqueScenarioKeys;
	}

	public boolean isSubsumable(ArrayList<String> list1, ArrayList<String> list2){
		boolean returnVal = true;
		for(int i=0; i< list1.size(); i++){
			String st1 = list1.get(i);
			String st2 = list2.get(i);
			if(!st1.equalsIgnoreCase(st2)){
				return false;
			}
		}
		return returnVal;
	}

	public void printSequence(ArrayList<CFGComponent> currScenCFGs)
	{
		String seqKeys = "";
		for(CFGComponent comp : currScenCFGs)
		{
			seqKeys += comp.getKey();
		}
		//		logger.error(seqKeys);
	}
	public boolean isKeySequenceUnique(ArrayList<ArrayList<String>> scenarioKeyColl, ArrayList<String> keyListParam)
	{
		boolean isUnique = true;

		for( ArrayList<String> compList : scenarioKeyColl)
		{
			if(compList.size() == keyListParam.size())
			{
				boolean allItemsMatched = true;
				for (int i=0; i < compList.size(); i++)
				{
					if(!compList.get(i).equalsIgnoreCase(keyListParam.get(i)))
					{
						allItemsMatched = false;
						break;
					}
				}
				if(allItemsMatched)
				{
					return false;
				}
			}
		}
		return isUnique;
	}

	public boolean isSequenceUnique(ArrayList<ArrayList<CFGComponent>> scenarioColl, ArrayList<CFGComponent> compListParam)
	{
		boolean isUnique = true;

		for( ArrayList<CFGComponent> compList : scenarioColl)
		{
			if(compList.size() == compListParam.size())
			{
				boolean allItemsMatched = true;
				for (int i=0; i < compList.size(); i++)
				{
					if(!compList.get(i).getKey().equalsIgnoreCase(compListParam.get(i).getKey()))
					{
						allItemsMatched = false;
						break;
					}
				}
				if(allItemsMatched)
				{
					return false;
				}
			}
		}
		return isUnique;
	}

	public CFGComponent createBorderCFG()
	{
		CFGComponent cfg = new CFG();

		cfg.setKey("borderCFG");

		return cfg;
	}

	public CFGComponent createBorderNWayPermutation()
	{
		CFGComponent cfg = new CFG();

		cfg.setKey("borderNWayPermutation");

		return cfg;
	}

	public CFGComponent createBorderPermutation()
	{
		CFGComponent cfg = new CFG();

		cfg.setKey("borderPermutation");

		return cfg;
	}

	public void setEventSpecificCallbacks()
	{
		this.eventSpecificCallbacks = Config.getInstance().getEventSpecificCallbacks();
	}

	public ArrayList<String> getEventSpecificCallbackSequence(String event)
	{
		ArrayList<String> callbackList = (ArrayList<String>) this.eventSpecificCallbacks.get(event);

		return callbackList;

	}
	public ArrayList<Scenario> generateServiceScenarios()
	{

		ArrayList<Scenario> scenarioColl;

		scenarioColl = new ArrayList<Scenario>();

		Scenario scen0 = new Scenario();
		ArrayList<String> scen0Coll = new ArrayList<String>();
		scen0Coll.add("onCreate");
		scen0.setSequenceItems(scen0Coll);

		Scenario scen1 = new Scenario();
		ArrayList<String> scen1Coll = new ArrayList<String>();
		scen1Coll.add("onCreate");
		scen1Coll.add("onStartCommand");
		scen1Coll.add("onStart");
		scen1Coll.add("onDestroy");
		scen1.setSequenceItems(scen1Coll);

		Scenario scen2 = new Scenario();
		ArrayList<String> scen2Coll = new ArrayList<String>();
		scen2Coll.add("onCreate");
		scen2Coll.add("onStartCommand");
		scen2Coll.add("onStart");
		scen2Coll.add("onHandleIntent");
		scen2Coll.add("onDestroy");
		scen2.setSequenceItems(scen2Coll);

		Scenario scen3 = new Scenario();
		ArrayList<String> scen3Coll = new ArrayList<String>();
		scen3Coll.add("onCreate");
		scen3Coll.add("onStart");
		scen3Coll.add("onBind");
		scen3Coll.add("onUnbind");
		scen3Coll.add("onDestroy");
		scen3.setSequenceItems(scen3Coll);


		Scenario scen4 = new Scenario();
		ArrayList<String> scen4Coll = new ArrayList<String>();
		scen3Coll.add("onCreate");
		scen3Coll.add("onStart");
		scen3Coll.add("onBind");
		scen4Coll.add("onUnbind");
		scen4Coll.add("onRebind");
		scen4Coll.add("onUnbind");
		scen4Coll.add("onDestroy");
		scen4.setSequenceItems(scen4Coll);

		Scenario scen5 = new Scenario();
		ArrayList<String> scen5Coll = new ArrayList<String>();
		scen5Coll.add("onCreate");
		scen5Coll.add("onStart");
		scen5Coll.add("onBind");
		scen5Coll.add("handleMessage");
		scen5Coll.add("onUnbind");
		scen5Coll.add("onDestroy");
		scen5.setSequenceItems(scen5Coll);

		// startService -> bindService -> startService -> stopService -> unbindService 
		Scenario scen6 = new Scenario();
		ArrayList<String> scen6Coll = new ArrayList<String>();
		scen6Coll.add("onCreate");
		scen6Coll.add("onStartCommand");
		scen6Coll.add("onBind");
		scen6Coll.add("onUnbind");
		scen6Coll.add("onDestroy");
		scen6.setSequenceItems(scen6Coll);

		Scenario scen7 = new Scenario();
		ArrayList<String> scen7Coll = new ArrayList<String>();
		scen7Coll.add("onCreate");
		scen7Coll.add("onStartCommand");
		scen7Coll.add("onBind");
		scen7Coll.add("onStartCommand");
		scen7Coll.add("onUnbind");
		scen7Coll.add("onDestroy");
		scen7.setSequenceItems(scen7Coll);

		Scenario scen8 = new Scenario();
		ArrayList<String> scen8Coll = new ArrayList<String>();
		scen8Coll.add("onCreate");
		scen8Coll.add("onBind");
		scen8Coll.add("onStartCommand");
		scen8Coll.add("onHandleIntent");
		scen8Coll.add("onUnbind");
		scen8Coll.add("onDestroy");
		scen8.setSequenceItems(scen8Coll);

		Scenario scen9 = new Scenario();
		ArrayList<String> scen9Coll = new ArrayList<String>();
		scen9Coll.add("onCreate");
		scen9Coll.add("onBind");
		scen9Coll.add("onStartCommand");
		scen9Coll.add("onUnbind");
		scen9Coll.add("onDestroy");
		scen9.setSequenceItems(scen9Coll);


		Scenario scen10 = new Scenario();
		ArrayList<String> scen10Coll = new ArrayList<String>();
		scen10Coll.add("onCreate");
		scen10Coll.add("onStart");
		scen10Coll.add("onBind");
		scen10Coll.add("onUnbind");
		scen10Coll.add("onRebind");
		scen10Coll.add("onUnbind");
		scen10Coll.add("onDestroy");
		scen10.setSequenceItems(scen10Coll);


		scenarioColl.add(scen0);
		scenarioColl.add(scen1);
		scenarioColl.add(scen2);
		scenarioColl.add(scen3);
		scenarioColl.add(scen4);
		scenarioColl.add(scen5);
		scenarioColl.add(scen6);
		scenarioColl.add(scen7);
		scenarioColl.add(scen8);
		scenarioColl.add(scen9);
		scenarioColl.add(scen10);

		return scenarioColl;
	}

	public ArrayList<Scenario> generateActivityScenarios()
	{

		//TODO Add all callback functions. One is missing, I think.		

		ArrayList<Scenario> scenarioColl;

		scenarioColl = new ArrayList<Scenario>();
		Scenario scen1 = new Scenario();

		ArrayList<String> scen1Coll = new ArrayList<String>();

		scen1Coll.add("onCreate");
		scen1Coll.add("onStart");
		scen1Coll.add("onPostCreate");
		scen1Coll.add("onResume");
		scen1Coll.add("onPostResume");
		scen1.setSequenceItems(scen1Coll);
		scenarioColl.add(scen1);

		String[] eventSequences = new String[]{ 
				"stopActivity : restartActivity",
				"stopActivity : confChngPOS",
				"stopActivity : killProcess : createActivity",
				"confChngPR",
				"backPressed : createActivity",
				"overlapActivity : restartActivity",
				"overlapActivity : killProcess : createActivity",
				"hideActivityPartially : gotoActivity",
				"hideActivityPartially : confChngSTP : gotoActivity",
				"hideActivityPartially : confChngSTP : confChngPAU : gotoActivity",
				"createActivity : hideActivityPartially : confChngSTP : confChngPAU : gotoStop : killProcess : createActivity",
				"hideActivityPartially : confChngSTP : gotoStop : confChngSTO : gotoActivity",
				"hideActivityPartially : confChngSTP : gotoStop : confChngSTO : gotoStop : killProcess : createActivity",
				"hideActivityPartially : confChngSTP : gotoStop : killProcess : createActivity",
				"hideActivityPartially : savStop : savRestart : gotoActivity",
				"hideActivityPartially : savStop : savRestart : confChngSTP : gotoActivity",
				"hideActivityPartially : savStop : savRestart : confChngSTP : confChngPAU : gotoActivity",
				"hideActivityPartially : savStop : savRestart : confChngSTP : confChngPAU : gotoStop : killProcess : createActivity",
				"hideActivityPartially : savStop : savRestart : confChngSTP : gotoStop : confChngSTO : gotoActivity",
				"hideActivityPartially : savStop : savRestart : confChngSTP : gotoStop : killProcess : createActivity",
				"hideActivityPartially : savStop : savRestart : savStop : savRestart : gotoActivity",
				"hideActivityPartially : savStop : savRestart : savStop : savRestart : confChngSTP : gotoActivity",
				"hideActivityPartially : savStop : savRestart : savStop : savRestart : confChngSTP : confChngPAU : gotoActivity",
				"hideActivityPartially : savStop : savRestart : savStop : killProcess : createActivity",
				"hideActivityPartially : savStop : killProcess : createActivity"
		};


		for(String eventSeq : eventSequences)
		{
			Scenario scen = new Scenario();
			ArrayList<String> scenColl = new ArrayList<String>();

			String[] events = eventSeq.split(":");
			for(String event: events)
			{
				ArrayList<String> callbackList = this.getEventSpecificCallbackSequence(event.trim());
				if(callbackList == null)
					System.out.println(event);
				scenColl.addAll(callbackList);
				//scen1Coll = getUniqueCallbacksInAList(scenColl, callbackList);
			}
			scen.setSequenceItems(scenColl);
			scenarioColl.add(scen);
		}

		return scenarioColl;
	}

	public ArrayList<String> getUniqueCallbacksInAList(ArrayList<String> previousScenarios, ArrayList<String> inputList){

		if(inputList != null && inputList.size() > 0){
			if(previousScenarios.size()> 0){
				String lastIndexedItem = previousScenarios.get(previousScenarios.size()-1);
				for(String st: inputList){
					if(!lastIndexedItem.equalsIgnoreCase(st)){
						previousScenarios.add(st);
						lastIndexedItem = st;
					}
				}
			}
		}


		return previousScenarios;
	} 
	public ArrayList<Scenario> generateApplicationScenarios()
	{

		//TODO Add all callback functions. One is missing, I think.		
		ArrayList<Scenario> scenarioColl;

		scenarioColl = new ArrayList<Scenario>();
		Scenario scen1 = new Scenario();
		ArrayList<String> scen1Coll = new ArrayList<String>();

		scen1Coll.add("onCreate");
		scen1Coll.add("onDestroy");
		scen1.setSequenceItems(scen1Coll);

		scenarioColl.add(scen1);

		return scenarioColl;
	}
	public ArrayList<Scenario> generateContentProviderScenarios()
	{

		//TODO Add all callback functions. One is missing, I think.		
		ArrayList<Scenario> scenarioColl;

		scenarioColl = new ArrayList<Scenario>();
		Scenario scen1 = new Scenario();
		ArrayList<String> scen1Coll = new ArrayList<String>();

		scen1Coll.add("onCreate");
		scen1.setSequenceItems(scen1Coll);

		scenarioColl.add(scen1);

		return scenarioColl;
	}


	public ArrayList<Scenario> generateAsynchTaskScenarios()
	{
		ArrayList<Scenario> scenarioColl;

		scenarioColl = new ArrayList<Scenario>();
		Scenario scen1 = new Scenario();
		ArrayList<String> scen1Coll = new ArrayList<String>();

		scen1Coll.add("onPreExecute");
		scen1Coll.add("doInBackground");
		scen1Coll.add("onProgressUpdate");
		scen1Coll.add("onPostExecute");
		scen1.setSequenceItems(scen1Coll);

		scenarioColl.add(scen1);


		return scenarioColl;
	}

	public ArrayList<Scenario> generateThreadScenarios()
	{
		ArrayList<Scenario> scenarioColl;

		scenarioColl = new ArrayList<Scenario>();
		Scenario scen1 = new Scenario();
		ArrayList<String> scen1Coll = new ArrayList<String>();

		scen1Coll.add("run");
		scen1.setSequenceItems(scen1Coll);

		scenarioColl.add(scen1);


		return scenarioColl;
	}

	public ArrayList<Scenario> generateBroadcastReceiverScenarios()
	{

		//TODO Add all callback functions. One is missing, I think.		
		ArrayList<Scenario> scenarioColl;

		scenarioColl = new ArrayList<Scenario>();
		Scenario scen1 = new Scenario();
		ArrayList<String> scen1Coll = new ArrayList<String>();

		scen1Coll.add("onReceive");
		scen1.setSequenceItems(scen1Coll);



		scenarioColl.add(scen1);

		return scenarioColl;
	}

	public ArrayList<Scenario> generateAdapterScenarios()
	{

		//TODO Add all callback functions. One is missing, I think.		
		ArrayList<Scenario> scenarioColl;

		scenarioColl = new ArrayList<Scenario>();
		Scenario scen1 = new Scenario();
		ArrayList<String> scen1Coll = new ArrayList<String>();

		scen1Coll.add("handle");
		scen1.setSequenceItems(scen1Coll);
		scenarioColl.add(scen1);

		return scenarioColl;
	}

	public ArrayList<Scenario> generateViewGroupScenarios()
	{
		//TODO: Callbacks can be invoked in different orders. Need to explore more of it in the future
		ArrayList<Scenario> scenarioColl;

		scenarioColl = new ArrayList<Scenario>();
		Scenario scen1 = new Scenario();
		ArrayList<String> scen1Coll = new ArrayList<String>();

		HashSet<String> callbacks = Config.getInstance().getViewGroupCallbacks();
		Iterator it = callbacks.iterator();
		while(it.hasNext()){
			String callback = (String) it.next();
			//			scen1Coll.add(callback);
		}
		scen1.setSequenceItems(scen1Coll);
		scenarioColl.add(scen1);

		return scenarioColl;
	}

	public ArrayList<Scenario> generateContentObserverScenarios()
	{

		//TODO Add all callback functions. One is missing, I think.		
		ArrayList<Scenario> scenarioColl;

		scenarioColl = new ArrayList<Scenario>();
		Scenario scen1 = new Scenario();
		ArrayList<String> scen1Coll = new ArrayList<String>();

		scen1Coll.add("onChange");
		scen1.setSequenceItems(scen1Coll);



		scenarioColl.add(scen1);

		return scenarioColl;
	}
	//TODO	// Typically we don't have any overloaded callback methods, so I am going with that assumption for now.

	public CFGComponent getCFGByKey(String key)
	{

		for(CFGComponent comp : currCFGCollection)
		{
			if(comp.getKey().equalsIgnoreCase(key))
			{
				return comp;
			}
		}
		return null;
	}

	public boolean hasNext()
	{
		if(currPosition < itrCFGCollection.size())
			return true;
		return false;
	}
	public Object next()
	{
		Object obj =  itrCFGCollection.get(currPosition);
		currPosition += 1;

		return obj;
	}

	public boolean remove(Object obj)
	{
		if(currComp.removeItem((CFGComponent)obj))
		{
			currPosition -= 1;
			return true;
		}
		return false;
	}

	@Override
	public String toString()
	{
		return currComp.getItem(currPosition).toString();
	}

	@Override
	public void remove() {
		// TODO Auto-generated method stub

	}

}
