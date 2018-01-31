package com.clashsoft.stocksim.strategy;

import com.clashsoft.stocksim.data.Order;
import com.clashsoft.stocksim.data.StockAmount;
import com.clashsoft.stocksim.model.Player;
import com.clashsoft.stocksim.model.Stock;
import com.clashsoft.stocksim.model.StockSim;

import java.util.List;
import java.util.UUID;

public class CompanyStrategy implements Strategy
{
	@Override
	public Order makeOrder(StockSim sim, Player player)
	{
		final List<StockAmount> stocks = player.getStocks();
		if (stocks.isEmpty())
		{
			return null;
		}

		if (sim.getOrders().stream().anyMatch(order -> order.getPlayer() == player))
		{
			return null;
		}

		final StockAmount first = stocks.get(0);
		final Stock stock = first.getStock();
		return new Order(UUID.randomUUID(), sim.getTime(), player, stock, -first.getAmount(), stock.getPrice());
	}
}
