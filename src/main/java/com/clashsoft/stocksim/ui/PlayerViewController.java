package com.clashsoft.stocksim.ui;

import com.clashsoft.stocksim.Main;
import com.clashsoft.stocksim.model.Leaderboard;
import com.clashsoft.stocksim.data.Period;
import com.clashsoft.stocksim.model.Player;
import com.clashsoft.stocksim.model.StockSim;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;

import java.io.IOException;

public class PlayerViewController
{
	@FXML
	public Label playerNameLabel;

	@FXML
	public Label netWorthDollarLabel;
	@FXML
	public Label netWorthCentLabel;

	@FXML
	public Label absChangeDollarLabel;
	@FXML
	public Label absChangeCentLabel;
	@FXML
	public Label relChangeLabel;

	@FXML
	public Label leaderboardPositionLabel;
	@FXML
	public Label leaderboardChangeLabel;

	@FXML
	public ToggleGroup periodToggle;
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
	public LineChart netWorthChart;
	@FXML
	public PieChart  portfolioChart;

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
		this.updateDisplay();
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
		final StockSim stockSim = this.player.getStockSim();
		final long time = stockSim.getTime();
		final long netWorth = this.player.getNetWorth(time);

		this.displayName(this.player.getName());
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
	}

	public void displayName(String name)
	{
		this.playerNameLabel.setText(name);
	}

	public void displayNetWorth(long amount)
	{
		long dollars = amount / 100;
		long cents = amount % 100;

		this.netWorthDollarLabel.setText(String.format("$ %,d", dollars));
		this.netWorthCentLabel.setText(String.format(".%2d", cents));
	}

	private void displayNetWorthChange(long netWorth, long oldNetWorth)
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
