package com.clashsoft.stocksim.local;

import com.clashsoft.stocksim.data.Transaction;
import com.clashsoft.stocksim.model.Stock;
import com.clashsoft.stocksim.model.StockSim;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LocalStock implements Stock
{
	private final StockSim sim;
	private final UUID     id;

	private String name;
	private String symbol;

	private long supply;

	// for all t: this == t.stock
	private final List<Transaction> transactions = new ArrayList<>();

	public LocalStock(StockSim sim, UUID id)
	{
		this.sim = sim;
		this.id = id;
	}

	public LocalStock(StockSim sim, UUID id, String name, String symbol, long supply)
	{
		this.sim = sim;
		this.id = id;
		this.name = name;
		this.symbol = symbol;
		this.supply = supply;
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

	@Override
	public String getSymbol()
	{
		return this.symbol;
	}

	@Override
	public long getSupply()
	{
		return this.supply;
	}

	@Override
	public long getPrice()
	{
		return this.getPrice(this.getTransactions());
	}

	@Override
	public long getPrice(long time)
	{
		return this.getPrice(this.getTransactions(0, time));
	}

	private long getPrice(List<Transaction> transactions)
	{
		long total = 0L;
		long amount = 0L;

		for (Transaction transaction : transactions)
		{
			total += transaction.getTotal();
			amount += transaction.getAmount();
		}

		return total / amount;
	}

	@Override
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
	public void addTransaction(Transaction transaction)
	{
		this.transactions.add(transaction);
	}

	public String toCSV()
	{
		return this.id + "," + this.symbol + "," + this.name + "," + this.supply;
	}

	public static LocalStock parseCSV(LocalStockSim sim, String csv)
	{
		final String[] array = csv.split(",");
		int i = 0;

		final UUID id = UUID.fromString(array[i++]);
		final String symbol = array[i++];
		final String name = array[i++];
		final long supply = Long.parseLong(array[i++]);

		return new LocalStock(sim, id, name, symbol, supply);
	}
}
