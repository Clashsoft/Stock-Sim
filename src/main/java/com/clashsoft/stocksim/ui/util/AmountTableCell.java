package com.clashsoft.stocksim.ui.util;

import javafx.scene.control.TableCell;

import java.util.Locale;

public class AmountTableCell<S> extends TableCell<S, Long>
{
	@Override
	protected void updateItem(Long item, boolean empty)
	{
		if (item == null || empty)
		{
			this.setText(null);
			return;
		}

		this.setText(String.format(Locale.ENGLISH, "%,d", item));
	}
}
