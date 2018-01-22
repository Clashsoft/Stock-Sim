package com.clashsoft.stocksim.ui.util;

import javafx.util.StringConverter;

public class DollarStringConverter extends StringConverter<Long>
{
	@Override
	public String toString(Long object)
	{
		return String.format("%,d", object);
	}

	@Override
	public Long fromString(String string)
	{
		return Long.parseUnsignedLong(string.replace(",", ""));
	}
}
