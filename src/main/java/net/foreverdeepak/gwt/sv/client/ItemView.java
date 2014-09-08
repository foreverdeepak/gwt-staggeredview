package net.foreverdeepak.gwt.sv.client;

import net.foreverdeepak.gwt.sv.client.pojo.Ad;
import net.foreverdeepak.gwt.sv.client.pojo.Ad.Img;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class ItemView extends Composite {

	private static ItemViewUiBinder uiBinder = GWT.create(ItemViewUiBinder.class);

	interface ItemViewUiBinder extends UiBinder<Widget, ItemView> {
	}
	
	@UiField AnchorElement adDetailUrl;
	@UiField ImageElement coverImage;
	@UiField SpanElement title;
	@UiField SpanElement description;
	@UiField SpanElement companyTitle;
	@UiField Element section;
	@UiField DivElement itemDiv;
	@UiField DivElement content;
	
	private int imageHeight;
	
	int index;

	public ItemView(Ad ad, int index) {
		initWidget(uiBinder.createAndBindUi(this));
		
		this.index = index;
	
		section.getStyle().setWidth(250, Unit.PX);
		adDetailUrl.setHref("/ad/" + ad.getId());
		title.setInnerText(ad.getTitle());
		description.setInnerText(ad.getDescription());
		companyTitle.setInnerText(ad.getCompany().getName());
		
		Img cImg = null;
		for (int i = 0; i < ad.getImageCount(); i++) {
			Img img = ad.getImage(i);
			if(img.getName().equalsIgnoreCase(ad.getCoverImage())) {
				cImg = img;
			}
		}
		
		if(cImg != null) {
			coverImage.setSrc("http://www.madpiggy.com:5052/ad/" + ad.getId() + "/" +cImg.getName());
			coverImage.setWidth(250);
			imageHeight = getCalulatedHeight(cImg);
			coverImage.setHeight(imageHeight);
		}
	}
	
	private int getCalulatedHeight(Img img) {
		return (int) ((double)img.getHeight()/(img.getWidth()*1.00)*300);
	}
	
	public void setTop(int value, Unit unit) {
		itemDiv.getStyle().setTop(value, unit);
	}
	
	public void setLeft(int value, Unit unit) {
		itemDiv.getStyle().setLeft(value, unit);
	}
	
	@Override
	protected void onLoad() {
		int height = content.getClientHeight() + imageHeight;
		GwtTest.eventBus.fireEvent(new ItemLoadedEvent(new ItemLoadedEvent.ColumnHeight(index, height)));
		super.onLoad();
	}
}
