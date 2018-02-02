package com.clashsoft.stocksim.data;

import com.clashsoft.stocksim.model.Player;
import com.clashsoft.stocksim.model.Stock;
import com.clashsoft.stocksim.model.StockSim;
import com.clashsoft.stocksim.ui.converter.TimeConverter;

import java.util.List;
import java.util.UUID;

public class Transaction
{
	private static final UUID DEFAULT_UUID = new UUID(0, 0);

	private final UUID id;
	private final long time;

	private final Stock stock;
	private final long  amount;

	private final long price;

	private final Player seller;
	private final Player buyer;

	public Transaction(UUID id, long time, Stock stock, long amount, long price, Player seller, Player buyer)
	{
		this.id = id;
		this.time = time;
		this.stock = stock;
		this.amount = amount;
		this.price = price;
		this.seller = seller;
		this.buyer = buyer;
	}

	public UUID getID()
	{
		return this.id;
	}

	public long getTime()
	{
		return this.time;
	}

	public Stock getStock()
	{
		return this.stock;
	}

	public long getAmount()
	{
		return this.amount;
	}

	public StockAmount getStockAmount()
	{
		return new StockAmount(this.stock, this.amount);
	}

	public long getPrice()
	{
		return this.price;
	}

	public long getTotal()
	{
		return this.price * this.amount;
	}

	public Player getSeller()
	{
		return this.seller;
	}

	public Player getBuyer()
	{
		return this.buyer;
	}

	public static List<Transaction> filter(List<Transaction> transactions, long start, long end)
	{
		final int size = transactions.size();
		if (transactions.isEmpty())
		{
			return transactions;
		}

		int startIndex = 0;
		while (startIndex < size && transactions.get(startIndex).getTime() < start)
		{
			startIndex++;
		}

		int endIndex = transactions.size() - 1;
		while (endIndex >= 0 && transactions.get(endIndex).getTime() >= end)
		{
			endIndex--;
		}

		return transactions.subList(startIndex, endIndex + 1);
	}

	public String toCSV()
	{
		return TimeConverter.format(this.time) + "," // time
		       + this.id + "," // trx id
		       + this.stock.getID() + "," // stock id
		       + (this.seller == null ? DEFAULT_UUID : this.seller.getID()) + "," // seller id
		       + (this.buyer == null ? DEFAULT_UUID : this.buyer.getID()) + "," // buyer id
		       + this.amount + "," // amount
		       + this.getPrice() + ","; // price in cent
	}

	public static Transaction parseCSV(StockSim sim, String csv)
	{
		final String[] array = csv.split(",");
		int i = 0;

		final long time = TimeConverter.parse(array[i++]);
		final UUID id = UUID.fromString(array[i++]);
		final Stock stock = sim.getStock(UUID.fromString(array[i++]));
		final Player seller = sim.getPlayer(UUID.fromString(array[i++]));
		final Player buyer = sim.getPlayer(UUID.fromString(array[i++]));
		final long amount = Long.parseLong(array[i++]);
		final long price = Long.parseLong(array[i++]);

		return new Transaction(id, time, stock, amount, price, seller, buyer);
	}
}
