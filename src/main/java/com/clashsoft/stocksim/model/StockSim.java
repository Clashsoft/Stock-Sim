package com.clashsoft.stocksim.model;

import com.clashsoft.stocksim.data.Order;
import com.clashsoft.stocksim.data.Transaction;

import java.util.List;
import java.util.UUID;

public interface StockSim
{
	long getTime();

	List<Order> getOrders();

	List<Transaction> getTransactions();

	List<Player> getPlayers();

	Player getPlayer(String name);

	Player getPlayer(UUID id);

	List<Stock> getStocks();

	Stock getStock(String symbol);

	Stock getStock(UUID id);

	Leaderboard getLeaderboard();

	void addTransaction(Transaction transaction);

	void addPlayer(Player player);

	Player createPlayer(String name, long cash);

	void addStock(Stock stock);

	Stock createStock(String symbol, String name, long price, long amount);
}
