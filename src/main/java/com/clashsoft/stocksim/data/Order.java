package com.clashsoft.stocksim.data;

import com.clashsoft.stocksim.model.Player;
import com.clashsoft.stocksim.model.Stock;
import com.clashsoft.stocksim.model.StockSim;
import com.clashsoft.stocksim.ui.converter.TimeConverter;

import java.util.Comparator;
import java.util.UUID;

public class Order
{
	public static final Comparator<Order> COMPARATOR = Comparator.comparingLong(Order::getPrice)
	                                                             .thenComparingLong(Order::getTime);
	private final UUID id;
	private final long time;
	private final long expiry;

	private final Player player;
	private final Stock  stock;

	private final long amount; // > 0 = buy, < 0 = sell
	private final long price;

	public Order(UUID id, long time, long expiry, Player player, Stock stock, long amount, long price)
	{
		this.id = id;
		this.time = time;
		this.expiry = expiry;
		this.player = player;
		this.stock = stock;
		this.amount = amount;
		this.price = price;
	}

	public UUID getId()
	{
		return this.id;
	}

	public long getTime()
	{
		return this.time;
	}

	public long getExpiry()
	{
		return this.expiry;
	}

	public Player getPlayer()
	{
		return this.player;
	}

	public Stock getStock()
	{
		return this.stock;
	}

	public long getAmount()
	{
		return this.amount;
	}

	public long getPrice()
	{
		return this.price;
	}

	public long getTotal()
	{
		return this.amount * this.price;
	}

	public String toCSV()
	{
		return "" //
		       + TimeConverter.format(this.time) + "," // time
		       + TimeConverter.format(this.expiry) + "," // expiry
		       + this.id + "," // id
		       + this.player.getID() + "," // player id
		       + this.stock.getID() + "," // stock id
		       + this.amount + "," // amount
		       + this.price + "," // price
			;
	}

	public static Order parseCSV(StockSim sim, String csv)
	{
		final String[] array = csv.split(",");
		int i = 0;

		final long time = TimeConverter.parse(array[i++]);
		final long expiry = array[i].contains("T") ? TimeConverter.parse(array[i++]) : time + Period.DAY.length;
		final UUID id = UUID.fromString(array[i++]);
		final Player player = sim.getPlayer(UUID.fromString(array[i++]));
		final Stock stock = sim.getStock(UUID.fromString(array[i++]));
		final long amount = Long.parseLong(array[i++]);
		final long price = Long.parseLong(array[i++]);

		return new Order(id, time, expiry, player, stock, amount, price);
	}

	public Order split(long amount)
	{
		long newAmount;
		if (this.amount < 0)
		{
			newAmount = this.amount + amount;
			if (newAmount >= 0)
			{
				return null;
			}
		}
		else
		{
			newAmount = this.amount - amount;
			if (newAmount <= 0)
			{
				return null;
			}
		}

		return new Order(UUID.randomUUID(), this.time, this.expiry, this.player, this.stock, newAmount, this.price);
	}
}
