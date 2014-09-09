package net.foreverdeepak.gwt.sv.client;

import java.util.HashMap;
import java.util.Map;

import net.foreverdeepak.gwt.sv.client.ColumnHeightUpdatedEvent.ColumnHeight;
import net.foreverdeepak.gwt.sv.client.ColumnHeightUpdatedEvent.ColumnHeightEventHandler;
import net.foreverdeepak.gwt.sv.client.pojo.Ad;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.jsonp.client.JsonpRequestBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ScrollEvent;
import com.google.gwt.user.client.Window.ScrollHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

public class ListView extends Composite implements ScrollHandler, ColumnHeightEventHandler {

	private static ListViewUiBinder uiBinder = GWT.create(ListViewUiBinder.class);

	interface ListViewUiBinder extends UiBinder<Widget, ListView> {
	}

	@UiField FlowPanel container;
	@UiField FlowPanel flowPanel;
	
	Map<Integer, Integer> columnHeightMap = new HashMap<Integer, Integer>();
	ColumnHeight lowestColumnHeight = new ColumnHeight(0, 0);
	int columnCount = 0;
	
	JsArray<Ad> ads = null;
	int currentAdIndex = 0;
	
	private boolean loading = false;

	public ListView() {
		initWidget(uiBinder.createAndBindUi(this));
		setContainerStyle();
		
		GwtTest.eventBus.addHandler(ColumnHeightUpdatedEvent.TYPE, this);
		Window.addWindowScrollHandler(this);
		
		columnCount = 5;
		for (int i = 0; i < columnCount; i++) {
			columnHeightMap.put(i, 0);
		}
		sendRequest();
	}
	
	private void setContainerStyle() {
		Style style = container.getElement().getStyle();
		style.setDisplay(Display.BLOCK);
		style.setProperty("margin", "auto");
		style.setLeft(0, Unit.EM);
		style.setRight(0, Unit.EM);
		style.setTop(0, Unit.EM);
		style.setBottom(0, Unit.EM);
		style.setPaddingTop(2, Unit.EM);
		style.setWidth(1310, Unit.PX);
	}
	
	public void addItem(Ad item) {
		Map.Entry<Integer, Integer> lowestEntry = getLowestHeight();
		int columnIndex = lowestEntry.getKey();
		
		int height = lowestEntry.getValue();
		
		int top = height;
		int left = columnIndex*265;
		
		ItemView view = new ItemView(item, columnIndex);
		view.getElement().getStyle();
		
		view.setTop(top, Unit.PX);
		view.setLeft(left, Unit.PX);
		
		flowPanel.add(view);
	}
	
	private Map.Entry<Integer, Integer> getLowestHeight() {
		Map.Entry<Integer, Integer> lowest = columnHeightMap.entrySet().iterator().next();
		for (Map.Entry<Integer, Integer> entry : columnHeightMap.entrySet()) {
			if(entry.getValue() < lowest.getValue()) {
				lowest = entry;
			}
		}
		return lowest;
	}
	
	public void setColumnHeight(int index, int height) {
		int last = this.columnHeightMap.get(index);
		this.columnHeightMap.put(index, height+last);
	}
	
	public static native int getScreenWidth() /*-{
		return screen.width;
	}-*/;
	
	private void sendRequest() {
		if(loading) {
			return;
		}
		loading = true;
		
		JsonpRequestBuilder builder = new JsonpRequestBuilder();
		builder.setCallbackParam("_jsonp");
		builder.requestObject("http://www.madpiggy.com:5051/ws/ad.jsonp", new AsyncCallback<JsArray<Ad>>() {
			
			@Override
			public void onSuccess(JsArray<Ad> result) {
				ListView.this.ads = result;
				currentAdIndex = 0;
				
				ColumnHeight columnHeight = new ColumnHeight(lowestColumnHeight.index,lowestColumnHeight.height);
				columnHeight.setNewLoad(true);
				GwtTest.eventBus.fireEvent(new ColumnHeightUpdatedEvent(columnHeight));
				loading = false;
			}
			
			@Override
			public void onFailure(Throwable caught) {
				loading = false;
			}
		});
	}
	
	@Override
	public void onWindowScroll(ScrollEvent event) {
		if(loading) return;
		int windowHeight = Window.getClientHeight();
		int scrollTop = Document.get().getScrollTop() + windowHeight;
		int height = getLowestHeight().getValue();
		
		int diff = Math.abs(scrollTop - height);
		
		if(diff >=0 && diff<=200) {
			sendRequest();
		}
	}

	@Override
	public void onUpdate(ColumnHeight columnHeight) {
		int last = this.columnHeightMap.get(columnHeight.getIndex());
		if(!columnHeight.isNewLoad()) {
			if(columnHeight.getHeight() < lowestColumnHeight.getHeight()) {
				lowestColumnHeight = columnHeight;
			}
			this.columnHeightMap.put(columnHeight.getIndex(), columnHeight.getHeight() +last + 15);
		} else {
			//this.columnHeightMap.put(lowestColumnHeight.getIndex(), lowestColumnHeight.getHeight() + 20);
		}
		
		
		if(currentAdIndex < ads.length()) {
			addItem(ads.get(currentAdIndex++));
		}
	}
}
