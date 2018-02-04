package com.clashsoft.stocksim.strategy;

import com.clashsoft.stocksim.data.Order;
import com.clashsoft.stocksim.model.Player;
import com.clashsoft.stocksim.model.StockSim;

import java.io.DataInput;
import java.io.DataOutput;
import java.util.function.Consumer;

public class NoStrategy implements Strategy
{
	@Override
	public void makeOrder(StockSim sim, Player player, Consumer<Order> orders)
	{
	}

	@Override
	public void writeData(DataOutput output)
	{
	}

	@Override
	public void readData(DataInput input)
	{
	}
}
