package com.clashsoft.stocksim.ui;

import com.clashsoft.stocksim.model.StockSim;
import com.clashsoft.stocksim.ui.util.TextFields;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class CreatePlayerViewController
{
	@FXML
	public TextField playerNameField;

	@FXML
	public TextField initialCashDollarField;

	@FXML
	public TextField initialCashCentField;

	private StockSim sim;
	private Stage stage;

	public CreatePlayerViewController()
	{
	}

	public CreatePlayerViewController(StockSim sim)
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
		TextFields.setupDollarCentFields(this.initialCashDollarField, this.initialCashCentField);
	}

	@FXML
	public void onCreatePlayer()
	{
		final String name = this.playerNameField.getText();

		final long dollars = (Long) this.initialCashDollarField.getTextFormatter().getValue();
		final long cents = (Long) this.initialCashCentField.getTextFormatter().getValue();
		final long cash = dollars * 100 + cents;

		this.sim.createPlayer(name, cash);

		this.stage.close();
	}

	@FXML
	public void onCancel()
	{
		this.stage.close();
	}
}
