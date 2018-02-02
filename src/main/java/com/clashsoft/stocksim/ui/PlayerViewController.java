package com.clashsoft.stocksim.ui;

import com.clashsoft.stocksim.Main;
import com.clashsoft.stocksim.data.Period;
import com.clashsoft.stocksim.data.StockAmount;
import com.clashsoft.stocksim.local.LocalPortfolio;
import com.clashsoft.stocksim.model.Leaderboard;
import com.clashsoft.stocksim.model.Player;
import com.clashsoft.stocksim.model.Stock;
import com.clashsoft.stocksim.model.StockSim;
import com.clashsoft.stocksim.ui.converter.ShortDollarConverter;
import com.clashsoft.stocksim.ui.converter.TimeConverter;
import com.clashsoft.stocksim.ui.util.TextFields;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

import static com.clashsoft.stocksim.ui.converter.PriceFormatter.formatAmount;
import static com.clashsoft.stocksim.ui.converter.PriceFormatter.formatDollarCents;

public class PlayerViewController
{
	private static final int NET_WORTH_CHART_COUNT = 20;
	@FXML
	public Label playerNameLabel;

	@FXML
	public Label netWorthDollarLabel;
	@FXML
	public Label netWorthCentLabel;

	@FXML
	public Label absChangeLabel;
	@FXML
	public Label relChangeLabel;

	@FXML
	public Label leaderboardPositionLabel;
	@FXML
	public Label leaderboardChangeLabel;

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
	public LineChart<Long, Long> netWorthChart;
	@FXML
	public PieChart              portfolioChart;

	private Player player;

	public PlayerViewController()
	{
	}

	public PlayerViewController(Player player)
	{
		this.setPlayer(player);
	}

	public Player getPlayer()
	{
		return this.player;
	}

	public void setPlayer(Player player)
	{
		this.player = player;
		this.updateAll();
	}

	public static void open(Player player)
	{
		try
		{
			final FXMLLoader loader = new FXMLLoader(Main.class.getResource("ui/PlayerView.fxml"));
			final Parent parent = loader.load();
			final PlayerViewController controller = loader.getController();
			final Stage stage = new Stage();

			controller.setPlayer(player);

			stage.setScene(new Scene(parent));
			stage.setTitle("Player – " + player.getName());
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
		this.netWorthChart.getData().add(new XYChart.Series<>());
		((NumberAxis) (Axis) this.netWorthChart.getXAxis()).setTickLabelFormatter(new TimeConverter());
		((NumberAxis) (Axis) this.netWorthChart.getYAxis()).setTickLabelFormatter(new ShortDollarConverter());

		this.hourToggleButton.setUserData(Period.HOUR);
		this.dayToggleButton.setUserData(Period.DAY);
		this.monthToggleButton.setUserData(Period.MONTH);
		this.yearToggleButton.setUserData(Period.YEAR);
		this.allTimeToggleButton.setUserData(Period.ALL_TIME);

		this.periodToggle.selectedToggleProperty().addListener((ob, o, n) -> this.updatePeriod());
	}

	public void updateAll()
	{
		this.displayName(this.player.getName());

		this.updatePeriod();

		this.updatePortfolio();
	}

	private void updatePeriod()
	{
		final StockSim stockSim = this.player.getStockSim();
		final long time = stockSim.getTime();
		final long netWorth = this.player.getNetWorth();

		this.displayNetWorth(netWorth);

		final Period period = (Period) this.periodToggle.getSelectedToggle().getUserData();

		long startTime = time - period.length;
		if (startTime < 0)
		{
			startTime = 0;
		}

		final long oldNetWorth = this.player.getNetWorth(startTime);
		this.displayNetWorthChange(netWorth, oldNetWorth);

		final Leaderboard leaderboard = stockSim.getLeaderboard();
		long position = leaderboard.getPosition(this.player, time);
		long oldPosition = leaderboard.getPosition(this.player, startTime);

		this.displayLeaderboardPosition(position);
		this.displayLeaderboardChange(position - oldPosition);

		this.updateChart(startTime, time);
	}

	private void updateChart(long startTime, long endTime)
	{
		final List<XYChart.Series<Long, Long>> data = this.netWorthChart.getData();
		final XYChart.Series<Long, Long> series = data.get(0);
		final List<XYChart.Data<Long, Long>> seriesData = series.getData();
		while (seriesData.size() <= NET_WORTH_CHART_COUNT)
		{
			seriesData.add(new XYChart.Data<>());
		}

		final LocalPortfolio portfolio = new LocalPortfolio(this.player.getPortfolio(startTime));

		long lastTime = startTime;
		for (int i = 0; i <= NET_WORTH_CHART_COUNT; i++)
		{
			final long time = startTime + (endTime - startTime) / NET_WORTH_CHART_COUNT * i;
			this.player.getTransactions(lastTime, time).forEach(portfolio::addTransaction);
			final long netWorth = portfolio.getNetWorth(time);

			final XYChart.Data<Long, Long> elem = seriesData.get(i);
			elem.setXValue(time);
			elem.setYValue(netWorth);

			lastTime = time;
		}
	}

	private void updatePortfolio()
	{
		final List<PieChart.Data> data = this.portfolioChart.getData();
		data.clear();

		for (StockAmount sa : this.player.getPortfolio().getStockAmounts())
		{
			final Stock stock = sa.getStock();
			final String symbol = stock.getSymbol();
			final long value = sa.getValue();
			final PieChart.Data e = new PieChart.Data(symbol, value);

			data.add(e);

			final Tooltip tooltip = new Tooltip();
			final String tooltipText = formatAmount(sa.getAmount()) + " x " + formatDollarCents(stock.getPrice()) //
			                           + "\n= " + formatDollarCents(value);
			tooltip.setText(tooltipText);
			Tooltip.install(e.getNode(), tooltip);
		}
	}

	public void displayName(String name)
	{
		this.playerNameLabel.setText(name);
	}

	public void displayNetWorth(long amount)
	{
		TextFields.displayNetWorth(amount, this.netWorthDollarLabel, this.netWorthCentLabel);
	}

	private void displayNetWorthChange(long netWorth, long oldNetWorth)
	{
		TextFields.displayAbsChange(netWorth - oldNetWorth, this.absChangeLabel);

		final double relChange = (double) netWorth / (double) oldNetWorth - 1;
		TextFields.displayRelChange(relChange, this.relChangeLabel);
	}

	public void displayLeaderboardPosition(long position)
	{
		this.leaderboardPositionLabel.setText("#" + position);
	}

	public void displayLeaderboardChange(long change)
	{
		if (change < 0) // ranked higher
		{
			this.leaderboardChangeLabel.setText("\u25b2" + -change);
		}
		else if (change > 0) // ranked lower
		{
			this.leaderboardChangeLabel.setText("\u25bc" + change);
		}
		else
		{
			this.leaderboardChangeLabel.setText("~");
		}
	}
}
