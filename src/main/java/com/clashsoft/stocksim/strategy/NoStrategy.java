package com.clashsoft.stocksim.strategy;

import com.clashsoft.stocksim.data.Order;
import com.clashsoft.stocksim.model.Player;
import com.clashsoft.stocksim.model.StockSim;

import java.util.function.Consumer;

public class NoStrategy implements Strategy
{
	@Override
	public void makeOrder(StockSim sim, Player player, Consumer<Order> orders)
	{
	}
}
