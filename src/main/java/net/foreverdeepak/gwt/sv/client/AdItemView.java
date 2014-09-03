package net.foreverdeepak.gwt.sv.client;

import net.foreverdeepak.gwt.sv.client.pojo.Ad;
import net.foreverdeepak.gwt.sv.client.pojo.Ad.Img;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class AdItemView extends Composite {

	private static ItemViewUiBinder uiBinder = GWT.create(ItemViewUiBinder.class);

	interface ItemViewUiBinder extends UiBinder<Widget, AdItemView> {
	}
	
	@UiField AnchorElement adDetailUrl;
	@UiField ImageElement coverImage;
	@UiField SpanElement title;
	@UiField SpanElement description;
	@UiField SpanElement companyTitle;

	public AdItemView(Ad ad, int width, Unit unit) {
		initWidget(uiBinder.createAndBindUi(this));
		
		coverImage.setAttribute("width", "100%");
		
		adDetailUrl.setHref("/ad/" + ad.getId());
		title.setInnerText(ad.getTitle());
		description.setInnerText(ad.getDescription());
		companyTitle.setInnerText(ad.getCompany().getName());
		
		for (int i = 0; i < ad.getImageCount(); i++) {
			Img img = ad.getImage(i);
			if(img.getName().equalsIgnoreCase(ad.getCoverImage())) {
				coverImage.setSrc("http://www.madpiggy.com:5052/ad/" + ad.getId() + "/" +img.getName());
			}
		}
	}
}
