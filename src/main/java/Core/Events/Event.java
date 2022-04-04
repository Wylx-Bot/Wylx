package Core.Events;

import Core.Events.Commands.ServerCommand;

import java.util.HashMap;

public abstract class Event {
	public abstract String getKeyword();

	public abstract String[] getAliases();

	public abstract String getDescription(String alias);

	public HashMap<String, Event> getEventMap(){
		HashMap<String, Event> myMap = new HashMap<>();
		myMap.put(getKeyword(), this);
		for(String alias : getAliases()){
			myMap.put(alias, this);
		}
		return myMap;
	}
}
