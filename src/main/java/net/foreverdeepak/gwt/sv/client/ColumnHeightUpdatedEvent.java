package net.foreverdeepak.gwt.sv.client;

import net.foreverdeepak.gwt.sv.client.ColumnHeightUpdatedEvent.ColumnHeightEventHandler;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public class ColumnHeightUpdatedEvent extends GwtEvent<ColumnHeightEventHandler> {
	
    public static Type<ColumnHeightEventHandler> TYPE = new Type<ColumnHeightEventHandler>();

    private ColumnHeight columnHeight;
    
	public ColumnHeightUpdatedEvent(ColumnHeight columnHeight) {
		this.columnHeight = columnHeight;
	}

	public static interface ColumnHeightEventHandler extends EventHandler {
		void onUpdate(ColumnHeight columnHeight);
	}

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<ColumnHeightEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ColumnHeightEventHandler handler) {
		handler.onUpdate(columnHeight);
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
