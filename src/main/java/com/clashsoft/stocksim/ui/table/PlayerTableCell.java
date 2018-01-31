package com.clashsoft.stocksim.ui.table;

import com.clashsoft.stocksim.model.Player;
import com.clashsoft.stocksim.ui.PlayerViewController;
import javafx.scene.control.TableCell;
import javafx.scene.input.MouseButton;

public class PlayerTableCell<T> extends TableCell<T, Player>
{
	public PlayerTableCell()
	{
		this.setOnMouseClicked(evt -> {
			final Player item = this.getItem();
			if (item != null && evt.getButton() == MouseButton.PRIMARY && evt.getClickCount() >= 2)
			{
				PlayerViewController.open(item);
			}
		});
	}

	@Override
	protected void updateItem(Player item, boolean empty)
	{
		this.setItem(item);

		if (empty || item == null)
		{
			this.setText("");
			return;
		}

		this.setText(item.getName());
	}
}
