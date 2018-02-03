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
				final long start = System.currentTimeMillis();
				final long stepsPerSecond = this.stepsPerSecond;
				for (int i = 0; i < stepsPerSecond; i++)
				{
					this.sim.simulate();
				}
				final long end = System.currentTimeMillis();
				final long elapsed = end - start;
				System.out.println(stepsPerSecond + " Simulation Steps took " + elapsed + " ms");
				final long sleep = 1000 - elapsed; // 1 second minus elapsed time

				if (sleep > 0)
				{
					Thread.sleep(sleep);
				}
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
