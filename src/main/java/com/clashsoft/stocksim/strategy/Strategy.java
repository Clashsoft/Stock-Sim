package com.clashsoft.stocksim.strategy;

import com.clashsoft.stocksim.data.Order;
import com.clashsoft.stocksim.model.Player;
import com.clashsoft.stocksim.model.StockSim;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.function.Consumer;

public interface Strategy
{
	void makeOrder(StockSim sim, Player player, Consumer<Order> orders);

	static String getName(Strategy strategy)
	{
		return strategy.getClass().getSimpleName();
	}

	static Strategy fromName(String name)
	{
		Strategy strategy;
		try
		{
			final Class<?> type = Class.forName(Strategy.class.getPackage().getName() + "." + name);
			strategy = (Strategy) type.newInstance();
		}
		catch (Exception e)
		{
			strategy = null;
		}
		return strategy;
	}

	void writeData(DataOutput output) throws IOException;

	void readData(DataInput input) throws IOException;

	static void write(DataOutput output, Strategy strategy) throws IOException
	{
		output.writeUTF(strategy.getClass().getName());
		strategy.writeData(output);
	}

	static Strategy read(DataInput input) throws IOException
	{
		final String type = input.readUTF();
		Strategy strategy;

		try
		{
			strategy = (Strategy) Class.forName(type).newInstance();
		}
		catch (ClassNotFoundException | IllegalAccessException | InstantiationException | ClassCastException e)
		{
			return null;
		}

		strategy.readData(input);
		return strategy;
	}
}
