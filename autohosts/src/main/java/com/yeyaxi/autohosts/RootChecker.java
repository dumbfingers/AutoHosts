package com.yeyaxi.AutoHosts;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author bert
 */
public class RootChecker
{
    private static final String TAG = RootChecker.class.getSimpleName();

	public static boolean hasRoot ()
	{
		Log.i(TAG, "Checking for root permission");
		try
		{
			String[] mountLocation = SystemMount.getMountLocation();
			// Perform SU to get root privileges
			Process p = Runtime.getRuntime().exec("su");

			DataOutputStream os = new DataOutputStream(p.getOutputStream());
			os.writeBytes("mount -o rw,remount -t " + mountLocation[1] + " " + mountLocation[0] + " /system\n");

			os.writeBytes("echo Root Test > /system/rootcheck.txt\n");
			os.writeBytes("mount -o ro,remount -t " + mountLocation[1] + " " + mountLocation[0] + " /system\n");
			os.writeBytes("exit\n");
			os.flush();

			p.waitFor();
			if (p.exitValue() != 255)
			{
				String fileContents = readFileContents();
				if (fileContents == null || !fileContents.equals("Root Test"))
				{
					Log.d(TAG, "Reading back contexnts failed.");
					return false;
				}
				// Phone is rooted
				Log.d(TAG, "Phone is Rooted");
				return true;
			} else
			{
				// Phone is not rooted
				Log.d(TAG, "Phone is not Rooted");
			}
		} catch (UnableToMountSystemException ex)
		{
			Log.e(TAG, "Unable to mount the /system folder as read-write", ex);
		} catch (InterruptedException ex)
		{
			Log.e(TAG, "Interupt exception when waiting for user to grant root permission", ex);
		} catch (IOException ex)
		{
			Log.e(TAG, "IO Exception when checking if phone is rooted", ex);
		}
		return false;

	}

	private static String readFileContents () throws IOException
	{
		FileReader fReader = null;
		BufferedReader br = null;
		try
		{
			fReader = new FileReader("/system/rootcheck.txt");
			br = new BufferedReader(fReader);

			StringBuilder sb = new StringBuilder();

			String line = null;
			while ((line = br.readLine()) != null)
			{
				sb.append(line);
			}
			return sb.toString();
		} finally
		{
			if (br != null)
				br.close();
			if (fReader != null)
				fReader.close();
		}
	}

}
