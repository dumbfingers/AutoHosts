package com.yeyaxi.AutoHosts;

/**
 *  GNU GPL v3
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *  
 */

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

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
/**
 * AutoHosts - An app for android to update hosts.
 * @author Yaxi Ye
 * @version 1.0.1
 * @since Nov.7.2011
 *
 */
public class AutoHostsActivity extends Activity {
	/** Called when the activity is first created. */
	Su su = new Su();
	TextView version;
	TextView textView2;
	Button getHosts;
	Button setHosts;
	Button revertHosts;
	ProgressDialog load = null;
	private AdView adView;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		version = (TextView)findViewById(R.id.ver);
		getHosts = (Button)findViewById(R.id.getHosts);
		setHosts = (Button)findViewById(R.id.setHosts);
		revertHosts = (Button)findViewById(R.id.revertHosts);
		textView2 = (TextView)findViewById(R.id.textView2);
	    // Create the adView
	    adView = new AdView(this, AdSize.BANNER, Constants.MY_AD_UNIT_ID);

	    // Lookup your LinearLayout assuming it’s been given
	    // the attribute android:id="@+id/mainLayout"
	    LinearLayout layout = (LinearLayout)findViewById(R.id.adLayout);

	    // Add the adView to it
	    layout.addView(adView);

	    // Initiate a generic request to load it with an ad
	    adView.loadAd(new AdRequest());
	    textView2.setText(R.string.current_ver);
	}
	public void onResume() {
		super.onResume();
	    //textView2.setText(R.string.current_ver);
		if(!su.can_su) {
			Toast.makeText(this, R.string.err_no_root, Toast.LENGTH_SHORT).show();
		}
		
		getHosts.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				load = ProgressDialog.show(AutoHostsActivity.this, "Please standby...", "Now retrieving hosts.", true);
				new Thread(new Runnable() {
					public void run() {
						getContent(Constants.hosts);
						Message msg = new Message();
						msg.what = 1;
						mHandler.sendMessage(msg);
						load.dismiss();
					}
				}).start();

			}
		});
		
		setHosts.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				updateHosts();
				Toast.makeText(AutoHostsActivity.this, R.string.host_success, Toast.LENGTH_LONG);
			}
		});
		
		revertHosts.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				revertHosts();
			}
		});
	}
	
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage (Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				try {
				    textView2.setText(R.string.current_ver);
					version.setText(getVersion(Constants.svn));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;

			default:
				break;
			}
		}
	};

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
	
	/**
	 * updateHosts() - Method for updating and replacing hosts file.
	 * 
	 */
	public void updateHosts() {
		textView2.setText(R.string.label_updating);
		
		//Mount /system as read-write
		su.Run("mount -o remount,rw -t yaffs2 /dev/block/mtdblock3 /system");
		Log.d("AutoHosts", "/system R/W mounted");
		version.setText("/system R/W mounted\n", TextView.BufferType.EDITABLE);
		
		//Backup the original one
		su.Run("mv /system/etc/hosts /system/etc/hosts.bak");
		Log.d("AutoHosts", "Backup hosts as hosts.bak");
		version.append("Hosts file backup as hosts.bak.\n");
		
		//Copy the newer hosts to replace the older system one
		su.Run("cp /data/data/com.yeyaxi.AutoHosts/files/hosts /system/etc/hosts");
		Log.d("AutoHosts","Hosts copied to /etc");
		version.append("Hosts is copied to /etc\n");
		
		//Fix the permission
		su.Run("chmod 644 /system/etc/hosts");
		Log.d("AutoHosts","Hosts ready to use");
		version.append("Success! The hosts file is ready to use!");
	}
	
	public void revertHosts() {
		textView2.setText(R.string.label_reverting);
		//Delete the downloaded one.
		su.Run("rm -rf /system/etc/hosts");
		Log.d("AutoHosts", "Delete the download one");
		version.setText("The downloaded hosts has been removed.\n", TextView.BufferType.EDITABLE);
		
		//Revert to the original one.
		su.Run("mv /system/etc/hosts.bak /system/etc/hosts");
		Log.d("AutoHosts", "Revert to the original one");
		version.append("Reverting to the original hosts...\nSuccess!");
	}
	/**
	 * getVersion - Method for retrieving SVN info
	 * @param s - Input URL
	 * @return - Returning String version
	 * @throws IOException
	 */
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