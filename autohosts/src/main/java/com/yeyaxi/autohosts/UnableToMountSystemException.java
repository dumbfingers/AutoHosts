package com.yeyaxi.AutoHosts;

public class UnableToMountSystemException extends Exception
{
	public UnableToMountSystemException (Throwable ex)
	{
		super(ex);
	}

	public UnableToMountSystemException (String message)
	{
		super(message);
	}

	public UnableToMountSystemException (String message, Throwable ex)
	{
		super(message, ex);
	}
}
