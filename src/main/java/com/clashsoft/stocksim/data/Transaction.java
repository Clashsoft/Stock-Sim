package com.clashsoft.stocksim.data;

import com.clashsoft.stocksim.model.Player;
import com.clashsoft.stocksim.model.Stock;
import com.clashsoft.stocksim.model.StockSim;
import com.clashsoft.stocksim.ui.converter.PriceFormatter;
import com.clashsoft.stocksim.ui.converter.TimeConverter;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static com.clashsoft.stocksim.persistence.Util.readUUID;
import static com.clashsoft.stocksim.persistence.Util.writeUUID;

public class Transaction
{
	private static final UUID DEFAULT_UUID = new UUID(0, 0);

	private final UUID id;
	private final long time;

	private final Stock stock;
	private final long  amount;

	private final long price;

	private final Player seller;
	private final Player buyer;

	public Transaction(UUID id, long time, Stock stock, long amount, long price, Player seller, Player buyer)
	{
		this.id = id;
		this.time = time;
		this.stock = stock;
		this.amount = amount;
		this.price = price;
		this.seller = seller;
		this.buyer = buyer;
	}

	public UUID getID()
	{
		return this.id;
	}

	public long getTime()
	{
		return this.time;
	}

	public Stock getStock()
	{
		return this.stock;
	}

	public long getAmount()
	{
		return this.amount;
	}

	public StockAmount getStockAmount()
	{
		return new StockAmount(this.stock, this.amount);
	}

	public long getPrice()
	{
		return this.price;
	}

	public long getTotal()
	{
		return this.price * this.amount;
	}

	public Player getSeller()
	{
		return this.seller;
	}

	public Player getBuyer()
	{
		return this.buyer;
	}

	public static List<Transaction> filter(List<Transaction> transactions, long start, long end)
	{
		if (transactions.isEmpty())
		{
			return transactions;
		}

		final int startIndex = indexOf(transactions, start);
		final int endIndex = indexOf(transactions, end);

		final List<Transaction> result = transactions.subList(startIndex, endIndex);

		// assert result.stream().allMatch(t -> t.getTime() >= start && t.getTime() < end);
		// assert startIndex == 0 || transactions.get(startIndex - 1).getTime() < start;
		// assert endIndex == transactions.size() || transactions.get(endIndex).getTime() >= end;

		return result;
	}

	private static int indexOf(List<Transaction> transactions, long start)
	{
		// adapted from Arrays.binarySearch

		int low = 0;
		int high = transactions.size() - 1;

		while (low <= high)
		{
			final int mid = (low + high) >>> 1;
			final long midVal = transactions.get(mid).getTime();

			if (midVal < start)
			{
				low = mid + 1;
			}
			else if (midVal > 0)
			{
				high = mid - 1;
			}
			else
			{
				return mid; // key found
			}
		}
		return low; // key not found.
	}

	public String toCSV()
	{
		return TimeConverter.format(this.time) + "," // time
		       + this.id + "," // trx id
		       + (this.seller == null ? "" : this.seller.getName()) + "," // seller id
		       + (this.buyer == null ? "" : this.buyer.getName()) + "," // buyer id
		       + this.stock.getSymbol() + "," // stock id
		       + this.amount + "," // amount
		       + PriceFormatter.formatPrice(this.price); // price
	}

	public void write(DataOutput output) throws IOException
	{
		writeUUID(output, this.id);
		output.writeLong(this.time);
		writeUUID(output, this.seller == null ? DEFAULT_UUID : this.seller.getID());
		writeUUID(output, this.buyer == null ? DEFAULT_UUID : this.buyer.getID());
		writeUUID(output, this.stock.getID());
		output.writeLong(this.amount);
		output.writeLong(this.price);
	}

	public static Transaction parseCSV(StockSim sim, String csv)
	{
		final String[] array = csv.split(",");
		int i = 0;

		final long time = TimeConverter.parse(array[i++]);
		final UUID id = UUID.fromString(array[i++]);
		final Player seller = sim.getPlayer(array[i++]);
		final Player buyer = sim.getPlayer(array[i++]);
		final Stock stock = sim.getStock(array[i++]);
		final long amount = Long.parseLong(array[i++]);
		final long price = PriceFormatter.parsePrice(array[i++]);

		return new Transaction(id, time, stock, amount, price, seller, buyer);
	}

	public static Transaction read(StockSim sim, DataInput input) throws IOException
	{
		final UUID id = readUUID(input);
		final long time = input.readLong();
		final Player seller = sim.getPlayer(readUUID(input));
		final Player buyer = sim.getPlayer(readUUID(input));
		final Stock stock = sim.getStock(readUUID(input));
		final long amount = input.readLong();
		final long price = input.readLong();

		return new Transaction(id, time, stock, amount, price, seller, buyer);
	}
}
