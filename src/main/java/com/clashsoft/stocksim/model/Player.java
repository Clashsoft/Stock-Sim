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

	long getCash(long time);

	long getNetWorth(long time);

	List<Transaction> getTransactions(long start, long end);

	List<StockAmount> getStocks(long time);
}
