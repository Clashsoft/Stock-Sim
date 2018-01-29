package com.clashsoft.stocksim.ui.util;

import javafx.scene.control.TableCell;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class TimeTableCell<T> extends TableCell<T, Long>
{
	@Override
	protected void updateItem(Long item, boolean empty)
	{
		if (empty || item == null)
		{
			this.setText("");
			return;
		}

		this.setText(formatTime(item));
	}

	public static String formatTime(long time)
	{
		return LocalDateTime.ofEpochSecond(time, 0, ZoneOffset.UTC).toString();
	}
}
