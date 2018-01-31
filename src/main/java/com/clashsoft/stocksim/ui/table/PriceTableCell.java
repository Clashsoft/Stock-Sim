package com.clashsoft.stocksim.ui.table;

import javafx.scene.control.TableCell;

import java.util.Locale;

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

		this.setText(String.format(Locale.ENGLISH, "$ %,d.%02d", item / 100, Math.abs(item % 100)));
	}
}
