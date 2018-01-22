package com.clashsoft.stocksim.data;

import com.clashsoft.stocksim.model.Stock;

public class StockAmount
{
	private final Stock stock;
	private final long  amount;

	public StockAmount(Stock stock, long amount)
	{
		this.stock = stock;
		this.amount = amount;
	}

	public Stock getStock()
	{
		return this.stock;
	}

	public long getAmount()
	{
		return this.amount;
	}

	public long getValue()
	{
		return this.amount * this.stock.getPrice();
	}

	public long getValue(long time)
	{
		return this.amount * this.stock.getPrice(time);
	}
}
