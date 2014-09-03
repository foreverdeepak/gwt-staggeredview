package net.foreverdeepak.gwt.sv.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class sview implements EntryPoint {

  public void onModuleLoad() {
	  ListView listView = new ListView();
		
		RootPanel.get("gwtdiv").add(listView.asWidget());
	  
  }
}
