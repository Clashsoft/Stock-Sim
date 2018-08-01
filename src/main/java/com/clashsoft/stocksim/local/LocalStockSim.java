package com.clashsoft.stocksim.local;

import com.clashsoft.fxcommons.data.ZipIO;
import com.clashsoft.stocksim.data.Order;
import com.clashsoft.stocksim.data.Transaction;
import com.clashsoft.stocksim.model.Leaderboard;
import com.clashsoft.stocksim.model.Player;
import com.clashsoft.stocksim.model.Stock;
import com.clashsoft.stocksim.model.StockSim;
import com.clashsoft.stocksim.strategy.CompanyStrategy;
import com.clashsoft.stocksim.strategy.PlayerStrategy;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

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

	private void readLocked(Runnable runnable)
	{
		this.readLock.lock();
		try
		{
			runnable.run();
		}
		finally
		{
			this.readLock.unlock();
		}
	}

	private <T> T readLocked(Supplier<T> supplier)
	{
		this.readLock.lock();
		try
		{
			return supplier.get();
		}
		finally
		{
			this.readLock.unlock();
		}
	}

	private void writeLocked(Runnable runnable)
	{
		this.writeLock.lock();
		try
		{
			runnable.run();
		}
		finally
		{
			this.writeLock.unlock();
		}
	}

	@Override
	public long getTime()
	{
		return this.readLocked(() -> this.time);
	}

	public void simulate()
	{
		final List<Order> orders = this.getNewOrders();

		this.writeLocked(() -> {
			this.openOrders.addAll(orders);
			this.time++;
		});

		this.processOrders();
	}

	private List<Order> getNewOrders()
	{
		final List<Order> orders = new ArrayList<>(this.players.size());

		this.readLocked(() -> {
			for (Player player : this.players)
			{
				player.makeOrder(orders::add);
			}
		});
		return orders;
	}

	private void processOrders()
	{
		final List<Order> buyOrders;
		final List<Order> sellOrders;

		this.readLock.lock();
		try
		{
			buyOrders = this.openOrders.stream().filter(this::isValidBuyOrder).sorted(Order.COMPARATOR)
			                           .collect(Collectors.toList());

			sellOrders = this.openOrders.stream().filter(this::isValidSellOrder).sorted(Order.COMPARATOR)
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
		outer:
		for (Iterator<Order> buyIterator = buyOrders.iterator(); buyIterator.hasNext(); )
		{
			final Order buyOrder = buyIterator.next();
			final long buyAmount = buyOrder.getAmount();
			final Stock stock = buyOrder.getStock();

			for (Iterator<Order> sellIterator = sellOrders.iterator(); sellIterator.hasNext(); )
			{
				final Order sellOrder = sellIterator.next();

				if (stock != sellOrder.getStock())
				{
					continue;
				}
				if (sellOrder.getPrice() > buyOrder.getPrice() || sellOrder.getPlayer() == buyOrder.getPlayer())
				{
					continue;
				}

				final long sellAmount = -sellOrder.getAmount();
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

	private boolean isValidBuyOrder(Order order)
	{
		return order.getAmount() > 0 && this.time <= order.getExpiry() && order.getPlayer().getCash() >= order
			                                                                                                 .getTotal();
	}

	private boolean isValidSellOrder(Order order)
	{
		if (order.getAmount() > 0 || this.time > order.getExpiry())
		{
			return false;
		}

		final Stock stock = order.getStock();
		final long ownedAmount = order.getPlayer().getPortfolio().getStockAmount(stock);
		final long orderedAmount = -order.getAmount();
		return ownedAmount >= orderedAmount;
	}

	@Override
	public void eachOrder(int n, Consumer<Order> action)
	{
		this.readLocked(() -> last(n, this.openOrders).forEach(action));
	}

	@Override
	public void eachTransaction(int n, Consumer<Transaction> action)
	{
		this.readLocked(() -> last(n, this.transactions).forEach(action));
	}

	@Override
	public void eachPlayer(Consumer<Player> action)
	{
		this.readLocked(() -> this.players.forEach(action));
	}

	@Override
	public Player getPlayer(String name)
	{
		return this.readLocked(() -> {
			for (Player player : this.players)
			{
				if (player.getName().equals(name))
				{
					return player;
				}
			}
			return null;
		});
	}

	@Override
	public Player getPlayer(UUID id)
	{
		return this.readLocked(() -> {
			for (Player player : this.players)
			{
				if (player.getID().equals(id))
				{
					return player;
				}
			}
			return null;
		});
	}

	@Override
	public void eachStock(Consumer<Stock> action)
	{
		this.readLocked(() -> this.stocks.forEach(action));
	}

	private static <T> List<T> last(int n, List<T> list)
	{
		if (n < 0)
		{
			return list;
		}
		return list.subList(Math.max(0, list.size() - n), list.size());
	}

	@Override
	public Stock getStock(String symbol)
	{
		return this.readLocked(() -> {
			for (Stock stock : this.stocks)
			{
				if (stock.getSymbol().equals(symbol))
				{
					return stock;
				}
			}
			return null;
		});
	}

	@Override
	public Stock getStock(UUID id)
	{
		return this.readLocked(() -> {
			for (Stock stock : this.stocks)
			{
				if (stock.getID().equals(id))
				{
					return stock;
				}
			}
			return null;
		});
	}

	@Override
	public Leaderboard getLeaderboard()
	{
		return this.leaderboard;
	}

	@Override
	public void addTransaction(Transaction transaction)
	{
		this.writeLocked(() -> {
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
		});
	}

	@Override
	public void addPlayer(Player player)
	{
		this.writeLocked(() -> this.players.add(player));
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
		this.writeLocked(() -> this.stocks.add(stock));
	}

	@Override
	public Stock createStock(String name, String symbol, long amount, long price)
	{
		final LocalStock stock = new LocalStock(this, UUID.randomUUID(), name, symbol, amount);
		final String playerName = "$" + symbol + " - " + name;
		final Player player = new LocalPlayer(this, UUID.randomUUID(), playerName, amount * price,
		                                      new CompanyStrategy());

		this.addPlayer(player);
		this.addStock(stock);
		this.addTransaction(new Transaction(UUID.randomUUID(), this.time, stock, amount, price, null, player));

		return stock;
	}

	public void load(File data) throws IOException
	{
		this.writeLock.lock();
		try (ZipFile zipFile = new ZipFile(data))
		{
			ZipIO.readData(zipFile, "sim.dat", input -> {
				this.time = input.readLong();
			});

			ZipIO.readData(zipFile, "players.dat", input -> {
				final int n = input.readInt();
				for (int i = 0; i < n; i++)
				{
					this.addPlayer(LocalPlayer.read(this, input));
				}
			});

			ZipIO.readData(zipFile, "stocks.dat", input -> {
				final int n = input.readInt();
				for (int i = 0; i < n; i++)
				{
					this.addStock(LocalStock.read(this, input));
				}
			});

			ZipIO.readData(zipFile, "transactions.dat", input -> {
				final int n = input.readInt();
				for (int i = 0; i < n; i++)
				{
					this.addTransaction(Transaction.read(this, input));
				}
			});

			ZipIO.readData(zipFile, "open_orders.dat", input -> {
				final int n = input.readInt();
				for (int i = 0; i < n; i++)
				{
					this.openOrders.add(Order.read(this, input));
				}
			});
		}
		finally
		{
			this.writeLock.unlock();
		}
	}

	public void save(File data) throws IOException
	{
		this.readLock.lock();

		try (ZipOutputStream zip = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(data)));
		     DataOutputStream output = new DataOutputStream(zip))
		{
			zip.putNextEntry(new ZipEntry("sim.dat"));

			output.writeLong(this.time);

			zip.putNextEntry(new ZipEntry("players.dat"));

			output.writeInt(this.players.size());
			for (Player player : this.players)
			{
				((LocalPlayer) player).write(output);
			}

			zip.putNextEntry(new ZipEntry("stocks.dat"));

			output.writeInt(this.stocks.size());
			for (Stock stock : this.stocks)
			{
				((LocalStock) stock).write(output);
			}

			zip.putNextEntry(new ZipEntry("transactions.dat"));

			output.writeInt(this.transactions.size());
			for (Transaction trx : this.transactions)
			{
				trx.write(output);
			}

			zip.putNextEntry(new ZipEntry("open_orders.dat"));
			output.writeInt(this.openOrders.size());
			for (Order order : this.openOrders)
			{
				order.write(output);
			}

			zip.closeEntry();
		}
		finally
		{
			this.readLock.unlock();
		}
	}
}
