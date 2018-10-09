package patternMatcher.events.csm.intent;

import patternMatcher.events.Event;

public class IntentActionDefinedEvent extends Event{

	/*
	 *		 0xa new-instance v0, Landroid/content/Intent;
			0xe const-string v1, 'android.intent.action.CALL'
			0x12 invoke-direct v0, v1, Landroid/content/Intent;-><init>(Ljava/lang/String;)V
	 * 
	 * (non-Javadoc)
	 * @see patternMatcher.events.Event#createEvent()
	 */
	public Event createEvent()
	{
		
		return new IntentActionDefinedEvent();
	}
}
