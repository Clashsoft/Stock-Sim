package com.clashsoft.stocksim.local;

import com.clashsoft.stocksim.data.Transaction;
import com.clashsoft.stocksim.model.Player;
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

	// for all t: this == t.stock
	private final List<Transaction> transactions = new ArrayList<>();

	public LocalStock(StockSim sim, UUID id)
	{
		this.sim = sim;
		this.id = id;
	}

	public LocalStock(StockSim sim, UUID id, String name, String symbol)
	{
		this.sim = sim;
		this.id = id;
		this.name = name;
		this.symbol = symbol;
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
	public long getValue(long time)
	{
		return 0;
	}

	@Override
	public List<Transaction> getTransactions(long start, long end)
	{
		return Transaction.filter(this.transactions, start, end);
	}
}
