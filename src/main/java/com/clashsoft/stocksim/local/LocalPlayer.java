package com.clashsoft.stocksim.local;

import com.clashsoft.stocksim.data.Order;
import com.clashsoft.stocksim.data.Transaction;
import com.clashsoft.stocksim.model.Player;
import com.clashsoft.stocksim.model.Portfolio;
import com.clashsoft.stocksim.model.StockSim;
import com.clashsoft.stocksim.strategy.Strategy;
import com.clashsoft.stocksim.ui.converter.PriceFormatter;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import static com.clashsoft.fxcommons.data.DataIO.readUUID;
import static com.clashsoft.fxcommons.data.DataIO.writeUUID;

public class LocalPlayer implements Player
{
	private final LocalStockSim sim;
	private final UUID          id;

	private String name;

	private long startCash;

	private Strategy strategy;

	// for all t: this == t.buyer || this == t.seller
	private final List<Transaction> transactions = new ArrayList<>();

	private LocalPortfolio portfolio;

	public LocalPlayer(LocalStockSim sim, UUID id, String name, long startCash, Strategy strategy)
	{
		this.sim = sim;
		this.id = id;
		this.name = name;
		this.startCash = startCash;
		this.portfolio = new LocalPortfolio(this);
		this.strategy = strategy;
	}

	@Override
	public StockSim getStockSim()
	{
		return this.sim;
	}

	@Override
	public UUID getID()
	{
		return this.id;
	}

	@Override
	public String getName()
	{
		return this.name;
	}

	public long getStartCash()
	{
		return this.startCash;
	}

	@Override
	public List<Transaction> getTransactions()
	{
		return this.transactions;
	}

	@Override
	public List<Transaction> getTransactions(long start, long end)
	{
		return Transaction.filter(this.transactions, start, end);
	}

	@Override
	public void addTransaction(Transaction transaction)
	{
		this.transactions.add(transaction);
		this.portfolio.addTransaction(transaction);
	}

	@Override
	public void makeOrder(Consumer<Order> orders)
	{
		if (this.strategy != null)
		{
			this.strategy.makeOrder(this.sim, this, orders);
		}
	}

	@Override
	public Portfolio getPortfolio()
	{
		return this.portfolio;
	}

	@Override
	public Portfolio getPortfolio(long time)
	{
		final LocalPortfolio portfolio = new LocalPortfolio(this);
		for (Transaction trx : this.getTransactions(0, time))
		{
			portfolio.addTransaction(trx);
		}
		return portfolio;
	}

	public String toCSV()
	{
		return this.id + "," // id
		       + this.name + "," // name
		       + PriceFormatter.formatPrice(this.startCash) + "," // start cash
		       + (this.strategy == null ? "" : this.strategy.getClass().getSimpleName()); // strategy
	}

	public void write(DataOutput output) throws IOException
	{
		writeUUID(output, this.id);
		output.writeUTF(this.name);
		output.writeLong(this.startCash);
		Strategy.write(output, this.strategy);
	}

	public static LocalPlayer parseCSV(LocalStockSim sim, String csv)
	{
		final String[] array = csv.split(",");
		int i = 0;

		final UUID id = UUID.fromString(array[i++]);
		final String name = array[i++];
		final long startCash = PriceFormatter.parsePrice(array[i++]);
		final Strategy strategy = Strategy.fromName(array[i++]);

		return new LocalPlayer(sim, id, name, startCash, strategy);
	}

	public static LocalPlayer read(LocalStockSim sim, DataInput input) throws IOException
	{
		final UUID id = readUUID(input);
		final String name = input.readUTF();
		final long startCash = input.readLong();
		final Strategy strategy = Strategy.read(input);

		return new LocalPlayer(sim, id, name, startCash, strategy);
	}
}
