package com.yeyaxi.android.autohosts;

import android.os.AsyncTask;
import android.util.Log;
import com.yeyaxi.AutoHosts.R;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * @author bert
 */
public class CommandRunner extends AsyncTask<String, Void, Boolean>
{
	private AutoHostsActivity callback;
	private int messageId;

	public CommandRunner (AutoHostsActivity callback, int messageId)
	{
		this.messageId = messageId;
		this.callback = callback;
	}

	@Override
	protected Boolean doInBackground (String... inputs)
	{
		BufferedReader bufferedReader = null;

		try
		{
			String[] mountLocation = SystemMount.getMountLocation();

			final Runtime runtime = Runtime.getRuntime();
			Process p = runtime.exec("su");
			DataOutputStream os = new DataOutputStream(p.getOutputStream());
			os.writeBytes("mount -o rw,remount -t " + mountLocation[1] + " " + mountLocation[0] + " /system\n");
			for (String input : inputs)
			{
				Log.d(Constants.LOG_NAME, "Executing Command: " + input);
				os.writeBytes(input);

			}

			os.writeBytes("mount -o ro,remount -t " + mountLocation[1] + " " + mountLocation[0] + " /system\n");
			os.writeBytes("exit\n");
			os.flush();

			p.waitFor();

			if (p.exitValue() != 255)
				return Boolean.TRUE;

		} catch (InterruptedException ex)
		{
			Log.e(Constants.LOG_NAME, ex.getMessage(), ex);
		} catch (IOException ex)
		{
			Log.e(Constants.LOG_NAME, ex.getMessage(), ex);
		} catch (UnableToMountSystemException ex)
		{
			Log.e(Constants.LOG_NAME, ex.getMessage(), ex);
		} finally
		{
			if (bufferedReader != null)
				try
				{
					bufferedReader.close();
				} catch (IOException ex)
				{
				}
		}

		return Boolean.FALSE;
	}

	@Override
	protected void onPostExecute (Boolean success)
	{
		if (success)
			callback.displayCalbackMessage(messageId, R.string.append_success);
		else
			callback.displayCalbackErrorMessage(messageId);
	}
}
