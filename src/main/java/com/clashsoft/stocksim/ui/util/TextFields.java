package com.clashsoft.stocksim.ui.util;

import javafx.scene.control.Label;
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

	public static void displayAbsChange(long amount, Label dollarLabel, Label centLabel)
	{
		char sign;
		if (amount < 0)
		{
			amount = -amount;
			sign = '-';
		}
		else
		{
			sign = '+';
		}

		long dollars = amount / 100;
		long cents = Math.abs(amount % 100);
		dollarLabel.setText(String.format("%c $ %,d", sign, dollars));
		centLabel.setText(String.format(".%02d", cents));
	}

	public static void displayRelChange(double amount, Label label)
	{
		char sign;
		if (amount < 0)
		{
			amount = -amount;
			sign = '-';
		}
		else
		{
			sign = '+';
		}

		label.setText(String.format("%c %.2f %%", sign, amount * 100));
	}

	public static void displayNetWorth(long amount, Label dollarLabel, Label centLabel)
	{
		long dollars = amount / 100;
		long cents = Math.abs(amount % 100);

		dollarLabel.setText(String.format("$ %,d", dollars));
		centLabel.setText(String.format(".%02d", cents));
	}
}
