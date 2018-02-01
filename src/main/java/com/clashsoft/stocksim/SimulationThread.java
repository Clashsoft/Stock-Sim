package com.clashsoft.stocksim;

import com.clashsoft.stocksim.local.LocalStockSim;

public class SimulationThread extends Thread
{
	private final LocalStockSim sim;

	private volatile boolean running = true;
	private volatile long stepsPerSecond;

	public SimulationThread(LocalStockSim sim)
	{
		this.sim = sim;
	}

	@Override
	public void run()
	{
		while (this.running)
		{
			try
			{
				this.sim.simulate();
				Thread.sleep(100);
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}
	}

	public void onClose()
	{
		this.running = false;
	}

	public void setStepsPerSecond(long stepsPerSecond)
	{

		this.stepsPerSecond = stepsPerSecond;
	}
}
