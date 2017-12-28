package com.clashsoft.stocksim;

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
		final Parent root = FXMLLoader.load(Main.class.getResource("ui/MainView.fxml"));

		primaryStage.setTitle("Stock Simulator");
		primaryStage.setScene(new Scene(root));

		primaryStage.show();
	}
}
