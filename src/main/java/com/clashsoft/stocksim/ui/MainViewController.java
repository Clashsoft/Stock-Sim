package com.clashsoft.stocksim.ui;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class MainViewController
{
	@FXML
	private TableView stocksTable;

	@FXML
	private TableColumn stockSymbolColumn;
	@FXML
	private TableColumn stockNameColumn;
	@FXML
	private TableColumn stockPriceColumn;
	@FXML
	private TableColumn stockAbsChangeColumn;
	@FXML
	private TableColumn stockRelChangeColumn;

	@FXML
	private TableView playersTable;

	@FXML
	private TableColumn playerNameColumn;
	@FXML
	private TableColumn playerNetWorthColumn;
	@FXML
	private TableColumn playerAbsChangeColumn;
	@FXML
	private TableColumn playerRelChangeColumn;
}
