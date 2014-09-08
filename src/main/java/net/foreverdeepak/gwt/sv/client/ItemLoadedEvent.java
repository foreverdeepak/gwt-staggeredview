package net.foreverdeepak.gwt.sv.client;

import net.foreverdeepak.gwt.sv.client.ItemLoadedEvent.ItemLoadedHandler;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public class ItemLoadedEvent extends GwtEvent<ItemLoadedHandler> {
	
    public static Type<ItemLoadedHandler> TYPE = new Type<ItemLoadedHandler>();

    private ColumnHeight columnHeight;
    
	public ItemLoadedEvent(ColumnHeight columnHeight) {
		this.columnHeight = columnHeight;
	}

	public static interface ItemLoadedHandler extends EventHandler {
		void onItemLoad(ColumnHeight columnHeight);
	}

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<ItemLoadedHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ItemLoadedHandler handler) {
		handler.onItemLoad(columnHeight);
	}

	public static class ColumnHeight {
		int index;
		int height;
		boolean newLoad;

		public ColumnHeight(int index, int height) {
			this.index = index;
			this.height = height;
		}

		public int getHeight() {
			return height;
		}

		public int getIndex() {
			return index;
		}

		public boolean isNewLoad() {
			return newLoad;
		}

		public void setNewLoad(boolean newLoad) {
			this.newLoad = newLoad;
		}

	}

}
