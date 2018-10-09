package patternMatcher.events.csm;

import patternMatcher.events.Event;

public class SqlLiteDatabaseInsertEvent extends Event{

	public Event createEvent(){
		return new SqlLiteDatabaseInsertEvent();
	}
}
