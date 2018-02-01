package com.clashsoft.stocksim.ui.table;

import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.layout.AnchorPane;

public class PriceTableCell<S> extends TableCell<S, Long>
{
	private final AnchorPane pane;
	private final Label      valueLabel;

	public PriceTableCell()
	{
		Label currencySignLabel = new Label("$");
		this.valueLabel = new Label();

		this.pane = new AnchorPane(currencySignLabel, this.valueLabel);
		AnchorPane.setLeftAnchor(currencySignLabel, 0.0);
		AnchorPane.setRightAnchor(this.valueLabel, 0.0);

		this.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
	}

	@Override
	protected void updateItem(Long price, boolean empty)
	{
		if (empty)
		{
			this.setGraphic(null);
		}
		else
		{
			final String value = String.format("%,d.%02d", price / 100, Math.abs(price % 100));
			this.valueLabel.setText(value);
			this.setGraphic(this.pane);
		}
	}
}
