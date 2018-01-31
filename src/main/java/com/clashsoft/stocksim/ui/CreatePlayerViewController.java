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

	public static void open(StockSim sim)
	{
		try
		{
			final FXMLLoader loader = new FXMLLoader(Main.class.getResource("ui/CreatePlayerView.fxml"));
			final Parent parent = loader.load();
			final CreatePlayerViewController controller = loader.getController();
			final Stage stage = new Stage();

			controller.setStockSim(sim);
			controller.setStage(stage);

			stage.setScene(new Scene(parent));
			stage.setTitle("Create New Player");
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
