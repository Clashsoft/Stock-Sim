package com.clashsoft.stocksim.ui.util;

import com.clashsoft.stocksim.model.Stock;
import com.clashsoft.stocksim.ui.StockViewController;
import javafx.scene.control.TableCell;
import javafx.scene.input.MouseButton;

public class StockTableCell<T> extends TableCell<T, Stock>
{
	public StockTableCell()
	{
		this.setOnMouseClicked(evt -> {
			final Stock item = this.getItem();
			if (item != null && evt.getButton() == MouseButton.PRIMARY && evt.getClickCount() >= 2)
			{
				StockViewController.open(item);
			}
		});
	}

	@Override
	protected void updateItem(Stock item, boolean empty)
	{
		this.setItem(item);

		if (empty || item == null)
		{
			this.setText("");
			return;
		}

		this.setText('$' + item.getSymbol());
	}
}
