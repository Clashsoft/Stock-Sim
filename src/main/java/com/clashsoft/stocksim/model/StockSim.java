package com.clashsoft.stocksim.model;

import com.clashsoft.stocksim.data.Transaction;

import java.util.List;

public interface StockSim
{
	long getTime();

	List<Transaction> getTransactions();

	List<Player> getPlayers();

	List<Stock> getStocks();

	Leaderboard getLeaderboard();

	void addTransaction(Transaction transaction);

	void addPlayer(Player player);

	Player createPlayer(String name, long cash);

	void addStock(Stock stock);

	Stock createStock(String symbol, String name, long price, long amount);
}
