package net.foreverdeepak.gwt.sv.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.jsonp.client.JsonpRequestBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ScrollEvent;
import com.google.gwt.user.client.Window.ScrollHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.mgwt.dom.client.event.orientation.OrientationChangeEvent;
import com.googlecode.mgwt.dom.client.event.orientation.OrientationChangeHandler;
import com.googlecode.mgwt.ui.client.MGWT;

public class ListView extends Composite implements ScrollHandler, ColumnHeightEventHandler, ResizeHandler {

	private static ListViewUiBinder uiBinder = GWT.create(ListViewUiBinder.class);

	interface ListViewUiBinder extends UiBinder<Widget, ListView> {
	}

	@UiField FlowPanel container;
	@UiField FlowPanel flowPanel;
	
	Timer resizeTimer;
	
	Map<Integer, Integer> columnHeightMap = new HashMap<Integer, Integer>();
	ColumnHeight lowestColumnHeight = new ColumnHeight(0, 0);
	
	int currentAdIndex = 0;
	private boolean loading = false;
	
	List<Ad> cachedAds = new ArrayList<Ad>();
	List<Ad> recentAds = new ArrayList<Ad>();
	
	DeviceProperties props = new DeviceProperties();
	
	public ListView() {
		initWidget(uiBinder.createAndBindUi(this));
		
		SViewEntryPoint.eventBus.addHandler(ColumnHeightUpdatedEvent.TYPE, this);
		
		Window.addWindowScrollHandler(this);
		
		resizeTimer = new Timer() {
			@Override
			public void run() {
				resize();
			}
		};

		Window.addResizeHandler(this);
		
		MGWT.addOrientationChangeHandler(new OrientationChangeHandler() {

			@Override
			public void onOrientationChanged(OrientationChangeEvent event) {
				switch (event.getOrientation()) {
				case LANDSCAPE:
					return;
				case PORTRAIT:
					return;
				}
			}
		});

		
		load();
		sendRequest();
	}
	
	private void load() {
		props = new DeviceProperties();
		props.calculateDeviceProperties();
		columnHeightMap.clear();
		for (int i = 0; i < props.columnCount; i++) {
			columnHeightMap.put(i, 0);
		}
		setContainerStyle();
	}
	
	private void resize() {
		DeviceProperties deviceProperties = new DeviceProperties();
		deviceProperties.calculateDeviceProperties();
		if(deviceProperties.columnCount != props.columnCount) {
			flowPanel.clear();
			recentAds.clear();
			deviceProperties = new DeviceProperties();
			deviceProperties.calculateDeviceProperties();
			recentAds.addAll(cachedAds);
			load();
			currentAdIndex = 0;
			lowestColumnHeight = new ColumnHeight(0,0);
			lowestColumnHeight.setNewLoad(true);
			SViewEntryPoint.eventBus.fireEvent(new ColumnHeightUpdatedEvent(lowestColumnHeight));
		}
	}
	
	private void setContainerStyle() {
		Style style = container.getElement().getStyle();
		style.setDisplay(Display.BLOCK);
		style.setProperty("margin", "auto");
		style.setLeft(0, Unit.EM);
		style.setRight(0, Unit.EM);
		style.setTop(0, Unit.EM);
		style.setBottom(0, Unit.EM);
		style.setPaddingTop(1, Unit.EM);
		style.setWidth(props.containerWidth, Unit.PX);
	}
	
	public void addItem(Ad item) {
		Map.Entry<Integer, Integer> lowestEntry = getLowestHeight();
		int columnIndex = lowestEntry.getKey();
		
		int height = lowestEntry.getValue();
		
		int top = height;
		int left = columnIndex*(props.itemWidth + props.itemMargin);
		
		ItemView view = new ItemView(item, columnIndex, props.itemWidth);
		
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
	
	public static native int getScreenWidth() /*-{
		return screen.width;
	}-*/;
	
	public static native int getScreenHeight() /*-{
		return screen.height;
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
				ListView.this.recentAds.clear();
				for (int i = 0; i < result.length(); i++) {
					ListView.this.recentAds.add(result.get(i));
					ListView.this.cachedAds.add(result.get(i));
				}
				currentAdIndex = 0;
				
				ColumnHeight columnHeight = new ColumnHeight(lowestColumnHeight.index,lowestColumnHeight.height);
				columnHeight.setNewLoad(true);
				SViewEntryPoint.eventBus.fireEvent(new ColumnHeightUpdatedEvent(columnHeight));
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
			this.columnHeightMap.put(columnHeight.getIndex(), columnHeight.getHeight() +last + props.itemMargin);
		}
		
		if(currentAdIndex < recentAds.size()) {
			addItem(recentAds.get(currentAdIndex++));
		}
	}

	@Override
	public void onResize(ResizeEvent event) {
		resizeTimer.cancel();
	    resizeTimer.schedule(250);
	}
	
	public static class DeviceProperties {
		int screenWidth;
		int windowWidth;
		int screenHeight;
		int windowHeight;
		int columnCount;
		int itemMargin;
		int containerWidth;
		int itemWidth;
		
		public void calculateDeviceProperties() {
			screenWidth = getScreenWidth();
			windowWidth = Window.getClientWidth();
			if(screenWidth == 0 || screenWidth < windowWidth) {
				screenWidth = windowWidth;
			}
			
			screenHeight = getScreenHeight();
			windowHeight = Window.getClientHeight();
			if(screenHeight == 0 || screenHeight < windowHeight) {
				screenHeight = windowHeight;
			}
			
			if(screenWidth <= 400) {
				columnCount = 2;
			} else if(screenWidth > 400 && screenWidth <= 800 ) {
				columnCount = 3;
			} else if(screenWidth > 800 && screenWidth <= 1000 ) {
				columnCount = 4;
			} else {
				columnCount = 5;
			}
			
			itemMargin = screenWidth/100;
			if(itemMargin < 10) {
				itemMargin = 10;
				itemWidth = (screenWidth / columnCount) - 2*itemMargin;
			} else {
				itemWidth = (screenWidth / columnCount) - 3*itemMargin;
			}
			
			columnCount = windowWidth/itemWidth; 
			
			containerWidth = ((itemWidth+itemMargin)*columnCount) - itemMargin;
		}
	}
}
