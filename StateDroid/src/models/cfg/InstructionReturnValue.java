package models.cfg;


import models.symboltable.SymbolTableEntry;
import patternMatcher.events.Event;

public class InstructionReturnValue {

	private SymbolTableEntry returnEntry;
	private Event event;
	
	public InstructionReturnValue(SymbolTableEntry entry, Event event){
		this.returnEntry = entry;
		this.event = event;
	}

	public InstructionReturnValue(SymbolTableEntry entry){
		this.returnEntry = entry;
	}
	public SymbolTableEntry getReturnEntry() {
		return returnEntry;
	}
	public void setReturnEntry(SymbolTableEntry returnEntry) {
		this.returnEntry = returnEntry;
	}
	public Event getEvent() {
		return event;
	}
	public void setEvent(Event event) {
		this.event = event;
	}
	

}
