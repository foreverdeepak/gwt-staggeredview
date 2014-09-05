package net.foreverdeepak.gwt.sv.client;

import java.util.ArrayList;
import java.util.List;

import net.foreverdeepak.gwt.sv.client.pojo.Ad;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.jsonp.client.JsonpRequestBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ScrollEvent;
import com.google.gwt.user.client.Window.ScrollHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class ListView extends Composite implements ResizeHandler, ScrollHandler {

	private static ListViewUiBinder uiBinder = GWT.create(ListViewUiBinder.class);

	interface ListViewUiBinder extends UiBinder<Widget, ListView> {
	}

	@UiField TableRowElement mrow;
	
	int width = 0;
	int tdCount = 0;
	Unit unit = Unit.PCT;
	int scrollTop = 0;
	
	Timer resizeTimer;
	RequestBuilder requestBuilder;
	
	ArrayList<TableCellElement> tds = new ArrayList<TableCellElement>();
	
	boolean loading = false;
	
	List<Ad> cachedAds = new ArrayList<Ad>();
	
	public ListView() {
		initWidget(uiBinder.createAndBindUi(this));

		resizeTimer = new Timer() {
			@Override
			public void run() {
				reload();
			}
		};

		Window.addResizeHandler(this);
		Window.addWindowScrollHandler(this);

		load();
		sendRequest();
	}
	
	private void reload() {
		if(getColumnColunt() != tdCount) {
			mrow.removeAllChildren();
			tds.clear();
			load();
			renderAds(cachedAds);
		}
	}
	
	private void load () {
		tdCount = getColumnColunt();
		width = 100/tdCount;
		createTds();
	}
	
	private int getColumnColunt() {
		int count = 0;
		int deviceWidth = Window.getClientWidth();

		if (deviceWidth <= 500) {
			count = 2;
		} else if (deviceWidth > 500 && deviceWidth <= 800) {
			count = 3;
		} else if (deviceWidth > 800 && deviceWidth <= 1000) {
			count = 4; 
		} else {
			count = 5;
		}
		return count;
	}
	
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
				loading = false;
				renderAds(result);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				loading = false;
			}
		});
		
	}
	
	private void renderAds(List<Ad> ads) {
		int tdIndex = 0;
		for (Ad ad : ads) {
			if(tdIndex == tdCount) {
				tdIndex = 0;
			}
			TableCellElement td = tds.get(tdIndex);
			AdItemView itemView = new AdItemView(ad, width, unit);
			td.appendChild(itemView.getElement());
			tdIndex++;
		}
		
		if(cachedAds.size() > ads.size()) {
			//Window.scrollTo(0, scrollTop+Window.getClientHeight());
		}
	}
	
	private void renderAds(JsArray<Ad> adArray) {
		List<Ad> adList = new ArrayList<Ad>();
		for (int i = 0; i < adArray.length(); i++) {
			adList.add(adArray.get(i));
		}
		cachedAds.addAll(adList);
		renderAds(adList);
	}
	
	private void createTds() {
		for (int i = 0; i < tdCount; i++) {
			TableCellElement td = createTd(width, unit);
			tds.add(td);
			mrow.appendChild(td);
		}
	}

	private TableCellElement createTd(int width, Unit unit) {
		TableCellElement td = Document.get().createTDElement();
		td.setAttribute("style", "vertical-align: top;");
		td.setAttribute("width", width + unit.getType());
		return td;
	}

	@Override
	public void onResize(ResizeEvent event) {
	    resizeTimer.cancel();
	    resizeTimer.schedule(250);
	}
	
	public static native String getDeviceDataUsingNative() /*-{
		return " " + screen.pixelDepth + "  " + window.devicePixelRatio + " " + window.innerWidth + " " + window.outerWidth;
	}-*/;
	
	@Override
	public void onWindowScroll(ScrollEvent event) {
		int scrollHeight = Document.get().getScrollHeight();
		int windowHeight = Window.getClientHeight();
		int scrollTop = Document.get().getScrollTop() + windowHeight;
		if(scrollTop - scrollHeight >= 0) {
			sendRequest();
		}
	}
}
