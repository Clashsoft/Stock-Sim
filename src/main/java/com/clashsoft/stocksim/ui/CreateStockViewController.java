package com.clashsoft.stocksim.ui;

import com.clashsoft.stocksim.Main;
import com.clashsoft.stocksim.model.StockSim;
import com.clashsoft.stocksim.ui.util.TextFields;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class CreateStockViewController
{
	@FXML
	public TextField stockSymbolField;

	@FXML
	public TextField stockNameField;

	@FXML
	public TextField supplyField;

	@FXML
	public TextField initialPriceDollarField;

	@FXML
	public TextField initialPriceCentField;

	@FXML
	public TextField marketCapCentField;

	@FXML
	public TextField marketCapDollarField;

	private StockSim sim;
	private Stage    stage;

	public CreateStockViewController()
	{
	}

	public CreateStockViewController(StockSim sim)
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

	public static void open(StockSim sim)
	{
		try
		{
			final FXMLLoader loader = new FXMLLoader(Main.class.getResource("ui/CreateStockView.fxml"));
			final Parent parent = loader.load();
			final CreateStockViewController controller = loader.getController();
			final Stage stage = new Stage();

			controller.setStockSim(sim);
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
	public void initialize()
	{
		TextFields.setupDollarCentFields(this.initialPriceDollarField, this.initialPriceCentField);

		TextFields.setupNumberField(this.supplyField);

		this.initialPriceDollarField.getTextFormatter().valueProperty()
		                            .addListener((ob, o, n) -> this.onValueChanged());
		this.initialPriceCentField.getTextFormatter().valueProperty().addListener((ob, o, n) -> this.onValueChanged());
		this.supplyField.getTextFormatter().valueProperty().addListener((ob, o, n) -> this.onValueChanged());
	}

	private void onValueChanged()
	{
		final long marketCap = this.getSupply() * this.getPrice();

		this.marketCapDollarField.setText(String.format("%,d", marketCap / 100));
		this.marketCapCentField.setText(String.format("%02d", marketCap % 100));
	}

	private long getSupply()
	{
		return (Long) this.supplyField.getTextFormatter().getValue();
	}

	private long getPrice()
	{
		final long dollars = (Long) this.initialPriceDollarField.getTextFormatter().getValue();
		final long cents = (Long) this.initialPriceCentField.getTextFormatter().getValue();
		return dollars * 100 + cents;
	}

	@FXML
	public void onCreateStock()
	{
		final long amount = this.getSupply();
		final long price = this.getPrice();

		this.sim.createStock(this.stockNameField.getText(), this.stockSymbolField.getText(), amount, price);

		this.stage.close();
	}

	@FXML
	public void onCancel()
	{
		this.stage.close();
	}
}
