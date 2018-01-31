package com.clashsoft.stocksim.strategy;

import com.clashsoft.stocksim.data.Order;
import com.clashsoft.stocksim.model.Player;
import com.clashsoft.stocksim.model.StockSim;

public class NoStrategy implements Strategy
{
	@Override
	public Order makeOrder(StockSim sim, Player player)
	{
		return null;
	}
}
