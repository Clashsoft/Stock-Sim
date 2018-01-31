package com.clashsoft.stocksim.ui.table;

import com.clashsoft.stocksim.ui.converter.TimeConverter;
import javafx.scene.control.TableCell;

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

		this.setText(TimeConverter.format(item));
	}
}
