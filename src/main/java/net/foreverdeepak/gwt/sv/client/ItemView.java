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
	@UiField DivElement imageDiv;
	
	int imageHeight = 0;
	int index = 0;
	int width = 0;

	public ItemView(Ad ad, int index, int width) {
		initWidget(uiBinder.createAndBindUi(this));
		
		this.index = index;
		this.width = width;
		this.imageHeight = width;
	
		section.getStyle().setWidth(width, Unit.PX);
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
		
		imageDiv.getStyle().setWidth(width, Unit.PX);
		
		if(cImg != null) {
			coverImage.setSrc("http://www.madpiggy.com:5052/ad/" + ad.getId() + "/" +cImg.getName());
			imageHeight = getCalulatedHeight(cImg);
		}
		imageDiv.getStyle().setHeight(imageHeight, Unit.PX);
	}
	
	private int getCalulatedHeight(Img img) {
		return (int) ((double)img.getHeight()/(img.getWidth()*1.00)*width);
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
		SViewEntryPoint.eventBus.fireEvent(new ColumnHeightUpdatedEvent(new ColumnHeightUpdatedEvent.ColumnHeight(index, height)));
		super.onLoad();
	}
}
