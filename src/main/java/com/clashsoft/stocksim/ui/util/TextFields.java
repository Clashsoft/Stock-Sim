package com.clashsoft.stocksim.ui.util;

import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.KeyCode;

public class TextFields
{
	public static void setupNumberField(TextField field)
	{
		final TextFormatter<Long> supplyFormatter = new TextFormatter<>(new DollarStringConverter(), 0L,
		                                                                new DollarFilter());
		field.setTextFormatter(supplyFormatter);
	}

	public static void setupDollarCentFields(TextField dollarField, TextField centField)
	{
		final TextFormatter<Long> priceDollarFormatter = new TextFormatter<>(new DollarStringConverter(), 0L,
		                                                                     new DollarFilter());
		dollarField.setTextFormatter(priceDollarFormatter);

		final TextFormatter<Long> priceCentFormatter = new TextFormatter<>(new CentStringConverter(), 0L,
		                                                                   new CentFilter());
		centField.setTextFormatter(priceCentFormatter);

		dollarField.setOnKeyPressed(evt -> {
			if (evt.getCode() == KeyCode.PERIOD || evt.getCode() == KeyCode.SEPARATOR)
			{
				centField.requestFocus();
			}
		});

		centField.setOnKeyPressed(evt -> {
			if (evt.getCode() == KeyCode.BACK_SPACE && centField.getText().isEmpty())
			{
				dollarField.requestFocus();
			}
		});
	}
}
