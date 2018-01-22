package com.clashsoft.stocksim.model;

import com.clashsoft.stocksim.data.StockAmount;
import com.clashsoft.stocksim.data.Transaction;

import java.util.List;
import java.util.UUID;

public interface Player
{
	StockSim getStockSim();

	UUID getID();

	String getName();

	long getCash();

	long getCash(long time);

	long getStocksValue();

	long getStocksValue(long time);

	long getNetWorth();

	long getNetWorth(long time);

	List<Transaction> getTransactions();

	List<Transaction> getTransactions(long start, long end);

	List<StockAmount> getStocks();

	List<StockAmount> getStocks(long time);

	void addTransaction(Transaction transaction);
}
