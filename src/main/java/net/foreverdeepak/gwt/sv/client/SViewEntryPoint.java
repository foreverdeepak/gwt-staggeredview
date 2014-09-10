package net.foreverdeepak.gwt.sv.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.ui.RootPanel;

public class SViewEntryPoint implements EntryPoint {
	
	public static EventBus eventBus = null;

	@Override
	public void onModuleLoad() {
		eventBus  = GWT.create(SimpleEventBus.class);
		ListView listView = new ListView();

		RootPanel.get("gwtdiv").add(listView);
	}

}
