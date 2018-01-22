package com.clashsoft.stocksim.ui.util;

import javafx.scene.control.TextFormatter;

import java.util.function.UnaryOperator;

public class DollarFilter implements UnaryOperator<TextFormatter.Change>
{
	@Override
	public TextFormatter.Change apply(TextFormatter.Change change)
	{
		return change.getText().matches("[\\d,]*") ? change : null;
	}
}
