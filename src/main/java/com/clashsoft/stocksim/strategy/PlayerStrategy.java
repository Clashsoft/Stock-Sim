package com.clashsoft.stocksim.strategy;

import com.clashsoft.stocksim.data.Order;
import com.clashsoft.stocksim.data.Period;
import com.clashsoft.stocksim.data.StockAmount;
import com.clashsoft.stocksim.model.Player;
import com.clashsoft.stocksim.model.Stock;
import com.clashsoft.stocksim.model.StockSim;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

public class PlayerStrategy implements Strategy
{
	final Random random = new Random();

	@Override
	public void makeOrder(StockSim sim, Player player, Consumer<Order> orders)
	{
		if (!this.isActive(sim))
		{
			// ensures players only trade one second per minute
			return;
		}

		if (this.random.nextBoolean())
		{
			this.makeBuyOrder(sim, player, orders);
		}
		else
		{
			this.makeSellOrder(sim, player, orders);
		}
	}

	protected boolean isActive(StockSim sim)
	{
		return sim.getTime() % Period.MINUTE.length == 0;
	}

	protected long getExpiryDuration()
	{
		return Period.HOUR.length;
	}

	private void makeBuyOrder(StockSim sim, Player player, Consumer<Order> orders)
	{
		final Stock stock = this.randomBuy(sim, player);
		if (stock == null)
		{
			return;
		}

		final double priceMultiplier = this.getBuyMultiplier();
		final long price = Math.round(stock.getPrice() * priceMultiplier);
		final long amount = Math.round(player.getCash() * this.random.nextDouble() / price);

		if (amount <= 0)
		{
			return;
		}

		final long time = sim.getTime();
		orders.accept(new Order(UUID.randomUUID(), time, time + this.getExpiryDuration(), player, stock, amount, price));
	}

	protected Stock randomBuy(StockSim sim, Player player)
	{
		final AtomicLong maxChange = new AtomicLong();
		sim.eachStock(stock -> {
			final long value = this.getPriceChange(stock);
			if (value > maxChange.get())
			{
				maxChange.set(value);
			}
		});

		final List<Stock> bestStocks = new ArrayList<>();
		sim.eachStock(stock -> {
			final long value = this.getPriceChange(stock);
			if (value >= maxChange.get())
			{
				bestStocks.add(stock);
			}
		});
		return this.random(bestStocks);
	}

	protected double getBuyMultiplier()
	{
		return 1.0 - 0.01 * this.random.nextDouble();
	}

	private void makeSellOrder(StockSim sim, Player player, Consumer<Order> orders)
	{
		final StockAmount stockAmount = this.randomSell(sim, player);
		if (stockAmount == null)
		{
			return;
		}

		final Stock stock = stockAmount.getStock();
		final long amount = stockAmount.getAmount();
		final double priceMultiplier = this.getSellMultiplier();
		final long price = Math.round(stock.getPrice() * priceMultiplier);
		final long orderAmount = this.random.nextLong() % amount;

		final long time = sim.getTime();
		orders.accept(new Order(UUID.randomUUID(), time, time + this.getExpiryDuration(), player, stock, -orderAmount, price));
	}

	protected StockAmount randomSell(StockSim sim, Player player)
	{
		final List<StockAmount> stockAmounts = new ArrayList<>(player.getPortfolio().getStockAmounts());
		if (stockAmounts.isEmpty())
		{
			return null;
		}
		return this.random(stockAmounts);
	}

	protected double getSellMultiplier()
	{
		return 1.0 + 0.01 * this.random.nextDouble();
	}

	private long getPriceChange(Stock stock)
	{
		return stock.getPrice() - stock.getPrice(stock.getStockSim().getTime() - 1);
	}

	protected <T> T random(List<T> stockAmounts)
	{
		return stockAmounts.get(this.random.nextInt(stockAmounts.size()));
	}
}
