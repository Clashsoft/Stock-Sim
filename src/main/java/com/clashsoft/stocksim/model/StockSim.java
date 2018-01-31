package com.clashsoft.stocksim.model;

import com.clashsoft.stocksim.data.Order;
import com.clashsoft.stocksim.data.Transaction;

import java.util.UUID;
import java.util.function.Consumer;

public interface StockSim
{
	long getTime();

	void eachOrder(int n, Consumer<Order> action);

	void eachTransaction(int n, Consumer<Transaction> action);

	void addTransaction(Transaction transaction);

	// Players

	void eachPlayer(Consumer<Player> action);

	Player getPlayer(String name);

	Player getPlayer(UUID id);

	void addPlayer(Player player);

	Player createPlayer(String name, long cash);

	// Stocks

	void eachStock(Consumer<Stock> action);

	Stock getStock(String symbol);

	Stock getStock(UUID id);

	void addStock(Stock stock);

	Stock createStock(String symbol, String name, long price, long amount);

	// Leaderboard

	Leaderboard getLeaderboard();
}
