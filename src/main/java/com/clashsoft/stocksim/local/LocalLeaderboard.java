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
		List<Long> players = new ArrayList<>();
		this.stockSim.eachPlayer(player1 -> players.add(player1.getNetWorth(time)));
		players.sort(((Comparator<Long>) Long::compareTo).reversed());
		return players.indexOf(player.getNetWorth(time)) + 1;
	}
}
