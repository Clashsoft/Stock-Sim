package com.clashsoft.stocksim.ui.converter;

import javafx.util.StringConverter;

public class CentStringConverter extends StringConverter<Long>
{
	@Override
	public String toString(Long object)
	{
		final long value = object;
		return value < 10 ? "0" + value : Long.toString(value);
	}

	@Override
	public Long fromString(String string)
	{
		switch (string.length())
		{
		case 0:
			return 0L;
		case 1:
			return Character.digit(string.charAt(0), 10) * 10L;
		}

		return Long.parseUnsignedLong(string);
	}
}
