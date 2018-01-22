package com.clashsoft.stocksim.ui.util;

import javafx.scene.control.TextFormatter;

import java.util.function.UnaryOperator;

public class CentFilter implements UnaryOperator<TextFormatter.Change>
{
	@Override
	public TextFormatter.Change apply(TextFormatter.Change change)
	{
		return matches(change.getControlNewText()) ? change : null;
	}

	private static boolean matches(String text)
	{
		final int length = text.length();
		if (length > 2)
		{
			return false;
		}

		for (int i = 0; i < length; i++)
		{
			final char c = text.charAt(i);
			if (c < '0' || c > '9')
			{
				return false;
			}
		}
		return true;
	}
}
