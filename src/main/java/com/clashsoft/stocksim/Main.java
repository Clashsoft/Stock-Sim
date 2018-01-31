package com.clashsoft.stocksim;

import com.clashsoft.stocksim.local.LocalStockSim;
import com.clashsoft.stocksim.ui.MainViewController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application
{
	public static void main(String[] args)
	{
		launch(Main.class, args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception
	{
		final LocalStockSim stockSim = new LocalStockSim();

		final FXMLLoader loader = new FXMLLoader(Main.class.getResource("ui/MainView.fxml"));
		final Parent root = loader.load();

		final MainViewController controller = loader.getController();

		controller.setStockSim(stockSim);
		controller.setStage(primaryStage);

		primaryStage.setTitle("Stock Simulator");
		primaryStage.setScene(new Scene(root));

		final BackgroundThread bgThread = new BackgroundThread(stockSim, controller);
		final SimulationThread simThread = new SimulationThread(stockSim);
		bgThread.start();
		simThread.start();

		primaryStage.setOnCloseRequest(evt -> {
			simThread.onClose();
			bgThread.onClose();
		});


		primaryStage.show();
	}
}
