package com.yeyaxi.autohosts;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.util.ByteArrayBuffer;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class WebFileDownloader extends AsyncTask<String, Void, File>
{
	private AutoHostsActivity caller;
    private static final String TAG = WebFileDownloader.class.getSimpleName();
	public WebFileDownloader (AutoHostsActivity caller)
	{
		this.caller = caller;
	}

	@Override
	protected File doInBackground (String... inputs)
	{
		try
		{
			Log.d(TAG, "Retrieving url: " + inputs[0]);
//			return new URL(inputs[0]).openStream();
//            return getContent(inputs[0]);

            File f = null;

            URL url = new URL(inputs[0]);
            URLConnection ucon = url.openConnection();
            InputStream is = ucon.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);

            ByteArrayBuffer baf = new ByteArrayBuffer(50);
            int current = 0;
            while ((current = bis.read()) != -1) {
                baf.append((byte) current);
            }

            f = new File(caller.getWritableCacheDir(), "hosts");
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(baf.toByteArray());
            fos.close();

            return f;

        } catch (MalformedURLException mue) {
            mue.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
		return null;
	}

	@Override
	protected void onPostExecute (File file)
	{
//		caller.loadHostsFromInputStream(inputStream);
        caller.loadHostsFromFile(file);
	}

    public File getContent(String strUrl) throws IOException{
//        try {
            URL url = new URL(strUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoOutput(true);
            connection.connect();
            InputStream is = connection.getInputStream();
//            BufferedInputStream bis = new BufferedInputStream(is);
            Reader reader = new InputStreamReader(is, "UTF-8");
            ByteArrayBuffer baf = new ByteArrayBuffer(50);
            while (reader.read() != -1) {
                baf.append((byte) reader.read());
            }
//            try {
                // Create file
                File file = new File(caller.getWritableCacheDir(), "hosts");
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(baf.toByteArray());
                fos.close();
//            } catch (Exception e){
//                //Catch exception if any
//                Log.e("AutoHosts","Error: " + e.getMessage());
//            }
            is.close();

//        } catch (Exception e) {

//            return "error open url:" + strUrl;

//        }
        return file;
    }
}
