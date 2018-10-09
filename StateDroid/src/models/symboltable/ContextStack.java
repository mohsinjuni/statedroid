package models.symboltable;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Stack;

public class ContextStack {

	private Stack entries = null;

	public ContextStack() {
		entries = new Stack();
	}

	public void saveContext(Context contextObj) {
		entries.push(contextObj);
	}

	public Context retrieveContext() {
		return (Context) entries.pop();
	}

	public Context retrieveContextByPeek() {
		Context currContext = null;

		if (entries != null && entries.size() > 0)
			currContext = (Context) entries.peek();
		return currContext;
	}

}
