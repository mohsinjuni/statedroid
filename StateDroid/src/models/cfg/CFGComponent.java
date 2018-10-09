package models.cfg;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import analyzer.Analyzer;



public class CFGComponent {

	protected String key="";
	protected String text="";

	protected String currMethodName="";
	protected String currClassName="";
	protected String currPkgName="";
	protected String currPkgClassName="";
	protected String currClassType="";
	private String currBBKey="";
	private boolean isAnalyzedAtLeaseOnce= false;
	
	
	protected ArrayList<CFGComponent> compCollection;

	protected static Logger logger;
	
	
	public void accept(Analyzer a){}

	public java.util.Iterator iterator(){return null;}

	public void addItem(CFGComponent c){}
	public boolean removeItem(CFGComponent c){ return false;}
	public void setItem(int index, CFGComponent comp){}
	public CFGComponent getItem(int index){ return null;	}
	

	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getCurrMethodName() {
		return currMethodName;
	}

	public void setCurrMethodName(String currMethodName) {
		this.currMethodName = currMethodName;
	}


	public String getCurrClassName() {
		return currClassName;
	}


	public void setCurrClassName(String currClassName) {
		this.currClassName = currClassName;
	}


	public String getCurrPkgName() {
		return currPkgName;
	}


	public void setCurrPkgName(String currPkgName) {
		this.currPkgName = currPkgName;
	}
	public String getCurrPkgClassName() {
		return currPkgClassName;
	}


	public void setCurrPkgClassName(String currPkgClassName) {
		this.currPkgClassName = currPkgClassName;
	}
	
	public ArrayList<CFGComponent> getCompCollection() {
		return compCollection;
	}

	public void setCompCollection(ArrayList<CFGComponent> compCollection) {
		this.compCollection = compCollection;
	}
	
	public String getCurrClassType() {
		return currClassType;
	}

	public void setCurrClassType(String currClassType) {
		this.currClassType = currClassType;
	}

	public String getCurrBBKey() {
		return currBBKey;
	}

	public void setCurrBBKey(String currBBKey) {
		this.currBBKey = currBBKey;
	}

	public boolean isAnalyzedAtLeaseOnce() {
		return isAnalyzedAtLeaseOnce;
	}

	public void setAnalyzedAtLeaseOnce(boolean isAnalyzedAtLeaseOnce) {
		this.isAnalyzedAtLeaseOnce = isAnalyzedAtLeaseOnce;
	}



}
