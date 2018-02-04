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

	public static long parsePrice(String price)
	{
		long result = 0;
		long multiplier = 1;
		int exponent = 0;

		final int length = price.length();
		for (int i = length - 1; i >= 0; i--)
		{
			char c = price.charAt(i);
			switch (c)
			{
			case '0':
			case '1':
			case '2':
			case '3':
			case '4':
			case '5':
			case '6':
			case '7':
			case '8':
			case '9':
				result += multiplier * (c - '0');
				multiplier *= 10;
				exponent++;
				continue;
			case '.':
				if (i != length - 3)
				{
					throw new NumberFormatException("unexpected '.' at position " + i);
				}
				continue;
			case ',':
				if (exponent < 5 || exponent % 3 != 2)
				{
					throw new NumberFormatException("unexpected ',' at position " + i);
				}
				continue;
			case '-':
				result = -result;
				// Fallthrough
			case '+':
				if (i == 0)
				{
					continue;
				}
				// Fallthrough
			default:
				throw new NumberFormatException("unexpected symbol '" + c + "' at position " + i);
			}
		}

		return result;
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
