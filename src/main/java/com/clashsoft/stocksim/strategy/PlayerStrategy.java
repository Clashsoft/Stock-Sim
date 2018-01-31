package com.clashsoft.stocksim.strategy;

import com.clashsoft.stocksim.data.Order;
import com.clashsoft.stocksim.data.StockAmount;
import com.clashsoft.stocksim.model.Player;
import com.clashsoft.stocksim.model.Stock;
import com.clashsoft.stocksim.model.StockSim;

import java.util.List;
import java.util.Random;
import java.util.UUID;

public class PlayerStrategy implements Strategy
{
	final Random random = new Random();

	@Override
	public Order makeOrder(StockSim sim, Player player)
	{
		if (this.random.nextFloat() >= 0.1)
		{
			return null;
		}

		if (this.random.nextBoolean())
		{
			// buy order

			final List<Stock> stocks = sim.getStocks();
			final Stock stock = stocks.get(this.random.nextInt(stocks.size()));
			final double priceMultiplier = 0.9 + 0.1 * this.random.nextDouble();
			final long price = (long) (stock.getPrice() * priceMultiplier);
			final long amount = (long) (player.getCash() * this.random.nextDouble() / price);

			if (amount <= 0)
			{
				return null;
			}

			return new Order(UUID.randomUUID(), sim.getTime(), player, stock, amount, price);
		}
		else
		{
			// sell order

			final List<StockAmount> stocks = player.getStocks();
			if (stocks.isEmpty())
			{
				return null;
			}

			final StockAmount stockAmount = stocks.get(this.random.nextInt(stocks.size()));
			final Stock stock = stockAmount.getStock();
			final long amount = stockAmount.getAmount();
			if (amount <= 0)
			{
				return null;
			}

			final double priceMultiplier = 1.0 + 0.1 * this.random.nextDouble();
			final long price = (long) (stock.getPrice() * priceMultiplier);
			final long orderAmount = this.random.nextLong() % amount;

			return new Order(UUID.randomUUID(), sim.getTime(), player, stock, -orderAmount, price);
		}
	}
}
