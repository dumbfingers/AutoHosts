package com.yeyaxi.AutoHosts;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.ProgressDialog;
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
	Button setHosts;
	ProgressDialog load = null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		version = (TextView)findViewById(R.id.ver);
		getHosts = (Button)findViewById(R.id.getHosts);
		setHosts = (Button)findViewById(R.id.setHosts);
	}
	public void onResume() {
		super.onResume();
		if(!su.can_su) {
			Toast.makeText(this, R.string.err_no_root, Toast.LENGTH_SHORT).show();
		}
		getHosts.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				load = ProgressDialog.show(AutoHostsActivity.this, "Please standby...", "Now retrieving hosts.", true);
				new Thread(new Runnable() {
					public void run() {
						getContent(Constants.hosts);
						load.dismiss();
					}
				}).start();
				
				try {
					version.setText(getVersion(Constants.svn));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				//				GetContentTask.execute(Constants.hosts);
			}
		});
		setHosts.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				updateHosts();
			}
		});
	}
//	private class GetContentTask extends AsyncTask<String, Void, String> {
//		protected void onPreExcute() {
//			try {
//				version.setText(getVersion(Constants.svn));
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//		
//		@Override
//		protected String doInBackground(String... strUrl) {
//			// TODO Auto-generated method stub
//			return null;
//		}
//		protected void onPostExcuted(String strUrl) {
//			getContent(strUrl);
//		}
//	}
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
				content = content + curLine+ "\n";
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
				Toast.makeText(this, R.string.host_pulled, Toast.LENGTH_LONG).show();
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
	
	public String updateHosts() {
		//Mount /system as read-write
		su.Run("mount -o remount,rw -t yaffs2 /dev/block/mtdblock3 /system");
		Log.d("AutoHosts", "/system R/W mounted");
		//Copy the newer hosts to replace the older system one
		su.Run("cp /data/data/com.yeyaxi.AutoHosts/files/hosts /system/etc/hosts");
		Log.d("AutoHosts","Hosts copied to /etc");
		//Fix the permission
		su.Run("chmod 644 /system/etc/hosts");
		Log.d("AutoHosts","Hosts ready to use");
		Toast.makeText(AutoHostsActivity.this, R.string.host_success, Toast.LENGTH_LONG);
		return null;
	}
	public String getVersion(String s) throws IOException {
		String version = "";
		String curLine = "";
	    URL url = new URL(s);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.connect();
		InputStream is = connection.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		while ((curLine = reader.readLine()) != null) {
			version = version + curLine+ "\n";
		}

	    version = version.replaceAll("\\s+", " ");
	    Pattern p = Pattern.compile("<title>(.*?)</title>");
	    Matcher m = p.matcher(version);
	    while (m.find() == true) {
	      version = m.group(1);
	    }
		return version;
	}
}