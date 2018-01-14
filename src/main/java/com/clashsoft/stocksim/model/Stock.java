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

	long getValue(long time);

	List<Transaction> getTransactions(long start, long end);
}
