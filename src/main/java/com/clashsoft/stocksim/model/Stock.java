package com.clashsoft.stocksim.model;

import com.clashsoft.stocksim.data.Transaction;

import java.util.List;
import java.util.UUID;

public interface Stock
{
	StockSim getStockSim();

	UUID getID();

	String getName();

	String getSymbol();

	long getPrice();

	long getPrice(long time);

	long getSupply();

	default long getMarketCap()
	{
		return this.getPrice() * this.getSupply();
	}

	default long getMarketCap(long time)
	{
		return this.getPrice(time) * this.getSupply();
	}

	List<Transaction> getTransactions();

	List<Transaction> getTransactions(long start, long end);

	void addTransaction(Transaction transaction);
}
