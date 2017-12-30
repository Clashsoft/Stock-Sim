package com.clashsoft.stocksim.ui;

import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleGroup;

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
	public LineChart netWorthChart;
	@FXML
	public PieChart  portfolioChart;
}
