package com.clashsoft.stocksim.local;

import com.clashsoft.stocksim.model.Leaderboard;
import com.clashsoft.stocksim.model.Player;
import com.clashsoft.stocksim.model.StockSim;

import java.util.ArrayList;
import java.util.List;

public class LocalStockSim implements StockSim
{
	private long time;
	private List<Player> players     = new ArrayList<>();
	private Leaderboard  leaderboard = new LocalLeaderboard(this);

	@Override
	public long getTime()
	{
		return this.time;
	}

	@Override
	public List<Player> getPlayers()
	{
		return this.players;
	}

	@Override
	public Leaderboard getLeaderboard()
	{
		return this.leaderboard;
	}
}
