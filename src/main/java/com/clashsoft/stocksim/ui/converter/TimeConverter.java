package com.clashsoft.stocksim.ui.converter;

import javafx.util.StringConverter;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class TimeConverter extends StringConverter<Number>
{
	@Override
	public String toString(Number object)
	{
		return format(object.longValue());
	}

	@Override
	public Number fromString(String string)
	{
		return parse(string);
	}

	public static String format(long time)
	{
		return LocalDateTime.ofEpochSecond(time, 0, ZoneOffset.UTC).toString();
	}

	public static long parse(String text)
	{
		return LocalDateTime.parse(text).atOffset(ZoneOffset.UTC).toEpochSecond();
	}
}
