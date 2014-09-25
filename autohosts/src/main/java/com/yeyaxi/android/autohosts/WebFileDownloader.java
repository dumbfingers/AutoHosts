package com.yeyaxi.android.autohosts;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class WebFileDownloader extends AsyncTask<String, Void, InputStream>
{
	private AutoHostsActivity caller;
    private static final String TAG = WebFileDownloader.class.getSimpleName();
	public WebFileDownloader (AutoHostsActivity caller)
	{
		this.caller = caller;
	}

	@Override
	protected InputStream doInBackground (String... inputs)
	{
		try
		{
			Log.d(TAG, "Retrieving url: " + inputs[0]);
			return new URL(inputs[0]).openStream();
		} catch (Exception e)
		{
			Log.e(TAG, "Error retrieving url stream. ", e);
		}
		return null;
	}

	@Override
	protected void onPostExecute (InputStream inputStream)
	{
		caller.loadHostsFromFile(inputStream);
	}

    public String getContent(String strUrl) {
        try {
            String curLine = "";
            String content = "";
            URL url = new URL(strUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoOutput(true);
            connection.connect();
            InputStream is = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            while ((curLine = reader.readLine()) != null) {
                content = content + curLine+ "\n";
            }
            try{
                // Create file
//				File file = new File("/sdcard/hosts");
//				FileWriter fstream = new FileWriter(file);
//				BufferedWriter out = new BufferedWriter(fstream);
//				out.write(content);
                File file = new File(caller.getWritableCacheDir(), "hosts");
                FileOutputStream fOut = new FileOutputStream(file);
//				fOut.write(content.getBytes());
//				fOut.close();
                OutputStreamWriter osw = new OutputStreamWriter(fOut);
                //Close the output stream
                osw.write(content);
                osw.flush();
//				out.close();
            }catch (Exception e){
                //Catch exception if any
                Log.e("AutoHosts","Error: " + e.getMessage());
            }
            is.close();

        } catch (Exception e) {

            return "error open url:" + strUrl;

        }
        return strUrl;
    }
}
