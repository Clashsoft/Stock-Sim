package com.clashsoft.stocksim.local;

import com.clashsoft.stocksim.model.Leaderboard;
import com.clashsoft.stocksim.model.Player;
import com.clashsoft.stocksim.model.StockSim;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class LocalLeaderboard implements Leaderboard
{
	private final StockSim stockSim;

	public LocalLeaderboard(StockSim stockSim)
	{
		this.stockSim = stockSim;
	}

	@Override
	public StockSim getStockSim()
	{
		return this.stockSim;
	}

	@Override
	public long getPosition(Player player, long time)
	{
		List<Player> players = new ArrayList<>(this.stockSim.getPlayers());
		players.sort(Comparator.comparingLong(it -> it.getNetWorth(time)));
		return players.indexOf(player) + 1;
	}
}
