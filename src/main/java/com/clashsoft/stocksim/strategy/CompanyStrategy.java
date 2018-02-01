package com.clashsoft.stocksim.strategy;

import com.clashsoft.stocksim.data.Order;
import com.clashsoft.stocksim.data.Period;
import com.clashsoft.stocksim.data.StockAmount;
import com.clashsoft.stocksim.model.Player;
import com.clashsoft.stocksim.model.Stock;
import com.clashsoft.stocksim.model.StockSim;

import java.util.UUID;
import java.util.function.Consumer;

public class CompanyStrategy implements Strategy
{
	@Override
	public void makeOrder(StockSim sim, Player player, Consumer<Order> orders)
	{
		for (StockAmount amount : player.getPortfolio().getStockAmounts())
		{
			final Stock stock = amount.getStock();
			final long time = sim.getTime();
			final long expiry = time + 2 * Period.SECOND.length;
			orders.accept(new Order(UUID.randomUUID(), time, expiry, player, stock, -amount.getAmount(), stock.getPrice()));
		}
	}
}
