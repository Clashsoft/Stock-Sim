package com.clashsoft.stocksim.ui.util;

import javafx.util.StringConverter;

import java.util.Locale;

public class ShortDollarConverter extends StringConverter<Number>
{
	@Override
	public String toString(Number object)
	{
		final long value = object.longValue();
		final String prefix = value < 0 ? "- $ " : "$ ";
		final long abs = Math.abs(value);
		final long dollars = abs / 100;

		if (dollars < 1000)
		{
			return prefix + String.format("%d.%02d", dollars, abs % 100);
		}
		if (dollars < 1_000_000L)
		{
			return prefix + String.format(Locale.ENGLISH, "%,d", dollars / 1000L);
		}
		if (dollars < 1_000_000_000L)
		{
			return prefix + String.format(Locale.ENGLISH, "%,dM", dollars / 1_000_000L);
		}
		if (dollars < 1_000_000_000_000L)
		{
			return prefix + String.format(Locale.ENGLISH, "%,dB", dollars / 1_000_000_000L);
		}
		if (dollars < 1_000_000_000_000_000L)
		{
			return prefix + String.format(Locale.ENGLISH, "%,dT", dollars / 1_000_000_000_000L);
		}
		if (dollars < 1_000_000_000_000_000_000L)
		{
			return prefix + String.format(Locale.ENGLISH, "%,dQ", dollars / 1_000_000_000_000_000L);
		}

		return prefix + String.format(Locale.ENGLISH, "%,dQ", dollars / 1_000_000_000_000_000_000L);
	}

	@Override
	public Long fromString(String string)
	{
		throw new UnsupportedOperationException();
	}
}
