package iterator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Stack;

import models.cfg.BasicBlock;
import models.cfg.CFG;
import models.cfg.CFGComponent;
import models.cfg.Parameter;

import org.apache.log4j.Logger;

import configuration.Config;



public class BBRPOIterator implements Iterator<Object>{

	private CFG currentCFG;
	private int currentPosition;
	private ArrayList<CFGComponent> currBBCollection;
	private ArrayList<CFGComponent> currRPOBBCollection;
	private static Logger logger;
	private ArrayList<String> suplementaryEdges;
	private Hashtable additionalEdges;
	boolean isComponentTimedOut = false;

	public BBRPOIterator(CFG cfg)
	{
		currentCFG =  cfg;
		currBBCollection = cfg.getCompCollection();
		currentPosition = 0;
		currRPOBBCollection = new ArrayList<CFGComponent>();
		suplementaryEdges = new ArrayList<String>();
		additionalEdges = new Hashtable();
		logger = Logger.getLogger(BBRPOIterator.class);

		removeLoops();
		computeBBOrder();

		if(!isComponentTimedOut){
			printRPOOrder();
		}
	}

	public boolean hasNext()
	{
		if(currentPosition < currRPOBBCollection.size())
			return true;
		return false;
	}
	public Object next()
	{
		Object obj =  currRPOBBCollection.get(currentPosition);
		currentPosition += 1;

		return obj;
	}

	public boolean remove(Object obj)
	{
		if(currentCFG.removeItem((CFGComponent)obj))
		{
			currentPosition -= 1;
			return true;
		}
		return false;
	}


	/**
	 * Computes the BB visit order
	 */
	public void computeBBOrder(){

		//Get the post order traversal of the BBs

		//		printSuccessors();

		BasicBlock firstBB = (BasicBlock) this.currBBCollection.get(0);

		logger.debug("instrrrr >> " + firstBB.getCompCollection().get(0).getText());

		BasicBlock root = new BasicBlock();

		root = getRoot();

		makeAllUnvisited();
		if(Config.getInstance().isExceptionHandling()){
			addExceptionBasicBlocks();		// This sucks the return values of a method if we keep it at the end. Keeping it at the start
			// does not help because it would not get any updated value from the method itself.
		}


		if(!this.currentCFG.isRecursive())
		{
			iterativePostOrderTraversalModifiedTimed(root);
		}
		else
		{
			//			logger.fatal(currentCFG.getCurrPkgClassName() +  "; " + this.currentCFG.getKey() + " >> damn, its recursive");
			iterativePostOrderTraversalModified(root);

		}
		if(!isComponentTimedOut){
			appendExceptionBasicBlocks();
		}
	}
	public void addExceptionBasicBlocks()
	{
		//Find first exception thrower basic block; find its position in currRPOCollection. Get ecepti 
		BasicBlock root = getRoot();
		for (CFGComponent bb : currBBCollection)
		{
			BasicBlock b = (BasicBlock) bb;
			if(b.getPredecessors().size() > 0
					&& b.getPredecessors().get(0).equalsIgnoreCase("exceptionCatcher") //TODO: Add a better strategy to determine exception-throwing basic block
					){
				//TODO, exception block should be handled at both start and end, may be
				// but we will get its successor's predecessors, and add them as its predecessors

				String predKey = b.getText();
				BasicBlock predecessor = getBasicBlockByKey(predKey);

				if(predecessor != null){
					b.getPredecessors().remove(0);
					b.getPredecessors().add(predKey);
					if(predecessor != null){
						predecessor.getSuccessors().add(b.getKey());
					}
				}
			}
		}
	}
	public void appendExceptionBasicBlocks()
	{
		BasicBlock root = getRoot();
		for (CFGComponent bb : currBBCollection)
		{
			BasicBlock b = (BasicBlock) bb;
			if(b.getPredecessors().size() > 0
					&& b.getPredecessors().get(0).equalsIgnoreCase("exceptionCatcher") //TODO: Add a better strategy to determine exception-throwing basic block
					&& b.getText().isEmpty()
					&& b.getSuccessors().size() > 0
					){

				String successorKey = b.getSuccessors().get(0);
				BasicBlock successor = getBasicBlockByKey(successorKey);

				b.getPredecessors().remove(0);
				for(String pred: successor.getPredecessors())
				{
					if(!pred.equalsIgnoreCase(b.getKey()))
					{
						b.getPredecessors().add(pred);
					}
				}
				int len = currRPOBBCollection.size();
				this.currRPOBBCollection.add(len-1, b);
			}
		}
	}
	public void  makeAllUnvisited()
	{
		for (CFGComponent bb : currBBCollection)
		{
			BasicBlock b = (BasicBlock) bb;
			b.setVisited(false);
		}
	}

	public void printComponents()
	{
		logger.trace("Before algorithms, order is below; CFG ++ " + currentCFG.getKey() + ", pkgCls = " + currentCFG.getCurrPkgClassName() );
		for (CFGComponent bb : currBBCollection)
		{
			logger.trace(bb.getKey());
		}
	}

	/**
	 * Performs post order traversal
	 * @param root the root node
	 * @param list list of nodes in post order
	 */

	private void postOrderTraversal(BasicBlock root)
	{
		//Traverse each of the children

		ArrayList<String> succList = root.getSuccessors();
		for(int i=succList.size()-1; i>=0;  i--)
		{
			String childKey = succList.get(i);
			BasicBlock childBB = getBasicBlockByKey(childKey);
			logger.warn("Root -> " + root.getKey() + " @child key Obtained >>>" + childBB.getKey() + ", isVisited>> " + childBB.isVisited());
			if(!childBB.isVisited())
			{
				postOrderTraversal(childBB);
			}
		}
		if(!root.isVisited())
		{
			root.setVisited(true);
			currRPOBBCollection.add(root);

			logger.warn("@Key marked as visited and added ::::>>>>>" + root.getKey() + ", isVisited" + root.isVisited());

		}
	}

	private void iterativePostOrderTraversal(BasicBlock root)
	{	
		Stack s1 = new Stack();
		Stack s2 = new Stack();

		logger.debug("<-------- start of iteravtive POT --------------->");
		// push root to first stack
		root.setVisited(true);
		s1.push(root);

		// Run while first stack is not empty
		while (!s1.empty())
		{
			// Pop an item from s1 and push it to s2
			BasicBlock bb = (BasicBlock) s1.pop();
			s2.push(bb);
			currRPOBBCollection.add(bb);
			//	        logger.debug("bbID " + bb.getKey());

			// Push left and right children of removed item to s1
			ArrayList<String> succList = bb.getSuccessors();
			for(int i=succList.size()-1; i>= 0 ; i--)

			{
				String childKey = succList.get(i);
				BasicBlock childBB = getBasicBlockByKey(childKey);

				if(!childBB.isVisited())
				{
					childBB.setVisited(true);
					s1.push(childBB);
				}
			}
		}

		// Print all elements of second stack
		while (!s2.isEmpty())
		{
			BasicBlock bb = (BasicBlock) s2.pop();
			logger.debug("Popped from iterative order = " + bb.getKey() );

		}
		logger.debug(" end iterative order ");

	}
	private void iterativePostOrderTraversalModifiedTimed(BasicBlock root)
	{	
		Stack s1 = new Stack();
		Stack s2 = new Stack();

		long startTime = System.currentTimeMillis();
		long endTime = System.currentTimeMillis();
		boolean isBreakTrue = false;
		String params = "";
		for(Parameter pm : this.currentCFG.getParamList()){
			params += pm.getType() + ", ";
		}
		String info = this.currentCFG.getCurrPkgClassName() + "; " + this.currentCFG.getKey() + "( " + params + " )";
		String loggingInfo = "<-------- iterativePostOrderTraversalModifiedTimed for " + info + " --------------->";
		logger.error(loggingInfo);
		// push root to first stack
		root.setVisited(true);
		s1.push(root);

		// Run while first stack is not empty
		while (!s1.empty())
		{
			// Peeks an item from s1 
			BasicBlock bb = (BasicBlock) s1.peek();

			if(Config.getInstance().isComponentAnalysisTimedOut()){
				isComponentTimedOut = true;
				//	    	  s1.setSize(0);
				break;
			}

			// Push left and right children of removed item to s1
			ArrayList<String> succList = bb.getSuccessors();

			boolean areAllLeafNodeDone = true; // This will remain true if there is no child or if all of the children are visited.
			for(int i=0; i < succList.size(); i++)
			{
				String childKey = succList.get(i);
				BasicBlock childBB = getBasicBlockByKey(childKey);

				if(!childBB.isVisited())
				{
					if(s1.contains(childBB))
					{
						s1.remove(childBB);
						logger.debug("Item removed:" + childKey);
						endTime = System.currentTimeMillis();
						long diffTime = endTime-startTime;

						if(diffTime > 5000)
						{
							isBreakTrue = true;
							break;
						}

					}
					areAllLeafNodeDone = false;
					logger.debug("Item being added to stack:" + childKey);
					s1.push(childBB);
				}
			}
			if(areAllLeafNodeDone)
			{
				BasicBlock bb2 = (BasicBlock) s1.pop();
				bb2.setVisited(true);
				logger.debug("Item being visited: " + bb2.getKey()) ;

				if(this.currentCFG.getKey().equalsIgnoreCase("onCreate"))
					logger.error("onCreate: " + bb2.getKey()) ;

				this.currRPOBBCollection.add(bb2);
				s2.push(bb2);
			}
		}

		if(!isComponentTimedOut){
			if(!isBreakTrue)
			{
				// Print all elements of second stack
				while (!s2.isEmpty())
				{
					BasicBlock bb = (BasicBlock) s2.pop();
					logger.debug("Popped from iterative order = " + bb.getKey() );

				}
				logger.debug(" end iterative order ");

				Collections.reverse(currRPOBBCollection);
			}
			else
			{
				logger.error("Break condition true: infinite loop, for CFG = " + currentCFG.getCurrPkgClassName() + "; " + currentCFG.getKey());
				//Lets just add all elements as it is.
				this.currentCFG.setRecursive(true);
				this.currRPOBBCollection = new ArrayList<CFGComponent>();

				Enumeration<BasicBlock> keys = additionalEdges.keys();

				while(keys.hasMoreElements())
				{
					BasicBlock culpritBB = keys.nextElement();
					String successorKeyToBeRemoved = (String) additionalEdges.get(culpritBB);

					ArrayList<String> successors = culpritBB.getSuccessors();
					successors.remove(successorKeyToBeRemoved);
					culpritBB.setSuccessors(successors);
				}

				root = new BasicBlock();

				root = getRoot();

				makeAllUnvisited();

				//	    		logger.fatal("second call for BBIterator for CFG = " + currentCFG.getCurrPkgClassName() + "; " + currentCFG.getKey());	    		
				iterativePostOrderTraversalModified(root);	    	

			}
		}
	}
	private void iterativePostOrderTraversalModifiedSept28(BasicBlock root)
	{	
		Stack s1 = new Stack();
		Stack s2 = new Stack();

		logger.debug("<-------- start of iteravtive POT --------------->");
		// push root to first stack
		root.setVisited(true);
		s1.push(root);

		// Run while first stack is not empty
		while (!s1.empty())
		{
			// Peeks an item from s1 
			BasicBlock bb = (BasicBlock) s1.peek();

			// Push left and right children of removed item to s1
			ArrayList<String> succList = bb.getSuccessors();

			boolean areAllLeafNodeDone = true; // This will remain true if there is no child or if all of the children are visited.
			for(int i=0; i < succList.size(); i++)
			{
				String childKey = succList.get(i);
				//				logger.debug("successor of root = " + root.getKey() + " is count="+ succList.size() + ", childKey = " + childKey);
				BasicBlock childBB = getBasicBlockByKey(childKey);

				if(!childBB.isVisited())
				{
					if(s1.contains(childBB))
					{
						s1.remove(childBB);
						logger.debug("Item removed:" + childKey);

					}
					areAllLeafNodeDone = false;
					logger.debug("Item being added to stack:" + childKey);
					s1.push(childBB);
				}
			}
			if(areAllLeafNodeDone)
			{
				BasicBlock bb2 = (BasicBlock) s1.pop();
				bb2.setVisited(true);
				logger.debug("Item being visited: " + bb2.getKey()) ;

				this.currRPOBBCollection.add(bb2);
				s2.push(bb2);
			}
		}

		// Print all elements of second stack
		while (!s2.isEmpty())
		{
			BasicBlock bb = (BasicBlock) s2.pop();
			logger.debug("Popped from iterative order = " + bb.getKey() );

		}
		logger.debug(" end iterative order ");

	}
	private void iterativePostOrderTraversalModified(BasicBlock root)
	{	
		Stack s1 = new Stack();
		Stack s2 = new Stack();

		logger.error("<-------- iterativePostOrderTraversalModified for " + this.currentCFG.getKey() + " --------------->");

		// push root to first stack
		root.setVisited(true);
		s1.push(root);

		// Run while first stack is not empty
		while (!s1.empty())
		{
			// Peeks an item from s1 
			BasicBlock bb = (BasicBlock) s1.peek();
			if(Config.getInstance().isComponentAnalysisTimedOut()){
				isComponentTimedOut = true;
				break;
			}

			// Push left and right children of removed item to s1
			ArrayList<String> succList = bb.getSuccessors();

			boolean areAllLeafNodeDone = true; // This will remain true if there is no child or if all of the children are visited.
			for(int i=0; i < succList.size(); i++)
			{
				String childKey = succList.get(i);
				BasicBlock childBB = getBasicBlockByKey(childKey);

				if(!childBB.isVisited())
				{
					if(!s1.contains(childBB))
					{
						areAllLeafNodeDone = false;
						logger.debug("Just before adding:" + childKey);
						s1.push(childBB);
					}
				}
			}
			if(areAllLeafNodeDone)
			{
				BasicBlock bb2 = (BasicBlock) s1.pop();
				bb2.setVisited(true);
				logger.debug("Item being visited: " + bb2.getKey()) ;

				this.currRPOBBCollection.add(bb2);
				s2.push(bb2);
			}
		}

		if(!isComponentTimedOut){
			// Print all elements of second stack
			while (!s2.isEmpty())
			{
				BasicBlock bb = (BasicBlock) s2.pop();
				logger.debug("Popped from iterative order = " + bb.getKey() );

			}
			logger.debug(" end iterative order ");
			Collections.reverse(this.currRPOBBCollection);
		}

	}


	public void removeLoops()
	{
		//		logger.debug("Before removing back edges");
		//		printPredSuccessors();

		calculateFixedPointSoln();
		printDominatorSets();

		removeBackEdges();

		printPredSuccessors();

		//		logger.debug("Another call to remove back edges:");
		logger.debug("After removing back edges");

	}


	public void printRPOOrder()
	{
		logger.debug("after algorithms, order is below " + currRPOBBCollection.size());
		for (int i=0; i < currRPOBBCollection.size(); i++)
		{
			BasicBlock bb = (BasicBlock) currRPOBBCollection.get(i);

			logger.debug(bb.getKey());
		}
	}
	public void removeBackEdges()
	{
		for (int i=0; i < currBBCollection.size(); i++)
		{
			BasicBlock bb = (BasicBlock) currBBCollection.get(i);
			String currBBKey = bb.getKey();


			ArrayList<String> successorsList = bb.getSuccessors();
			ArrayList<String> dominatorsList = bb.getDominators();
			ArrayList<String> predecessorList = bb.getPredecessors();

			//			logger.debug("@removeBackEdge(): bbKey " + bb.getKey());
			for(int j=0; j< dominatorsList.size(); j++)
			{
				String dominator = dominatorsList.get(j);

				//				logger.fatal("dominator "+ dominator);
				if(successorsList.contains(dominator))
				{
					//Back Edge found.
					// We remove the back edge by removing dominator-BB-key from succlist of src and predcessorList of dest BB.
					//					int dominatorIndex = successorsList.indexOf(dominator);
					//					String key = successorsList.get(dominatorIndex);

					BasicBlock destBBOfBackEdge = getBasicBlockByKey(dominator);
					ArrayList<String> destPredecessorList = destBBOfBackEdge.getPredecessors();

					if(destPredecessorList.contains(currBBKey))
					{
						destPredecessorList.remove(currBBKey);
						destBBOfBackEdge.setPredecessors(destPredecessorList); // Though not needed.
					}

					logger.error("<BackEdge> removed from: " + currBBKey + ", TO: " + dominator );

					// Now remove destBBKey (which is dominator) from successor List of current BB.	
					successorsList.remove(dominator);


					ArrayList<String> destBBSuccessorlist = destBBOfBackEdge.getSuccessors();

					//for each successor of the dominator or while-loop head node, we will check if BB has it as its dominator or not.
					// If not, then that is else branch of the while-loop head node. But in case of switch-case, it creates problems.
					// It add supplementary edge only if has only one else branch, else it leaves it there as it is.
					//TODO 

					for(String successor: destBBSuccessorlist)
					{

						if(!dominatorsList.contains(successor)
								&& !predecessorList.contains(successor)) //if this is not the one we just got rid of via removing back edge.
						{
							// Now this successor is else branch.
							if(successorsList.size() == 0) // Just making sure, it has no where to go
							{

								additionalEdges.put(bb, successor); //we will remove these successors laters.

								//Because of following line, com.allstate.view apk produces warning
								// This adds successor and probably plays some role in dominator calculation of methods
								// For allstate app, it probably works on callGC something.

								successorsList.add(successor); //We don't need to add successor though. We only need predecessor info
								BasicBlock successorBB = getBasicBlockByKey(successor);
								successorBB.getPredecessors().add(currBBKey);

								//we also add this BB, as predecessor of 
							}
						}
					}
					//					}
					bb.setSuccessors(successorsList);

				}
			}
		}		
	}
	public void calculateFixedPointSoln()
	{

		/*		getGoal-BB@0x122 0x122 0x124[ NEXT = 0x122-0x124-getGoal-BB@0x124  ] [ PREV = ] 
		0x122 move-exception v1

		Basic blocks like above create problems when calculating dominator sets. Because their dominator set include themselves only.
		So whenever they become predecessor of someone, and we take intersection of predecessors, these basic blocks eat all other
		dominator sets. So, we need to remove such basic blocks from the code, may be.

		So, we are going to extract such BB for now and will add laters.
		 */			
		boolean changed = true;
		initDominatorSet();

		//Setting root at initially
		while(changed)
		{
			changed = false;

			for (int i=1; i < currBBCollection.size(); i++)
			{
				ArrayList<String> intersect = new ArrayList<String>();

				BasicBlock bb = (BasicBlock) currBBCollection.get(i);

				ArrayList<String> predList = bb.getPredecessors();

				logger.debug("BBKey:" + bb.getKey());
				if(predList.size() > 0)
				{
					for(int k=0; k < predList.size(); k++)
					{
						String keyStr = predList.get(k);
						BasicBlock bbb = (BasicBlock) getBasicBlockByKey(keyStr);

						if(bbb!= null)
						{
							// Over here, we add the additional check to get rid of the problem.
							// If bbb is not root and still have no predecessors, don't count it for intersection.

							if(!bbb.getBbPosition().equalsIgnoreCase("root")
									&& bbb.getDominators().size() < 2)
							{
								// do nothing
							}
							else
							{
								ArrayList<String> domSet = bbb.getDominators();

								if(k==0)
								{
									intersect.addAll(domSet);
								}
								else
								{
									intersect.retainAll(domSet);
								}
							}
						}
					}
				}

				if(!intersect.contains(bb.getKey()))
					intersect.add(bb.getKey());
				if(isDominatorSetChanged(bb.getDominators(), intersect))
				{
					changed = true;
				}

				bb.setDominators(intersect);

			}

		}

	}

	public void printPredSuccessors()
	{
		logger.debug("Successors List ");
		for (int i=0; i < currBBCollection.size(); i++)
		{
			BasicBlock bb = (BasicBlock) currBBCollection.get(i);

			ArrayList<String> predecessorList = bb.getPredecessors();
			logger.debug(bb.getKey() + " <Predecessors>:: " );
			for(int j=0; j< predecessorList.size(); j++)
			{
				logger.debug(" >> " + predecessorList.get(j));
			}

			ArrayList<String> successorsList = bb.getSuccessors();
			logger.debug(bb.getKey() + " <Successors>:: " );
			for(int j=0; j< successorsList.size(); j++)
			{
				logger.debug(" >> " + successorsList.get(j));
			}

			logger.debug(" ");
		}

	}
	public void printDominatorSets()
	{
		logger.debug("Dominators for each BB");
		for (int i=0; i < currBBCollection.size(); i++)
		{
			BasicBlock bb = (BasicBlock) currBBCollection.get(i);

			ArrayList<String> dominatorsList = bb.getDominators();

			logger.debug(bb.getKey() + " :: " );
			for(int j=0; j< dominatorsList.size(); j++)
			{
				logger.debug(" >> " + dominatorsList.get(j));
			}
			logger.debug(" ");
		}

	}
	public void printOrderedListOfBB()
	{

		logger.trace("\n\n\n\n");
		for (int i=0; i < currBBCollection.size(); i++)
		{
			BasicBlock bb = (BasicBlock) currBBCollection.get(i);
		}

	}


	public boolean isDominatorSetChanged(ArrayList<String> prev, ArrayList<String> curr)
	{
		boolean changed = false;

		if(prev.size() != curr.size())
		{
			return true;
		}
		for(int i=0; i < prev.size(); i++)
		{
			if(!curr.contains(prev.get(i)))
			{
				changed = true;
			}
		}

		return changed;
	}

	public ArrayList<String> clone(ArrayList<String> input)
	{
		ArrayList<String> cloneList = new ArrayList<String>();

		for(int i =0; i< input.size(); i++)
		{
			String item = input.get(i);
			cloneList.add(item);
		}

		return cloneList;
	}

	public void initDominatorSet()
	{
		for (int i=0; i < currBBCollection.size(); i++)
		{
			BasicBlock bb = (BasicBlock) currBBCollection.get(i);
			ArrayList<String> domSet = new ArrayList<String>();

			if(bb.getBbPosition().equalsIgnoreCase("root"))
			{
				domSet.add(bb.getKey());
				bb.setDominators(domSet);
			}
			else
			{
				domSet = getBBListSet();
				bb.setDominators(domSet);
			}
		}
	}
	public ArrayList<String> getBBListSet()
	{
		ArrayList<String> domSet =  new ArrayList<String>();

		for(int k=0; k < currBBCollection.size(); k++)
		{
			domSet.add(currBBCollection.get(k).getKey());
		}

		return domSet;
	}

	public BasicBlock getBasicBlockByKey(String key)
	{
		BasicBlock bb= null;

		if(this.currBBCollection != null && this.currBBCollection.size() > 0 )
		{
			//			System.out.println("key=>" + key );
			//			System.out.println("list size " + this.currBBList.size());
			for (int i=0; i < currBBCollection.size(); i++)
			{
				BasicBlock b =  (BasicBlock) currBBCollection.get(i);
				//				BasicBlock tmpBB = new BasicBlock();
				//				tmpBB = (BasicBlock) b;
				//				logger.fatal("bbKey --" + b.getKey() + ", key =  " + key);
				if(b != null &&  key != null)
				{
					if(key.equalsIgnoreCase(b.getKey()))
					{
						return b;
					}
				}
			}
		}

		return bb;
	}

	public BasicBlock getRoot()
	{
		BasicBlock root = null;

		for (CFGComponent b : this.currBBCollection)
		{
			BasicBlock tmpBB = new BasicBlock();
			tmpBB = (BasicBlock) b;

			if(tmpBB != null)
			{
				if(tmpBB.getBbPosition().equalsIgnoreCase("root"))
				{
					root = tmpBB;
				}
			}
		}
		return root;

	}

	@Override
	public String toString()
	{
		return currentCFG.getItem(currentPosition).toString();
	}

	@Override
	public void remove() {
		// TODO Auto-generated method stub

	}

}
