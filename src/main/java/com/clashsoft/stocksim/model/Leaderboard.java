package com.clashsoft.stocksim.model;

public interface Leaderboard
{
	StockSim getStockSim();

	long getPosition(Player player, long time);
}
