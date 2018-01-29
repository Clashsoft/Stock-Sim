package com.clashsoft.stocksim.local;

import com.clashsoft.stocksim.data.Transaction;
import com.clashsoft.stocksim.model.Leaderboard;
import com.clashsoft.stocksim.model.Player;
import com.clashsoft.stocksim.model.Stock;
import com.clashsoft.stocksim.model.StockSim;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
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

	public void load(File data) throws IOException
	{
		this.players.clear();
		this.stocks.clear();
		this.transactions.clear();

		final File players = new File(data, "players.csv");
		final File stocks = new File(data, "stocks.csv");
		final File transactions = new File(data, "transactions.csv");

		Files.lines(players.toPath()).forEach(line -> this.addPlayer(LocalPlayer.parseCSV(this, line)));

		Files.lines(stocks.toPath()).forEach(line -> this.addStock(LocalStock.parseCSV(this, line)));

		Files.lines(transactions.toPath()).forEach(line -> this.addTransaction(Transaction.parseCSV(this, line)));
	}

	public void save(File data) throws IOException
	{
		data.mkdirs();

		final File players = new File(data, "players.csv");
		final File stocks = new File(data, "stocks.csv");
		final File transactions = new File(data, "transactions.csv");

		Files.write(players.toPath(), (Iterable<String>) this.players.stream().map(p -> (LocalPlayer) p)
		                                                             .map(LocalPlayer::toCSV)::iterator);
		Files.write(stocks.toPath(),
		            (Iterable<String>) this.stocks.stream().map(p -> (LocalStock) p).map(LocalStock::toCSV)::iterator);
		Files.write(transactions.toPath(),
		            (Iterable<String>) this.transactions.stream().map(Transaction::toCSV)::iterator);
	}
}
