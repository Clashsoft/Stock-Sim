package com.clashsoft.stocksim.model;

import java.util.List;

public interface StockSim
{
	long getTime();

	List<Player> getPlayers();

	Leaderboard getLeaderboard();
}
