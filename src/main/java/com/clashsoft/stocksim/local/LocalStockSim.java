package com.clashsoft.stocksim.local;

import com.clashsoft.stocksim.data.Transaction;
import com.clashsoft.stocksim.model.Leaderboard;
import com.clashsoft.stocksim.model.Player;
import com.clashsoft.stocksim.model.Stock;
import com.clashsoft.stocksim.model.StockSim;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LocalStockSim implements StockSim
{
	private long time;
	private List<Transaction> transactions = new ArrayList<>();
	private List<Player>      players      = new ArrayList<>();
	private List<Stock>       stocks       = new ArrayList<>();
	private Leaderboard       leaderboard  = new LocalLeaderboard(this);

	@Override
	public long getTime()
	{
		return this.time;
	}

	@Override
	public List<Transaction> getTransactions()
	{
		return this.transactions;
	}

	@Override
	public List<Player> getPlayers()
	{
		return this.players;
	}

	@Override
	public Player getPlayer(String name)
	{
		for (Player player : this.players)
		{
			if (player.getName().equals(name))
			{
				return player;
			}
		}
		return null;
	}

	@Override
	public Player getPlayer(UUID id)
	{
		for (Player player : this.players)
		{
			if (player.getID().equals(id))
			{
				return player;
			}
		}
		return null;
	}

	@Override
	public List<Stock> getStocks()
	{
		return this.stocks;
	}

	@Override
	public Stock getStock(String symbol)
	{
		for (Stock stock : this.stocks)
		{
			if (stock.getSymbol().equals(symbol))
			{
				return stock;
			}
		}
		return null;
	}

	@Override
	public Stock getStock(UUID id)
	{
		for (Stock stock : this.stocks)
		{
			if (stock.getID().equals(id))
			{
				return stock;
			}
		}
		return null;
	}

	@Override
	public Leaderboard getLeaderboard()
	{
		return this.leaderboard;
	}

	@Override
	public void addTransaction(Transaction transaction)
	{
		this.transactions.add(transaction);

		transaction.getStock().addTransaction(transaction);

		final Player buyer = transaction.getBuyer();
		if (buyer != null)
		{
			buyer.addTransaction(transaction);
		}

		final Player seller = transaction.getSeller();
		if (seller != null)
		{
			seller.addTransaction(transaction);
		}
	}

	@Override
	public void addPlayer(Player player)
	{
		this.players.add(player);
	}

	@Override
	public Player createPlayer(String name, long cash)
	{
		final LocalPlayer player = new LocalPlayer(this, UUID.randomUUID(), name, cash);
		this.addPlayer(player);
		return player;
	}

	@Override
	public void addStock(Stock stock)
	{
		this.stocks.add(stock);
	}

	@Override
	public Stock createStock(String name, String symbol, long amount, long price)
	{
		final LocalStock stock = new LocalStock(this, UUID.randomUUID(), name, symbol, amount);
		final Player player = this.createPlayer(name, amount * price);

		this.addTransaction(new Transaction(UUID.randomUUID(), this.time, stock, amount, price, null, player));

		this.addStock(stock);
		return stock;
	}
}
