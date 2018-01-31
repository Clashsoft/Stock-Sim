package com.clashsoft.stocksim.strategy;

import com.clashsoft.stocksim.data.Order;
import com.clashsoft.stocksim.model.Player;
import com.clashsoft.stocksim.model.StockSim;

public interface Strategy
{
	Order makeOrder(StockSim sim, Player player);

	static String getName(Strategy strategy)
	{
		return strategy.getClass().getSimpleName();
	}

	static Strategy fromName(String name)
	{
		Strategy strategy;
		try
		{
			final Class<?> type = Class.forName(Strategy.class.getPackage().getName() + "." + name);
			strategy = (Strategy) type.newInstance();
		}
		catch (Exception e)
		{
			strategy = null;
		}
		return strategy;
	}
}
