package com.clashsoft.stocksim.ui;

import com.clashsoft.stocksim.Main;
import com.clashsoft.stocksim.data.Period;
import com.clashsoft.stocksim.model.Stock;
import com.clashsoft.stocksim.model.StockSim;
import com.clashsoft.stocksim.ui.converter.ShortDollarConverter;
import com.clashsoft.stocksim.ui.util.TextFields;
import com.clashsoft.stocksim.ui.converter.TimeConverter;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.Axis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class StockViewController
{
	private static final int PRICE_CHART_COUNT = 20;

	@FXML
	public Label stockSymbolLabel;
	@FXML
	public Label stockNameLabel;

	@FXML
	public Label stockPriceDollarLabel;
	@FXML
	public Label stockPriceCentLabel;

	@FXML
	public Label absChangeLabel;
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
	public LineChart<Long, Long> stockPriceChart;

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
		this.updateAll();
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
			stage.setAlwaysOnTop(true);
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
		this.stockPriceChart.getData().add(new XYChart.Series<>());
		((NumberAxis) (Axis) this.stockPriceChart.getXAxis()).setTickLabelFormatter(new TimeConverter());
		((NumberAxis) (Axis) this.stockPriceChart.getYAxis()).setTickLabelFormatter(new ShortDollarConverter());

		this.hourToggleButton.setUserData(Period.HOUR);
		this.dayToggleButton.setUserData(Period.DAY);
		this.monthToggleButton.setUserData(Period.MONTH);
		this.yearToggleButton.setUserData(Period.YEAR);
		this.allTimeToggleButton.setUserData(Period.ALL_TIME);

		this.periodToggle.selectedToggleProperty().addListener((ob, o, n) -> this.updatePeriod());
	}

	public void updateAll()
	{
		this.displaySymbol(this.stock.getSymbol());
		this.displayName(this.stock.getName());

		this.updatePeriod();
	}

	private void updatePeriod()
	{
		final StockSim stockSim = this.stock.getStockSim();
		final long time = stockSim.getTime();
		final long price = this.stock.getPrice();

		this.displayPrice(price);

		final Period period = (Period) this.periodToggle.getSelectedToggle().getUserData();

		long startTime = time - period.length;
		if (startTime < 0)
		{
			startTime = 0;
		}

		final long oldNetWorth = this.stock.getPrice(startTime);
		this.displayValueChange(price, oldNetWorth);

		this.updateChart(startTime, time);
	}

	private void displaySymbol(String symbol)
	{
		this.stockSymbolLabel.setText("$" + symbol);
	}

	private void displayName(String name)
	{
		this.stockNameLabel.setText(name);
	}

	private void displayPrice(long amount)
	{
		TextFields.displayNetWorth(amount, this.stockPriceDollarLabel, this.stockPriceCentLabel);
	}

	private void displayValueChange(long netWorth, long oldNetWorth)
	{
		TextFields.displayAbsChange(netWorth - oldNetWorth, this.absChangeLabel);

		final double relChange = (double) netWorth / (double) oldNetWorth - 1;
		TextFields.displayRelChange(relChange, this.relChangeLabel);
	}

	private void updateChart(long startTime, long endTime)
	{
		final List<XYChart.Series<Long, Long>> data = this.stockPriceChart.getData();
		final XYChart.Series<Long, Long> series = data.get(0);
		final List<XYChart.Data<Long, Long>> seriesData = series.getData();
		while (seriesData.size() <= PRICE_CHART_COUNT)
		{
			seriesData.add(new XYChart.Data<>());
		}

		for (int i = 0; i <= PRICE_CHART_COUNT; i++)
		{
			final long time = startTime + (endTime - startTime) / PRICE_CHART_COUNT * i;
			final long price = this.stock.getPrice(time);
			final XYChart.Data<Long, Long> e = seriesData.get(i);
			e.setXValue(time);
			e.setYValue(price);
		}
	}
}
