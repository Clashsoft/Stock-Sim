package com.clashsoft.stocksim.ui;

import com.clashsoft.stocksim.Main;
import com.clashsoft.stocksim.data.Transaction;
import com.clashsoft.stocksim.model.Player;
import com.clashsoft.stocksim.model.Stock;
import com.clashsoft.stocksim.model.StockSim;
import com.clashsoft.stocksim.ui.util.TextFields;
import com.clashsoft.stocksim.ui.util.TimeTableCell;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.UUID;

public class CreateTransactionViewController
{
	@FXML
	public Label timestampLabel;

	@FXML
	public Label idLabel;

	@FXML
	public TextField stockSymbolField;

	@FXML
	public TextField stockNameField;

	@FXML
	public TextField sellerNameField;

	@FXML
	public TextField buyerNameField;

	@FXML
	public TextField stockPriceDollarField;

	@FXML
	public TextField stockPriceCentField;

	@FXML
	public TextField amountField;

	@FXML
	public TextField totalCentField;

	@FXML
	public TextField totalDollarField;

	private StockSim sim;
	private Stage    stage;

	private long time;
	private final UUID id = UUID.randomUUID();

	private Stock  stock;
	private Player buyer;
	private Player seller;

	public CreateTransactionViewController()
	{
	}

	public CreateTransactionViewController(StockSim sim)
	{
		this.setStockSim(sim);
	}

	public void setStockSim(StockSim sim)
	{
		this.sim = sim;
		this.time = sim.getTime();
	}

	public void setStage(Stage stage)
	{
		this.stage = stage;
	}

	public static void open(StockSim sim)
	{
		try
		{
			final FXMLLoader loader = new FXMLLoader(Main.class.getResource("ui/CreateTransactionView.fxml"));
			final Parent parent = loader.load();
			final CreateTransactionViewController controller = loader.getController();
			final Stage stage = new Stage();

			controller.setStockSim(sim);
			controller.setStage(stage);

			stage.setScene(new Scene(parent));
			stage.setTitle("Create New Transaction");
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
		this.idLabel.setText(this.id.toString());
		this.timestampLabel.setText(TimeTableCell.formatTime(this.time));

		this.stockSymbolField.textProperty().addListener((ob, o, n) -> this.onStockSymbolChanged());

		this.sellerNameField.textProperty().addListener((ob, o, n) -> this.onSellerNameChanged());
		this.buyerNameField.textProperty().addListener((ob, o, n) -> this.onBuyerNameChanged());

		TextFields.setupDollarCentFields(this.stockPriceDollarField, this.stockPriceCentField);

		TextFields.setupNumberField(this.amountField);

		this.stockPriceDollarField.getTextFormatter().valueProperty().addListener((ob, o, n) -> this.onValueChanged());
		this.stockPriceCentField.getTextFormatter().valueProperty().addListener((ob, o, n) -> this.onValueChanged());
		this.amountField.getTextFormatter().valueProperty().addListener((ob, o, n) -> this.onValueChanged());
	}

	private void onStockSymbolChanged()
	{
		this.stock = this.sim.getStock(this.stockSymbolField.getText());
		if (this.stock != null)
		{
			this.stockNameField.setText(this.stock.getName());
			this.stockSymbolField.setStyle("-fx-border-color: green");
		}
		else
		{
			this.stockNameField.setText("???");
			this.stockSymbolField.setStyle("-fx-border-color: red");
		}
	}

	private void onSellerNameChanged()
	{
		this.seller = this.sim.getPlayer(this.sellerNameField.getText());
		if (this.seller != null)
		{
			this.sellerNameField.setStyle("-fx-border-color: green");
		}
		else
		{
			this.sellerNameField.setStyle("-fx-border-color: red");
		}
	}

	private void onBuyerNameChanged()
	{
		this.buyer = this.sim.getPlayer(this.buyerNameField.getText());
		if (this.buyer != null)
		{
			this.buyerNameField.setStyle("-fx-border-color: green");
		}
		else
		{
			this.buyerNameField.setStyle("-fx-border-color: red");
		}
	}

	private void onValueChanged()
	{
		final long marketCap = this.getAmount() * this.getPrice();

		this.totalDollarField.setText(String.format("%,d", marketCap / 100));
		this.totalCentField.setText(String.format("%02d", marketCap % 100));
	}

	private long getAmount()
	{
		return (Long) this.amountField.getTextFormatter().getValue();
	}

	private long getPrice()
	{
		final long dollars = (Long) this.stockPriceDollarField.getTextFormatter().getValue();
		final long cents = (Long) this.stockPriceCentField.getTextFormatter().getValue();
		return dollars * 100 + cents;
	}

	@FXML
	public void onCreateTransaction()
	{
		if (this.stock == null || this.seller == null || this.buyer == null)
		{
			return;
		}

		final long amount = this.getAmount();
		final long price = this.getPrice();

		this.sim.addTransaction(
			new Transaction(this.id, this.time, this.stock, amount, price, this.seller, this.buyer));

		this.stage.close();
	}

	@FXML
	public void onCancel()
	{
		this.stage.close();
	}
}
