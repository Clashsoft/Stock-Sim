package com.clashsoft.stocksim.local;

import com.clashsoft.stocksim.data.Transaction;
import com.clashsoft.stocksim.model.Stock;
import com.clashsoft.stocksim.model.StockSim;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.clashsoft.stocksim.persistence.Util.readUUID;
import static com.clashsoft.stocksim.persistence.Util.writeUUID;

public class LocalStock implements Stock
{
	private final StockSim sim;
	private final UUID     id;

	private String name;
	private String symbol;

	private long supply;

	// for all t: this == t.stock
	private final List<Transaction> transactions = new ArrayList<>();

	public LocalStock(StockSim sim, UUID id)
	{
		this.sim = sim;
		this.id = id;
	}

	public LocalStock(StockSim sim, UUID id, String name, String symbol, long supply)
	{
		this.sim = sim;
		this.id = id;
		this.name = name;
		this.symbol = symbol;
		this.supply = supply;
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

	@Override
	public String getSymbol()
	{
		return this.symbol;
	}

	@Override
	public long getSupply()
	{
		return this.supply;
	}

	@Override
	public long getPrice()
	{
		return this.getPrice(this.getTransactions());
	}

	@Override
	public long getPrice(long time)
	{
		return this.getPrice(this.getTransactions(0, time));
	}

	private long getPrice(List<Transaction> transactions)
	{
		return transactions.isEmpty() ? 0L : transactions.get(transactions.size() - 1).getPrice();
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
	}

	public String toCSV()
	{
		return this.id + "," + this.symbol + "," + this.name + "," + this.supply;
	}

	public void write(DataOutput output) throws IOException
	{
		writeUUID(output, this.id);
		output.writeUTF(this.symbol);
		output.writeUTF(this.name);
		output.writeLong(this.supply);
	}

	public static LocalStock parseCSV(LocalStockSim sim, String csv)
	{
		final String[] array = csv.split(",");
		int i = 0;

		final UUID id = UUID.fromString(array[i++]);
		final String symbol = array[i++];
		final String name = array[i++];
		final long supply = Long.parseLong(array[i++]);

		return new LocalStock(sim, id, name, symbol, supply);
	}

	public static LocalStock read(LocalStockSim sim, DataInput input) throws IOException
	{
		final UUID id = readUUID(input);
		final String symbol = input.readUTF();
		final String name = input.readUTF();
		final long supply = input.readLong();

		return new LocalStock(sim, id, name, symbol, supply);
	}
}
