package com.clashsoft.stocksim.ui.util;

import javafx.scene.control.TableCell;

public class PriceTableCell<S> extends TableCell<S, Long>
{
	@Override
	protected void updateItem(Long item, boolean empty)
	{
		if (item == null || empty)
		{
			this.setText(null);
			return;
		}

		this.setText(String.format("$ %,d,%02d", item / 100, item % 100));
	}
}
