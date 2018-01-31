package com.clashsoft.stocksim.local;

import com.clashsoft.stocksim.data.Order;
import com.clashsoft.stocksim.data.StockAmount;
import com.clashsoft.stocksim.data.Transaction;
import com.clashsoft.stocksim.model.Leaderboard;
import com.clashsoft.stocksim.model.Player;
import com.clashsoft.stocksim.model.Stock;
import com.clashsoft.stocksim.model.StockSim;
import com.clashsoft.stocksim.strategy.CompanyStrategy;
import com.clashsoft.stocksim.strategy.PlayerStrategy;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class LocalStockSim implements StockSim
{
	private static final int MAX_ORDERS = 1 << 8;

	private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
	private final Lock          readLock      = this.readWriteLock.readLock();
	private final Lock          writeLock     = this.readWriteLock.writeLock();

	private long time;

	private List<Transaction> transactions = new ArrayList<>();
	private List<Player>      players      = new ArrayList<>();
	private List<Stock>       stocks       = new ArrayList<>();
	private List<Order>       openOrders   = new ArrayList<>();
	private Leaderboard       leaderboard  = new LocalLeaderboard(this);

	@Override
	public long getTime()
	{
		this.readLock.lock();
		try
		{
			return this.time;
		}
		finally
		{
			this.readLock.unlock();
		}
	}

	public void simulate()
	{
		final List<Order> orders = this.getNewOrders();

		this.writeLock.lock();
		try
		{
			this.openOrders.addAll(orders);
			this.time++;
		}
		finally
		{
			this.writeLock.unlock();
		}

		this.processOrders();
	}

	private List<Order> getNewOrders()
	{
		final List<Order> orders = new ArrayList<>(this.players.size());

		this.readLock.lock();
		try
		{
			for (Player player : this.players)
			{
				final Order order = player.makeOrder();
				if (order != null)
				{
					orders.add(order);
				}
			}
		}
		finally
		{
			this.readLock.unlock();
		}
		return orders;
	}

	private void processOrders()
	{
		final List<Order> buyOrders;
		final List<Order> sellOrders;

		this.readLock.lock();
		try
		{
			buyOrders = this.openOrders.stream().filter(e -> e.getAmount() > 0).sorted(Order.COMPARATOR)
			                           .collect(Collectors.toList());

			sellOrders = this.openOrders.stream().filter(e -> e.getAmount() < 0).sorted(Order.COMPARATOR)
			                            .collect(Collectors.toList());
		}
		finally
		{
			this.readLock.unlock();
		}

		final List<Order> newOrders = new ArrayList<>();
		final List<Transaction> newTransactions = new ArrayList<>();

		this.processOrders(buyOrders, sellOrders, newOrders, newTransactions);

		this.writeLock.lock();
		try
		{
			this.openOrders.clear();
			this.openOrders.addAll(newOrders);
			for (Transaction transaction : newTransactions)
			{
				this.addTransaction(transaction);
			}
		}
		finally
		{
			this.writeLock.unlock();
		}
	}

	private void processOrders(List<Order> buyOrders, List<Order> sellOrders, List<Order> newOrders,
		List<Transaction> newTransactions)
	{
		// remove buy orders the player can't afford
		buyOrders.removeIf(order -> order.getPlayer().getCash() < order.getTotal());

		// remove sell orders where the player does not own enough of the stock
		sellOrders.removeIf(order -> {
			final Stock stock = order.getStock();
			final List<StockAmount> stocks = order.getPlayer().getStocks();
			final StockAmount amount = stocks.stream().filter(stockAmount -> stockAmount.getStock() == stock).findAny()
			                                 .orElse(null);
			return amount == null || amount.getAmount() < -order.getAmount();
		});

		outer:
		for (Iterator<Order> buyIterator = buyOrders.iterator(); buyIterator.hasNext(); )
		{
			final Order buyOrder = buyIterator.next();
			final long buyAmount = buyOrder.getAmount();
			final Stock stock = buyOrder.getStock();

			for (Iterator<Order> sellIterator = sellOrders.iterator(); sellIterator.hasNext(); )
			{
				final Order sellOrder = sellIterator.next();
				long sellAmount = -sellOrder.getAmount();

				if (stock != sellOrder.getStock())
				{
					continue;
				}
				if (sellOrder.getPrice() > buyOrder.getPrice() || sellOrder.getPlayer() == buyOrder.getPlayer())
				{
					continue;
				}

				final long amount = Math.min(buyAmount, sellAmount);
				final Transaction transaction = new Transaction(UUID.randomUUID(), this.time, stock, amount,
				                                                sellOrder.getPrice(), sellOrder.getPlayer(),
				                                                buyOrder.getPlayer());
				newTransactions.add(transaction);

				final Order newSell = sellOrder.split(amount);
				if (newSell != null)
				{
					newOrders.add(newSell);
				}

				final Order newBuy = buyOrder.split(amount);
				if (newBuy != null)
				{
					newOrders.add(newBuy);
				}

				buyIterator.remove();
				sellIterator.remove();
				continue outer;
			}
		}

		newOrders.addAll(buyOrders);
		newOrders.addAll(sellOrders);
	}

	@Override
	public List<Order> getOrders()
	{
		return this.openOrders;
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
		this.readLock.lock();
		try
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
		finally
		{
			this.readLock.unlock();
		}
	}

	@Override
	public Player getPlayer(UUID id)
	{
		this.readLock.lock();
		try
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
		finally
		{
			this.readLock.unlock();
		}
	}

	@Override
	public List<Stock> getStocks()
	{
		return this.stocks;
	}

	@Override
	public Stock getStock(String symbol)
	{
		this.readLock.lock();
		try
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
		finally
		{
			this.readLock.unlock();
		}
	}

	@Override
	public Stock getStock(UUID id)
	{
		this.readLock.lock();
		try
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
		finally
		{
			this.readLock.unlock();
		}
	}

	@Override
	public Leaderboard getLeaderboard()
	{
		return this.leaderboard;
	}

	@Override
	public void addTransaction(Transaction transaction)
	{
		this.writeLock.lock();
		try
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
		finally
		{
			this.writeLock.unlock();
		}
	}

	@Override
	public void addPlayer(Player player)
	{
		this.writeLock.lock();
		try
		{
			this.players.add(player);
		}
		finally
		{
			this.writeLock.unlock();
		}
	}

	@Override
	public Player createPlayer(String name, long cash)
	{
		final LocalPlayer player = new LocalPlayer(this, UUID.randomUUID(), name, cash, new PlayerStrategy());
		this.addPlayer(player);
		return player;
	}

	@Override
	public void addStock(Stock stock)
	{
		this.writeLock.lock();
		try
		{
			this.stocks.add(stock);
		}
		finally
		{
			this.writeLock.unlock();
		}
	}

	@Override
	public Stock createStock(String name, String symbol, long amount, long price)
	{
		final LocalStock stock = new LocalStock(this, UUID.randomUUID(), name, symbol, amount);
		final Player player = new LocalPlayer(this, UUID.randomUUID(), name, amount * price, new CompanyStrategy());

		this.addPlayer(player);
		this.addStock(stock);
		this.addTransaction(new Transaction(UUID.randomUUID(), this.time, stock, amount, price, null, player));

		return stock;
	}

	public void load(File data) throws IOException
	{
		this.writeLock.lock();
		try
		{
			this.time = 0;
			this.players.clear();
			this.stocks.clear();
			this.transactions.clear();
			this.openOrders.clear();

			final File info = new File(data, "sim.txt");
			final File players = new File(data, "players.csv");
			final File stocks = new File(data, "stocks.csv");
			final File transactions = new File(data, "transactions.csv");
			final File openOrders = new File(data, "open_orders.csv");

			Files.lines(info.toPath()).forEach(line -> {
				if (line.startsWith("time="))
				{
					this.time = Long.parseLong(line.substring(5));
				}
			});

			Files.lines(players.toPath()).forEach(line -> this.addPlayer(LocalPlayer.parseCSV(this, line)));

			Files.lines(stocks.toPath()).forEach(line -> this.addStock(LocalStock.parseCSV(this, line)));

			Files.lines(transactions.toPath()).forEach(line -> this.addTransaction(Transaction.parseCSV(this, line)));

			Files.lines(openOrders.toPath()).forEach(line -> this.openOrders.add(Order.parseCSV(this, line)));
		}
		finally
		{
			this.writeLock.unlock();
		}
	}

	public void save(File data) throws IOException
	{
		this.readLock.lock();

		try
		{
			data.mkdirs();

			final File sim = new File(data, "sim.txt");
			final List<String> infoLines = Collections.singletonList("time=" + this.time);
			Files.write(sim.toPath(), infoLines);

			final File players = new File(data, "players.csv");
			final File stocks = new File(data, "stocks.csv");
			final File transactions = new File(data, "transactions.csv");
			final File openOrders = new File(data, "open_orders.csv");

			Files.write(players.toPath(), (Iterable<String>) this.players.stream().map(p -> (LocalPlayer) p)
			                                                             .map(LocalPlayer::toCSV)::iterator);

			Files.write(stocks.toPath(), (Iterable<String>) this.stocks.stream().map(p -> (LocalStock) p)
			                                                           .map(LocalStock::toCSV)::iterator);

			Files.write(transactions.toPath(),
			            (Iterable<String>) this.transactions.stream().map(Transaction::toCSV)::iterator);

			Files.write(openOrders.toPath(), (Iterable<String>) this.openOrders.stream().map(Order::toCSV)::iterator);
		}
		finally
		{
			this.readLock.unlock();
		}
	}
}
