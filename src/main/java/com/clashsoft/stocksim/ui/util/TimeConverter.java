package com.clashsoft.stocksim.ui.util;

import javafx.util.StringConverter;

public class TimeConverter extends StringConverter<Number>
{
	@Override
	public String toString(Number object)
	{
		return TimeTableCell.formatTime(object.longValue());
	}

	@Override
	public Number fromString(String string)
	{
		return TimeTableCell.parseTime(string);
	}
}
