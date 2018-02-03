package com.clashsoft.stocksim.ui;

import com.clashsoft.stocksim.Main;
import com.clashsoft.stocksim.SimulationThread;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.stage.Stage;

import java.io.IOException;

public class SimulationControlViewController
{
	@FXML
	public Slider speedSlider;

	@FXML
	public Label infoLabel;

	private SimulationThread simThread;

	public void setSimThread(SimulationThread simThread)
	{
		this.simThread = simThread;
	}

	public static void open(SimulationThread simThread)
	{
		try
		{
			final FXMLLoader loader = new FXMLLoader(Main.class.getResource("ui/SimulationControlView.fxml"));
			final Parent parent = loader.load();
			final SimulationControlViewController controller = loader.getController();
			final Stage stage = new Stage();

			controller.setSimThread(simThread);

			stage.setScene(new Scene(parent));
			stage.setTitle("Simulation Control");
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
		this.speedSlider.valueProperty().addListener((ob, o, n) -> this.speedChanged());
	}

	private void speedChanged()
	{
		final long steps = this.getStepsPerSecond();
		this.infoLabel.setText("= " + steps + " Simulation Steps / Second");
		this.simThread.setStepsPerSecond(this.getStepsPerSecond());
	}

	private long getStepsPerSecond()
	{
		return (long) Math.pow(10, (int) this.speedSlider.getValue());
	}

	@FXML
	public void onPauseAction()
	{
		this.speedSlider.setValue(-1);
	}

	@FXML
	public void onSetDefaultAction()
	{
		this.speedSlider.setValue(2);
	}

	@FXML
	public void onSetMaxAction()
	{
		this.speedSlider.setValue(this.speedSlider.getMax());
	}
}
