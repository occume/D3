package org.d3.monitor.util;

import org.d3.monitor.warrior.Warrior.Event;

public interface EventListener {

	public void onEvent(Event event);
}
