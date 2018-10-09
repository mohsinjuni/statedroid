package patternMatcher.events;

import java.util.HashMap;

public class EventFactory {

	private HashMap<String, Event> registeredEvents;
	private static EventFactory instance;

	private EventFactory() {
		registeredEvents = new HashMap<String, Event>();
	}

	public static EventFactory getInstance() {
		if (instance == null) {
			synchronized (EventFactory.class) {
				if (instance == null)
					instance = new EventFactory();
			}
		}
		return instance;
	}

	public void registerEvent(String eventID, Event event) {
		if (eventID != null && event != null)
			if (!registeredEvents.containsKey(eventID))
				registeredEvents.put(eventID, event);
	}

	public Event createEvent(String eventID) {
		return ((Event) registeredEvents.get(eventID).createEvent());
	}

}
