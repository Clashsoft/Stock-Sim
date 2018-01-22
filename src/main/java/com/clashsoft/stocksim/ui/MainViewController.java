package com.clashsoft.stocksim.ui;

import com.clashsoft.stocksim.Main;
import com.clashsoft.stocksim.model.Player;
import com.clashsoft.stocksim.model.Stock;
import com.clashsoft.stocksim.model.StockSim;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.io.IOException;

public class MainViewController
{
	@FXML
	private TableView<Stock> stocksTable;

	@FXML
	private TableColumn<Stock, String> stockSymbolColumn;
	@FXML
	private TableColumn<Stock, String> stockNameColumn;
	@FXML
	private TableColumn<Stock, Long> stockPriceColumn;
	@FXML
	private TableColumn<Stock, Long> stockAbsChangeColumn;
	@FXML
	private TableColumn<Stock, Double> stockRelChangeColumn;

	@FXML
	private TableView<Player> playersTable;

	@FXML
	private TableColumn<Player, String> playerNameColumn;
	@FXML
	private TableColumn<Player, Long> playerNetWorthColumn;
	@FXML
	private TableColumn<Player, Long> playerAbsChangeColumn;
	@FXML
	private TableColumn<Player, Double> playerRelChangeColumn;

	private StockSim sim;
	private Stage    stage;

	public MainViewController()
	{
	}

	public MainViewController(StockSim sim)
	{
		this.sim = sim;
	}

	public void setStockSim(StockSim sim)
	{
		this.sim = sim;
	}

	public void setStage(Stage stage)
	{
		this.stage = stage;
	}

	@FXML
	public void updateStocks()
	{
		this.stocksTable.getItems().clear();
		this.stocksTable.getItems().addAll(this.sim.getStocks());
	}

	@FXML
	public void updatePlayers()
	{
		this.playersTable.getItems().clear();
		this.playersTable.getItems().addAll(this.sim.getPlayers());
	}

	@FXML
	public void onAddStockAction()
	{
		try
		{
			final FXMLLoader loader = new FXMLLoader(Main.class.getResource("ui/CreateStockView.fxml"));
			final Parent parent = loader.load();
			final CreateStockViewController controller = loader.getController();
			final Stage stage = new Stage();

			controller.setStockSim(this.sim);
			controller.setStage(stage);

			stage.setScene(new Scene(parent));
			stage.setTitle("Create New Stock");
			stage.show();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	@FXML
	public void onAddPlayerAction()
	{
		try
		{
			final FXMLLoader loader = new FXMLLoader(Main.class.getResource("ui/CreatePlayerView.fxml"));
			final Parent parent = loader.load();
			final CreatePlayerViewController controller = loader.getController();
			final Stage stage = new Stage();

			controller.setStockSim(this.sim);
			controller.setStage(stage);

			stage.setScene(new Scene(parent));
			stage.setTitle("Create New Player");
			stage.show();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
