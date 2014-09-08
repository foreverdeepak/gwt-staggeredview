package net.foreverdeepak.gwt.sv.client.pojo;

import com.google.gwt.core.client.JavaScriptObject;

public class Ad extends JavaScriptObject {
	
	protected Ad () {}
	
	public final native String getTitle ()/*-{ 
    	return this.title;
  	}-*/;
	
	public final native String getDescription ()/*-{ 
		return this.description;
	}-*/;
	
	public final native String getCoverImage ()/*-{ 
		return this.coverImage;
	}-*/;
	
	public final native int getId ()/*-{ 
		return this.id;
	}-*/;
	
	public final native int getShares ()/*-{ 
		return this.shares;
	}-*/;
	
	public final native int getEarns ()/*-{ 
		return this.earns;
	}-*/;
	
	public final native int getReviews ()/*-{ 
		return this.reviews;
	}-*/;
	
	public final native Company getCompany ()/*-{ 
		return this.company;
	}-*/;
	
	public final native int getImageCount ()/*-{ 
		return this.images.length;
	}-*/;
	
	public final native Img getImage (int index)/*-{ 
		return this.images[index];
	}-*/;
	
	public static class Company extends JavaScriptObject {
		
		protected Company () {}
		
		public final native String getName ()/*-{ 
	    	return this.name;
	  	}-*/;
		
		public final native String getDescription ()/*-{ 
			return this.description;
		}-*/;
		
		public final native int getId ()/*-{ 
			return this.id;
		}-*/;
		
		public final native int getImageCount ()/*-{ 
			return this.images.length;
		}-*/;
		
		public final native String getCoverImage ()/*-{ 
			return this.coverImage;
		}-*/;
		
		public final native Img getImage (int index)/*-{ 
			return this.images[index];
		}-*/;
	}
	
	public static class Img extends JavaScriptObject {
		
		protected Img () {}
		
		public final native String getName ()/*-{ 
	    	return this.name;
	  	}-*/;
		
		public final native String getImgType()/*-{ 
			return this.imgType;
		}-*/;
		
		public final native int getHeight()/*-{ 
			return this.height;
		}-*/;
		
		public final native int getWidth()/*-{ 
			return this.width;
		}-*/;
	}
}
