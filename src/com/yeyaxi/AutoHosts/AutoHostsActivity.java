package com.yeyaxi.AutoHosts;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class AutoHostsActivity extends Activity {
	/** Called when the activity is first created. */
	Su su = new Su();
	TextView version;
	Button getHosts;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		version = (TextView)findViewById(R.id.ver);
		getHosts = (Button)findViewById(R.id.button1); 
	}
	public void onResume() {
		super.onResume();
		if(!su.can_su) {
			Toast.makeText(this, "Error: Cannot get ROOT", Toast.LENGTH_SHORT).show();
		}
		getHosts.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				getContent(Constants.hosts);
			}
		});
	}
	public String getContent(String strUrl) {

		try {

			String curLine = "";
			String content = "";
			URL url = new URL(strUrl);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();

			connection.connect();

			InputStream is = connection.getInputStream();

			BufferedReader reader = new BufferedReader(new InputStreamReader(is));

			while ((curLine = reader.readLine()) != null) {

				content = content + curLine+ "\r\n";

			}
			try{
				// Create file 
//				File file = new File("/sdcard/hosts");
//				FileWriter fstream = new FileWriter(file);
//				BufferedWriter out = new BufferedWriter(fstream);
//				out.write(content);

				FileOutputStream fOut = openFileOutput("hosts", MODE_PRIVATE);
//				fOut.write(content.getBytes());
//				fOut.close();
				OutputStreamWriter osw = new OutputStreamWriter(fOut);
				//Close the output stream
				osw.write(content);
				osw.flush();
				Toast.makeText(this, R.string.host_pulled, Toast.LENGTH_SHORT).show();
//				out.close();
			}catch (Exception e){//Catch exception if any
				Log.e("AutoHosts","Error: " + e.getMessage());
			}
			//System.out.println("content= " + content);

			is.close();

			//			}
			//
			//			br.close();
			//
			//			return sb.toString();

		} catch (Exception e) {

			return "error open url:" + strUrl;

		}
		return strUrl;
	}
}