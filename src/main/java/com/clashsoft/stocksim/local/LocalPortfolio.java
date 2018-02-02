package com.clashsoft.stocksim.local;

import com.clashsoft.stocksim.data.StockAmount;
import com.clashsoft.stocksim.data.Transaction;
import com.clashsoft.stocksim.model.Player;
import com.clashsoft.stocksim.model.Portfolio;
import com.clashsoft.stocksim.model.Stock;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class LocalPortfolio implements Portfolio
{
	private final Player player;

	private long cash;

	private Map<Stock, Long> stocks;

	public LocalPortfolio(LocalPlayer player)
	{
		this.player = player;
		this.cash = player.getStartCash();
		this.stocks = new HashMap<>();
	}

	public LocalPortfolio(Portfolio portfolio)
	{
		if (portfolio instanceof LocalPortfolio)
		{
			final LocalPortfolio local = (LocalPortfolio) portfolio;
			this.player = local.player;
			this.cash = local.cash;
			this.stocks = new HashMap<>(local.stocks);
			return;
		}

		this.player = portfolio.getPlayer();
		this.cash = portfolio.getCash();
		this.stocks = new HashMap<>();
		portfolio.getStockAmounts().forEach(amount -> this.stocks.put(amount.getStock(), amount.getValue()));
	}

	@Override
	public Player getPlayer()
	{
		return this.player;
	}

	@Override
	public long getCash()
	{
		return this.cash;
	}

	@Override
	public Collection<Stock> getStocks()
	{
		return this.stocks.keySet();
	}

	@Override
	public Collection<StockAmount> getStockAmounts()
	{
		return this.stocks.entrySet().stream().map(e -> new StockAmount(e.getKey(), e.getValue()))
		                  .collect(Collectors.toList());
	}

	@Override
	public long getStockAmount(Stock stock)
	{
		return this.stocks.getOrDefault(stock, 0L);
	}

	@Override
	public long getStocksValue(long time)
	{
		long sum = 0;
		for (Map.Entry<Stock, Long> entry : this.stocks.entrySet())
		{
			sum += entry.getKey().getPrice(time) * entry.getValue();
		}
		return sum;
	}

	@Override
	public long getNetWorth(long time)
	{
		return this.getCash() + this.getStocksValue(time);
	}

	public void addTransaction(Transaction trx)
	{
		if (this.player == trx.getBuyer())
		{
			this.cash -= trx.getTotal();

			this.stocks.put(trx.getStock(), this.stocks.getOrDefault(trx.getStock(), 0L) + trx.getAmount());
		}
		else if (this.player == trx.getSeller())
		{
			this.cash += trx.getTotal();

			long amount = this.stocks.getOrDefault(trx.getStock(), 0L) + trx.getAmount();
			if (amount == 0)
			{
				this.stocks.remove(trx.getStock());
			}
			else
			{
				this.stocks.put(trx.getStock(), amount);
			}
		}
	}
}
