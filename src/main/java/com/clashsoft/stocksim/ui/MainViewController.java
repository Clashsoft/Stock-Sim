package com.clashsoft.stocksim.ui;

import com.clashsoft.stocksim.data.Transaction;
import com.clashsoft.stocksim.model.Player;
import com.clashsoft.stocksim.model.Stock;
import com.clashsoft.stocksim.model.StockSim;
import com.clashsoft.stocksim.ui.table.*;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainViewController
{
	public static final int MAX_TRANSACTIONS = 100;

	@FXML
	private TableView<Stock> stocksTable;

	@FXML
	private TableColumn<Stock, String> stockSymbolColumn;
	@FXML
	private TableColumn<Stock, String> stockNameColumn;
	@FXML
	private TableColumn<Stock, Long>   stockPriceColumn;
	@FXML
	private TableColumn<Stock, Long>   stockSupplyColumn;
	@FXML
	private TableColumn<Stock, Long>   stockMarketCapColumn;

	@FXML
	private TableView<Player> playersTable;

	@FXML
	private TableColumn<Player, String> playerNameColumn;
	@FXML
	private TableColumn<Player, Long>   playerNetWorthColumn;
	@FXML
	private TableColumn<Player, Long>   playerStocksValueColumn;
	@FXML
	private TableColumn<Player, Long>   playerCashColumn;

	@FXML
	public TableView<Transaction> transactionTable;

	@FXML
	public TableColumn<Transaction, Long>   transactionTimeColumn;
	@FXML
	public TableColumn<Transaction, UUID>   transactionIDColumn;
	@FXML
	public TableColumn<Transaction, Player> transactionSellerColumn;
	@FXML
	public TableColumn<Transaction, Player> transactionBuyerColumn;
	@FXML
	public TableColumn<Transaction, Stock>  transactionStockColumn;
	@FXML
	public TableColumn<Transaction, Long>   transactionAmountColumn;
	@FXML
	public TableColumn<Transaction, Long>   transactionStockPriceColumn;
	@FXML
	public TableColumn<Transaction, Long>   transactionTotalColumn;

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
	public void initialize()
	{
		this.initPlayersTable();
		this.initStocksTable();
		this.initTransactionTable();
	}

	private void initStocksTable()
	{
		this.stocksTable.setRowFactory(e -> {
			final TableRow<Stock> row = new TableRow<>();
			row.setOnMouseClicked(evt -> {
				if (evt.getButton() == MouseButton.PRIMARY && evt.getClickCount() >= 2)
				{
					StockViewController.open(row.getItem());
				}
			});
			row.setOnKeyReleased(evt -> {
				if (evt.getCode() == KeyCode.ENTER)
				{
					StockViewController.open(row.getItem());
				}
			});
			return row;
		});

		this.stocksTable.getSortOrder().add(this.stockNameColumn);

		// Stock Table Columns

		this.stockSymbolColumn.setCellValueFactory(new PropertyValueFactory<>("symbol"));

		this.stockNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

		this.stockPriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
		this.stockPriceColumn.setCellFactory(e -> new PriceTableCell<>());

		this.stockSupplyColumn.setCellValueFactory(new PropertyValueFactory<>("supply"));
		this.stockSupplyColumn.setCellFactory(e -> new AmountTableCell<>());

		this.stockMarketCapColumn.setCellValueFactory(new PropertyValueFactory<>("marketCap"));
		this.stockMarketCapColumn.setCellFactory(e -> new PriceTableCell<>());
	}

	private void initPlayersTable()
	{
		this.playersTable.setRowFactory(e -> {
			final TableRow<Player> row = new TableRow<>();
			row.setOnMouseClicked(evt -> {
				if (evt.getButton() == MouseButton.PRIMARY && evt.getClickCount() >= 2)
				{
					PlayerViewController.open(row.getItem());
				}
			});
			row.setOnKeyReleased(evt -> {
				if (evt.getCode() == KeyCode.ENTER)
				{
					PlayerViewController.open(row.getItem());
				}
			});
			return row;
		});

		this.playersTable.getSortOrder().add(this.playerNameColumn);

		// Player Table Columns

		this.playerNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

		this.playerNetWorthColumn.setCellValueFactory(new PropertyValueFactory<>("netWorth"));
		this.playerNetWorthColumn.setCellFactory(e -> new PriceTableCell<>());

		this.playerStocksValueColumn.setCellValueFactory(new PropertyValueFactory<>("stocksValue"));
		this.playerStocksValueColumn.setCellFactory(e -> new PriceTableCell<>());

		this.playerCashColumn.setCellValueFactory(new PropertyValueFactory<>("cash"));
		this.playerCashColumn.setCellFactory(e -> new PriceTableCell<>());
	}

	private void initTransactionTable()
	{
		this.transactionTable.getSortOrder().add(this.transactionTimeColumn);

		// Transaction Table Columns

		this.transactionTimeColumn.setCellValueFactory(new PropertyValueFactory<>("time"));
		this.transactionTimeColumn.setCellFactory(e -> new TimeTableCell<>());

		this.transactionIDColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

		this.transactionSellerColumn.setCellValueFactory(new PropertyValueFactory<>("seller"));
		this.transactionSellerColumn.setCellFactory(e -> new PlayerTableCell<>());

		this.transactionBuyerColumn.setCellValueFactory(new PropertyValueFactory<>("buyer"));
		this.transactionBuyerColumn.setCellFactory(e -> new PlayerTableCell<>());

		this.transactionStockColumn.setCellValueFactory(new PropertyValueFactory<>("stock"));
		this.transactionStockColumn.setCellFactory(e -> new StockTableCell<>());

		this.transactionAmountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
		this.transactionAmountColumn.setCellFactory(e -> new AmountTableCell<>());

		this.transactionStockPriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
		this.transactionStockPriceColumn.setCellFactory(e -> new PriceTableCell<>());

		this.transactionTotalColumn.setCellValueFactory(new PropertyValueFactory<>("total"));
		this.transactionTotalColumn.setCellFactory(e -> new PriceTableCell<>());
	}

	public void updateAll()
	{
		this.updateStocks();
		this.updatePlayers();
		this.updateTransactions();
	}

	@FXML
	public void updateStocks()
	{
		final List<Stock> stocks = new ArrayList<>();
		this.sim.eachStock(stocks::add);
		this.stocksTable.getItems().setAll(stocks);
		this.stocksTable.sort();
	}

	@FXML
	public void updatePlayers()
	{
		final List<Player> players = new ArrayList<>();
		this.sim.eachPlayer(players::add);
		this.playersTable.getItems().setAll(players);
		this.playersTable.sort();
	}

	@FXML
	public void updateTransactions()
	{
		List<Transaction> transactions = new ArrayList<>(MAX_TRANSACTIONS);
		this.sim.eachTransaction(MAX_TRANSACTIONS, transactions::add);
		this.transactionTable.getItems().setAll(transactions);
		this.transactionTable.sort();
	}

	@FXML
	public void onAddStockAction()
	{
		CreateStockViewController.open(this.sim);
	}

	@FXML
	public void onAddPlayerAction()
	{
		CreatePlayerViewController.open(this.sim);
	}

	@FXML
	public void onAddTransactionAction()
	{
		CreateTransactionViewController.open(this.sim);
	}
}
