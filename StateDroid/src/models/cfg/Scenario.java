package models.cfg;

import java.util.ArrayList;
import java.util.List;

public class Scenario {

	private ArrayList<String> sequenceItems;

	public Scenario() {
		sequenceItems = new ArrayList<String>();
	}

	public ArrayList<String> getSequenceItems() {
		return sequenceItems;
	}

	public void setSequenceItems(ArrayList<String> sequenceItems) {
		this.sequenceItems = sequenceItems;
	}

}
