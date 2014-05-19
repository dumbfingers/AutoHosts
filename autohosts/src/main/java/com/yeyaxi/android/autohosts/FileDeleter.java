package com.yeyaxi.android.autohosts;

import android.os.AsyncTask;
import android.util.Log;
import com.yeyaxi.AutoHosts.R;

import java.io.File;

public class FileDeleter extends AsyncTask<String, Void, Boolean>
{
	private AutoHostsActivity caller;
	private int callbackMessage;

	public FileDeleter (AutoHostsActivity caller, int callbackMessage)
	{
		this.caller = caller;
		this.callbackMessage = callbackMessage;
	}

	@Override
	protected Boolean doInBackground (String... input)
	{
		File fileToDelete = new File(input[0]);
		if (fileToDelete != null && fileToDelete.isFile() && fileToDelete.canWrite())
			return fileToDelete.delete();
		return true;

	}

	@Override
	protected void onPostExecute (Boolean success)
	{
		Log.d(Constants.LOG_NAME, "Result for deleting file (" + callbackMessage + "): " + success);
		if (success)
		{
			caller.displayCalbackMessage(callbackMessage, R.string.append_success);
			caller.doNextTask();
		}
		else
			caller.displayCalbackErrorMessage(callbackMessage);
	}
}
