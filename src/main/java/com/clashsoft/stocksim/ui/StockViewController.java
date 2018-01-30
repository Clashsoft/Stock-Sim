package com.clashsoft.stocksim.ui;

import com.clashsoft.stocksim.Main;
import com.clashsoft.stocksim.data.Period;
import com.clashsoft.stocksim.model.Stock;
import com.clashsoft.stocksim.model.StockSim;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;

import java.io.IOException;

public class StockViewController
{
	@FXML
	public Label stockSymbolLabel;
	@FXML
	public Label stockNameLabel;

	@FXML
	public Label stockPriceDollarLabel;
	@FXML
	public Label stockPriceCentLabel;

	@FXML
	public Label absChangeDollarLabel;
	@FXML
	public Label absChangeCentLabel;
	@FXML
	public Label relChangeLabel;

	@FXML
	public ToggleGroup  periodToggle;
	@FXML
	public ToggleButton hourToggleButton;
	@FXML
	public ToggleButton dayToggleButton;
	@FXML
	public ToggleButton monthToggleButton;
	@FXML
	public ToggleButton yearToggleButton;
	@FXML
	public ToggleButton allTimeToggleButton;

	@FXML
	public LineChart stockPriceChart;

	private Stock stock;

	public StockViewController()
	{
	}

	public StockViewController(Stock stock)
	{
		this.setStock(stock);
	}

	public Stock getStock()
	{
		return this.stock;
	}

	public void setStock(Stock stock)
	{
		this.stock = stock;
		this.updateDisplay();
	}

	public static void open(Stock stock)
	{
		try
		{
			final FXMLLoader loader = new FXMLLoader(Main.class.getResource("ui/StockView.fxml"));
			final Parent parent = loader.load();
			final StockViewController controller = loader.getController();
			final Stage stage = new Stage();

			controller.setStock(stock);

			stage.setScene(new Scene(parent));
			stage.setTitle("Stock – " + stock.getName());
			stage.show();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	@FXML
	public void initialize()
	{
		this.hourToggleButton.setUserData(Period.HOUR);
		this.dayToggleButton.setUserData(Period.DAY);
		this.monthToggleButton.setUserData(Period.MONTH);
		this.yearToggleButton.setUserData(Period.YEAR);
		this.allTimeToggleButton.setUserData(Period.ALL_TIME);

		this.periodToggle.selectedToggleProperty().addListener((ob, o, n) -> this.updateDisplay());
	}

	public void updateDisplay()
	{
		final StockSim stockSim = this.stock.getStockSim();
		final long time = stockSim.getTime();
		final long netWorth = this.stock.getPrice(time);

		this.displaySymbol(this.stock.getSymbol());
		this.displayName(this.stock.getName());
		this.displayNetWorth(netWorth);

		final Period period = (Period) this.periodToggle.getSelectedToggle().getUserData();

		long startTime = time - period.length;
		if (startTime < 0)
		{
			startTime = 0;
		}

		final long oldNetWorth = this.stock.getPrice(startTime);
		this.displayValueChange(netWorth, oldNetWorth);
	}

	public void displaySymbol(String symbol)
	{
		this.stockSymbolLabel.setText("$" + symbol);
	}

	public void displayName(String name)
	{
		this.stockNameLabel.setText(name);
	}

	public void displayNetWorth(long amount)
	{
		long dollars = amount / 100;
		long cents = amount % 100;

		this.stockPriceDollarLabel.setText(String.format("$ %,d", dollars));
		this.stockPriceCentLabel.setText(String.format(".%2d", cents));
	}

	private void displayValueChange(long netWorth, long oldNetWorth)
	{
		this.displayAbsChange(netWorth - oldNetWorth);

		final double relChange = (double) netWorth / (double) oldNetWorth - 1;
		this.displayRelChange(relChange);
	}

	public void displayRelChange(double amount)
	{
		char sign;
		if (amount < 0)
		{
			amount = -amount;
			sign = '-';
		}
		else
		{
			sign = '+';
		}

		this.relChangeLabel.setText(String.format("%c %.2f %%", sign, amount * 100));
	}

	public void displayAbsChange(long amount)
	{
		char sign;
		if (amount < 0)
		{
			amount = -amount;
			sign = '-';
		}
		else
		{
			sign = '+';
		}

		long dollars = amount / 100;
		long cents = amount % 100;
		this.absChangeDollarLabel.setText(String.format("%c $ %,d", sign, dollars));
		this.absChangeCentLabel.setText(String.format(".%2d", cents));
	}
}
