package com.yeyaxi.autohosts;

import android.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class SystemMount
{
    private static final String TAG = SystemMount.class.getSimpleName();

	public static String[] getMountLocation () throws UnableToMountSystemException
	{
		String[] mountStructure = new String[2];
		FileReader fReader = null;
		BufferedReader br = null;
		try
		{
			fReader = new FileReader("/proc/mounts");
			br = new BufferedReader(fReader);
			String line = null;
			int count = 0;

			while ((line = br.readLine()) != null)
			{
				if (line.contains("/system"))
				{

					String[] mountLocationArray = line.split(" ");
					if (mountLocationArray.length >= 3)
					{
						mountStructure[0] = mountLocationArray[0];
						mountStructure[1] = mountLocationArray[2];
					}
					return mountStructure;
				}
			}

		} catch (Exception ex)
		{
			Log.d(TAG, "Error getting mount location", ex);
		} finally
		{
			if (fReader != null)
			{
				try
				{
					fReader.close();
				} catch (IOException ex)
				{
					Log.e(TAG, "Error closing file reader", ex);
				}

			}

			if (br != null)
			{
				try
				{
					br.close();
				} catch (IOException ex)
				{
					Log.e(TAG, "Error closing buffered reader", ex);
				}
			}

		}
		throw new UnableToMountSystemException("Unable to mount /system folder as writable.");
	}
}
