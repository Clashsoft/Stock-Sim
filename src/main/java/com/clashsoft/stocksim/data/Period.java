package com.clashsoft.stocksim.data;

public enum Period
{
	SECOND(1),
	MINUTE(SECOND.length * 60),
	HOUR(MINUTE.length * 60),
	DAY(HOUR.length * 24),
	MONTH((long) (DAY.length * 30.436875)),
	YEAR((long) (DAY.length * 365.2425)),
	ALL_TIME(Long.MAX_VALUE);

	public final long length;

	Period(long length)
	{
		this.length = length;
	}
}
