package utility;

import java.util.ArrayList;

import models.cfg.CFGComponent;
import models.cfg.Scenario;


public class MethodCallSequenceGenerator {

	
	private ArrayList<Scenario> sequenceList;
	
	public MethodCallSequenceGenerator() 
	{
		setSequenceList(new ArrayList<Scenario>());
	}
	
	public void generateSequence()
	{
		Scenario seq1 = new Scenario();
		seq1.getSequenceItems().add("onCreate");
		seq1.getSequenceItems().add("onStart");
		seq1.getSequenceItems().add("onPostCreate");
		seq1.getSequenceItems().add("onResume");
		seq1.getSequenceItems().add("onPostResume");
		
		Scenario seq2 = new Scenario();
		seq2.getSequenceItems().add("onPause");
		seq2.getSequenceItems().add("onStop");
		seq2.getSequenceItems().add("onDestroy");
		seq2.getSequenceItems().add("onCreate");
		seq2.getSequenceItems().add("onStart");
		seq2.getSequenceItems().add("onPostCreate");
		seq2.getSequenceItems().add("onResume");
		seq2.getSequenceItems().add("onPostResume");

		Scenario seq3 = new Scenario();
		seq3.getSequenceItems().add("onPause");
		seq3.getSequenceItems().add("onSaveInstanceState");
		seq3.getSequenceItems().add("onStop");
		seq3.getSequenceItems().add("onDestroy");
		seq3.getSequenceItems().add("onCreate");
		seq3.getSequenceItems().add("onStart");
		seq3.getSequenceItems().add("onRestoreInstanceState");
		seq3.getSequenceItems().add("onPostCreate");
		seq3.getSequenceItems().add("onResume");
		seq3.getSequenceItems().add("onPostResume");

		Scenario seq4 = new Scenario();
		seq4.getSequenceItems().add("onUserLeaveHint");
		seq4.getSequenceItems().add("onPause");
		seq4.getSequenceItems().add("onSaveInstanceState");
		seq4.getSequenceItems().add("onResume");
		seq4.getSequenceItems().add("onPostResume");

		Scenario seq5 = new Scenario();
		seq5.getSequenceItems().add("onUserLeaveHint");
		seq5.getSequenceItems().add("onPause");
		seq5.getSequenceItems().add("onSaveInstanceState");
		seq5.getSequenceItems().add("onStop");
		seq5.getSequenceItems().add("onRestart");
		seq5.getSequenceItems().add("onStart");
		seq5.getSequenceItems().add("onResume");
		seq5.getSequenceItems().add("onPostResume");
		
		sequenceList.add(seq1);
		sequenceList.add(seq2);
		sequenceList.add(seq3);
		sequenceList.add(seq4);
		sequenceList.add(seq5);
		
	}
	
	public ArrayList<CFGComponent> buildSequenceList(ArrayList<CFGComponent> paramCompList)
	{
		ArrayList<CFGComponent> mthdItrList = new ArrayList<CFGComponent>();
		
		
		
		
		
		
		return mthdItrList;
	}

	public ArrayList<Scenario> getSequenceList() {
		return sequenceList;
	}

	public void setSequenceList(ArrayList<Scenario> sequenceList) {
		this.sequenceList = sequenceList;
	}
	
}
