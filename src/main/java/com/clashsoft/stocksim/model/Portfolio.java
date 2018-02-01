package com.clashsoft.stocksim.model;

import com.clashsoft.stocksim.data.StockAmount;

import java.util.Collection;

public interface Portfolio
{
	long getCash();

	Collection<Stock> getStocks();

	Collection<StockAmount> getStockAmounts();

	long getStockAmount(Stock stock);

	long getStocksValue(long time);

	long getNetWorth(long time);
}
