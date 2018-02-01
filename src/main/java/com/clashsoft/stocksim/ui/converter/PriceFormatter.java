package com.clashsoft.stocksim.ui.converter;

import java.util.Locale;

public class PriceFormatter
{
	public static long getDollars(long price)
	{
		return price / 100;
	}

	public static long getCents(long price)
	{
		return Math.abs(price % 100);
	}

	public static String formatAmount(long amount)
	{
		return String.format(Locale.ENGLISH, "%,d", amount);
	}

	public static String formatDollarCents(long price)
	{
		return String.format(Locale.ENGLISH, "$ %,d.%02d", price / 100, Math.abs(price % 100));
	}

	public static String formatPrice(long price)
	{
		return String.format(Locale.ENGLISH, "%,d.%02d", price / 100, Math.abs(price % 100));
	}

	public static String formatChange(long price)
	{
		char sign;
		if (price < 0)
		{
			price = -price;
			sign = '-';
		}
		else
		{
			sign = '+';
		}

		long dollars = price / 100;
		long cents = price % 100;
		return String.format("%c $ %,d.%02d", sign, dollars, cents);
	}
}
