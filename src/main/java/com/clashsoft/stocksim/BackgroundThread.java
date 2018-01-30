package com.clashsoft.stocksim;

import com.clashsoft.stocksim.local.LocalStockSim;
import com.clashsoft.stocksim.ui.MainViewController;
import javafx.application.Platform;

import java.io.File;
import java.io.IOException;

public class BackgroundThread extends Thread
{
	private static final File DATA_DIR = new File("data");

	private final LocalStockSim      stockSim;
	private final MainViewController controller;

	private volatile boolean running = true;

	public BackgroundThread(LocalStockSim stockSim, MainViewController controller)
	{
		this.stockSim = stockSim;
		this.controller = controller;
	}

	@Override
	public void run()
	{
		this.load();

		while (this.running)
		{
			for (int i = 0; i < 5; i++)
			{
				this.update();
				try
				{
					Thread.sleep(1000);
				}
				catch (InterruptedException ignored)
				{
				}
			}
			this.save();
		}

		this.save();
	}

	private void update()
	{
		Platform.runLater(this.controller::updateAll);
	}

	private void load()
	{
		try
		{
			this.stockSim.load(DATA_DIR);
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
			// TODO handle load error
		}
	}

	private void save()
	{
		try
		{
			System.out.println("Saving...");
			long start = System.currentTimeMillis();
			this.stockSim.save(DATA_DIR);
			long end = System.currentTimeMillis();
			System.out.println("Done. (" + (end - start) + " ms)");
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
			// TODO handle save error
		}
	}

	public void onClose()
	{
		System.out.println("Close requested");
		this.running = false;
	}
}
