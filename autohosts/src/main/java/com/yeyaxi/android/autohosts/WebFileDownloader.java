package com.yeyaxi.android.autohosts;

import android.os.AsyncTask;
import android.util.Log;

import java.io.InputStream;
import java.net.URL;

public class WebFileDownloader extends AsyncTask<String, Void, InputStream>
{
	private AutoHostsActivity caller;

	public WebFileDownloader (AutoHostsActivity caller)
	{
		this.caller = caller;
	}

	@Override
	protected InputStream doInBackground (String... inputs)
	{
		try
		{
			Log.d(Constants.LOG_NAME, "Retrieving url: " + inputs[0]);
			return new URL(inputs[0]).openStream();
		} catch (Exception e)
		{
			Log.e(Constants.LOG_NAME, "Error retrieving url stream. ", e);
		}
		return null;
	}

	@Override
	protected void onPostExecute (InputStream inputStream)
	{
		caller.loadHostsFromInputStream(inputStream);
	}
}
