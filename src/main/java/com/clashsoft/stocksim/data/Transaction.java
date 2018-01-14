package com.clashsoft.stocksim.data;

import com.clashsoft.stocksim.model.Player;
import com.clashsoft.stocksim.model.Stock;

import java.util.List;

public class Transaction
{
	private final long time;

	private final Stock stock;
	private final long  amount;

	private final long price;

	private final Player seller;
	private final Player buyer;

	public Transaction(long time, Stock stock, long amount, long price, Player seller, Player buyer)
	{
		this.time = time;
		this.stock = stock;
		this.amount = amount;
		this.price = price;
		this.seller = seller;
		this.buyer = buyer;
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
		int startIndex = 0;
		while (transactions.get(startIndex).getTime() < start)
		{
			startIndex++;
		}

		int endIndex = transactions.size() - 1;
		while (transactions.get(endIndex).getTime() > end)
		{
			endIndex--;
		}

		return transactions.subList(startIndex, endIndex + 1);
	}
}
