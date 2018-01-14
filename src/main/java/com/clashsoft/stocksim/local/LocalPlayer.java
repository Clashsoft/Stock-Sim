package com.clashsoft.stocksim.local;

import com.clashsoft.stocksim.model.Player;
import com.clashsoft.stocksim.model.Stock;
import com.clashsoft.stocksim.data.StockAmount;
import com.clashsoft.stocksim.data.Transaction;
import com.clashsoft.stocksim.model.StockSim;

import java.util.*;

public class LocalPlayer implements Player
{
	private final StockSim sim;
	private final UUID id;

	private String name;

	private long startCash;

	// for all t: this == t.buyer || this == t.seller
	private final List<Transaction> transactions = new ArrayList<>();

	public LocalPlayer(StockSim sim, UUID id, String name, long startCash)
	{
		this.sim = sim;
		this.id = id;
		this.name = name;
		this.startCash = startCash;
	}

	@Override
	public StockSim getStockSim()
	{
		return this.sim;
	}

	@Override
	public UUID getID()
	{
		return this.id;
	}

	@Override
	public String getName()
	{
		return this.name;
	}

	public List<Transaction> getTransactions()
	{
		return this.transactions;
	}

	@Override
	public List<Transaction> getTransactions(long start, long end)
	{
		return Transaction.filter(this.transactions, start, end);
	}

	@Override
	public long getNetWorth(long time)
	{
		final List<Transaction> transactions = this.getTransactions(0, time);
		final long cash = this.startCash + this.getCashChange(transactions);
		final long stocks = this.getStocks(transactions).stream().mapToLong(s -> s.getValue(time)).sum();
		return cash + stocks;
	}

	@Override
	public long getCash(long time)
	{
		return this.startCash + this.getCashChange(this.getTransactions(0, time));
	}

	private long getCashChange(List<Transaction> transactions)
	{
		long delta = 0;
		for (Transaction transaction : transactions)
		{
			if (this == transaction.getBuyer())
			{
				delta -= transaction.getPrice();
			}
			else if (this == transaction.getSeller())
			{
				delta += transaction.getPrice();
			}
		}
		return delta;
	}

	@Override
	public List<StockAmount> getStocks(long time)
	{
		return this.getStocks(this.getTransactions(0, time));
	}

	private List<StockAmount> getStocks(List<Transaction> transactions)
	{
		final Map<Stock, Long> amounts = new HashMap<>();

		// replay transaction history
		for (Transaction transaction : transactions)
		{
			long value = amounts.getOrDefault(transaction.getStock(), 0L);

			if (this == transaction.getBuyer())
			{
				value += transaction.getAmount();
			}
			else if (this == transaction.getSeller())
			{
				value -= transaction.getAmount();
			}

			amounts.put(transaction.getStock(), value);
		}

		final List<StockAmount> result = new ArrayList<>();
		for (Map.Entry<Stock, Long> entry : amounts.entrySet())
		{
			final Long value = entry.getValue();
			if (value == null)
			{
				continue;
			}
			final long amount = value;
			if (amount <= 0)
			{
				continue;
			}
			result.add(new StockAmount(entry.getKey(), amount));
		}
		return result;
	}
}
