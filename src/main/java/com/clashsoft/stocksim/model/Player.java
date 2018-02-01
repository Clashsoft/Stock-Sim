package com.clashsoft.stocksim.model;

import com.clashsoft.stocksim.data.Order;
import com.clashsoft.stocksim.data.StockAmount;
import com.clashsoft.stocksim.data.Transaction;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public interface Player
{
	StockSim getStockSim();

	UUID getID();

	String getName();

	default long getCash()
	{
		return this.getPortfolio().getCash();
	}

	default long getCash(long time)
	{
		return this.getPortfolio(time).getCash();
	}

	default long getStocksValue()
	{
		return this.getPortfolio().getStockAmounts().stream().mapToLong(StockAmount::getValue).sum();
	}

	default long getStocksValue(long time)
	{
		return this.getPortfolio(time).getStocksValue(time);
	}

	default long getNetWorth()
	{
		return this.getCash() + this.getStocksValue();
	}

	default long getNetWorth(long time)
	{
		final Portfolio portfolio = this.getPortfolio(time);
		return portfolio.getCash() + portfolio.getStocksValue(time);
	}

	List<Transaction> getTransactions();

	List<Transaction> getTransactions(long start, long end);

	Portfolio getPortfolio();

	Portfolio getPortfolio(long time);

	void addTransaction(Transaction transaction);

	void makeOrder(Consumer<Order> orders);
}
