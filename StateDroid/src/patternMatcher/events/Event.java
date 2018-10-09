package patternMatcher.events;

import java.util.ArrayList;
import java.util.Hashtable;

import models.manifest.ComponentManifest;

//Base event-template class. Child classes are created so that when events are passed to all state machines, only interested state-nmachines will
// receive specific events. ====> Side-effects of using design patterns, may be. Hopefully, I am not abusing design patterns. LOL
public abstract class Event {

	private String ID="";
	private String name="";
	private String currMethodName="";
	private String currPkgClsName="";
	private String currCompCallbackMethodName = "";
	private String currComponentName= "";
	private String currComponentPkgName= "";
	private String currComponentType="";
	
	private Hashtable<String, Object> eventInfo = new Hashtable<String, Object>();
	
	public Event createEvent(){ return null;}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCurrMethodName() {
		return currMethodName;
	}
	public void setCurrMethodName(String currMethodName) {
		this.currMethodName = currMethodName;
	}
	public String getCurrPkgClsName() {
		return currPkgClsName;
	}
	public void setCurrPkgClsName(String currPkgClsName) {
		this.currPkgClsName = currPkgClsName;
	}
	public String getID() {
		return ID;
	}
	public void setID(String iD) {
		ID = iD;
	}

	public Hashtable<String, Object> getEventInfo() {
		return eventInfo;
	}

	public void setEventInfo(Hashtable<String, Object> eventInfo) {
		this.eventInfo = eventInfo;
	}

	public String getCurrComponentName() {
		return currComponentName;
	}

	public void setCurrComponentName(String currComponentName) {
		this.currComponentName = currComponentName;
	}

	public String getCurrCompCallbackMethodName() {
		return currCompCallbackMethodName;
	}

	public void setCurrCompCallbackMethodName(String currCompCallbackMethodName) {
		this.currCompCallbackMethodName = currCompCallbackMethodName;
	}

	public String getCurrComponentPkgName() {
		return currComponentPkgName;
	}

	public void setCurrComponentPkgName(String currComponentPkgName) {
		this.currComponentPkgName = currComponentPkgName;
	}

	public String getCurrComponentType() {
		return currComponentType;
	}

	public void setCurrComponentType(String currComponentType) {
		this.currComponentType = currComponentType;
	}

	
}
