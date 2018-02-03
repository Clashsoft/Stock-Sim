package com.clashsoft.stocksim.strategy;

import com.clashsoft.stocksim.data.Period;
import com.clashsoft.stocksim.data.StockAmount;
import com.clashsoft.stocksim.model.Player;
import com.clashsoft.stocksim.model.Stock;
import com.clashsoft.stocksim.model.StockSim;

public class CompanyStrategy extends PlayerStrategy
{
	@Override
	protected Stock randomBuy(StockSim sim, Player player)
	{
		final String playerName = player.getName();
		final String symbol = playerName.substring(1, playerName.indexOf(' '));
		return sim.getStock(symbol);
	}

	@Override
	protected StockAmount randomSell(StockSim sim, Player player)
	{
		return super.randomSell(sim, player);
	}

	@Override
	protected boolean isActive(StockSim sim)
	{
		return true;
	}

	@Override
	protected long getExpiryDuration()
	{
		return 2 * Period.SECOND.length;
	}

	@Override
	protected double getBuyMultiplier()
	{
		return 1.0;
	}

	@Override
	protected double getSellMultiplier()
	{
		return 1.0;
	}
}
