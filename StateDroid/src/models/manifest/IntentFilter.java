package models.manifest;

import java.util.ArrayList;

public class IntentFilter {

	private String priority = "";
	private ArrayList<String> actionList;
	private ArrayList<String> categoriesList;
	private ArrayList<String> dataList;

	public IntentFilter() {
		actionList = new ArrayList<String>();
		categoriesList = new ArrayList<String>();
		dataList = new ArrayList<String>();

	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public ArrayList<String> getActionList() {
		return actionList;
	}

	public void setActionList(ArrayList<String> actionList) {
		this.actionList = actionList;
	}

	public ArrayList<String> getCategoriesList() {
		return categoriesList;
	}

	public void setCategoriesList(ArrayList<String> categoriesList) {
		this.categoriesList = categoriesList;
	}

	public ArrayList<String> getDataList() {
		return dataList;
	}

	public void setDataList(ArrayList<String> dataList) {
		this.dataList = dataList;
	}

}
