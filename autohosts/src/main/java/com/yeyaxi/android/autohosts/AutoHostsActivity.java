package com.yeyaxi.android.autohosts;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.apache.http.util.ByteArrayBuffer;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * AutoHosts - An app for android to update hosts.
 *
 * @author Yaxi Ye
 * @version 1.0.2
 * @since Nov.7.2011
 */
public class AutoHostsActivity extends BaseActivity
{

    private static final String TAG = AutoHostsActivity.class.getSimpleName();

	private enum TASK
	{
		BACKUP_ENTRIES, LOAD_NEW_ENTRIES, DOWNLOAD_NEW_ENTRIES, REVERT_ENTRIES, DELETE_DOWNLOADED_ENTRIES, DELETE_BACKUP, DISPLAY_SUCCESS_MESSAGE, DISPLAY_REVERT_MESSAGE, CREATE_BLANK_FILE_, LOAD_BLANK_FILE
	}

	private static final DateFormat df = DateFormat.getTimeInstance(DateFormat.MEDIUM);
	private Queue<TASK> taskQueue = null;
	private TextView version;
	private TextView textView2;
	private Button getHosts;
	private Button setHosts;
	private Button revertHosts;
	private ProgressDialog load = null;
	private AdView adView;

	@Override
	public void onCreate (Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		version = (TextView) findViewById(R.id.ver);
        // apply fonts
        setFont(version);

		getHosts = (Button) findViewById(R.id.getHosts);
		setHosts = (Button) findViewById(R.id.setHosts);
		revertHosts = (Button) findViewById(R.id.revertHosts);
		textView2 = (TextView) findViewById(R.id.textView2);
		// Create the adView
	    adView = new AdView(this);
        adView.setAdSize(com.google.android.gms.ads.AdSize.SMART_BANNER);
        adView.setAdUnitId(BaseActivity.MY_AD_UNIT_ID);
	    // Lookup your LinearLayout assuming its been given
	    // the attribute android:id="@+id/mainLayout"
	    LinearLayout layout = (LinearLayout)findViewById(R.id.adLayout);

	    // Add the adView to it
	    layout.addView(adView);

        AdRequest.Builder adRequestBuilder = new AdRequest.Builder();

	    // Initiate a generic request to load it with an ad
	    adView.loadAd(adRequestBuilder.build());

//			version.setText(getString(R.string.current_ver) + getVersionTask.execute(BaseActivity.PROJECTH));
        getVersionTask.execute(BaseActivity.PROJECTH);
//		} catch (IOException ex)
//		{
//			Log.d(BaseActivity.LOG_NAME, "Error getting version", ex);
//			version.setText(getString(R.string.current_ver) + " Unknown");
//		}
		taskQueue = new LinkedList<TASK>();
	}

	@Override
	  public void onDestroy() {
	    if (adView != null) {
	      adView.destroy();
	    }
	    super.onDestroy();
	  }
	
	
	public void onResume ()
	{
		super.onResume();
		if (!RootChecker.hasRoot())
		{
			Toast.makeText(this, R.string.err_no_root, Toast.LENGTH_SHORT).show();
		}

		getHosts.setOnClickListener(new OnClickListener()
		{
			public void onClick (View v)
			{
				taskQueue.clear();
				addTask(TASK.BACKUP_ENTRIES);
				addTask(TASK.DOWNLOAD_NEW_ENTRIES);
				addTask(TASK.LOAD_NEW_ENTRIES);
				doNextTask();
			}
		});

		setHosts.setOnClickListener(new OnClickListener()
		{
			public void onClick (View v)
			{
				taskQueue.clear();
				displayCalbackMessage(R.string.label_updating);
				addTask(TASK.BACKUP_ENTRIES);
				addTask(TASK.LOAD_NEW_ENTRIES);
				addTask(TASK.DISPLAY_SUCCESS_MESSAGE);
				doNextTask();
			}
		});

		revertHosts.setOnClickListener(new OnClickListener()
		{
			public void onClick (View v)
			{
				taskQueue.clear();
				displayCalbackMessage(R.string.label_reverting);
				addTask(TASK.REVERT_ENTRIES);
				addTask(TASK.DELETE_DOWNLOADED_ENTRIES);
				addTask(TASK.DELETE_BACKUP);
				addTask(TASK.DISPLAY_REVERT_MESSAGE);
				doNextTask();
			}
		});
	}

	//For Menus
	@Override
	public boolean onCreateOptionsMenu (Menu menu)
	{
		getSupportMenuInflater().inflate(R.menu.main_menu, menu);
		return true;

	}

	public boolean onOptionsItemSelected (MenuItem item)
	{
		//Handle menu item selection
		switch (item.getItemId())
		{
			case R.id.fix_dns:
				//Add dialog for DNS
				AlertDialog.Builder builderDNS = new AlertDialog.Builder(this);
				builderDNS.setMessage(R.string.dialog_dns);
//			builderDNS.setCancelable(true);
				builderDNS.setPositiveButton("OK to Proceed", new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick (DialogInterface dialog, int id)
					{
						//Set 3G DNS
						String[] commands = new String[4];
						commands[0] = "setprop net.rmnet0.dns1 8.8.8.8";
						commands[1] = "setprop net.rmnet0.dns2 208.67.220.220";

						//Set DNS
						commands[2] = "setprop net.dns1 8.8.8.8";
						commands[3] = "setprop net.dns2 208.67.220.220";

						new CommandRunner(AutoHostsActivity.this, R.string.label_fixingDNS).execute(commands);

						dialog.dismiss();
					}
				});
				builderDNS.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener()
				{

					@Override
					public void onClick (DialogInterface dialog, int which)
					{
						dialog.cancel();
					}

				});

				AlertDialog alertDNS = builderDNS.create();
				alertDNS.show();
				break;

			case R.id.add_hosts_entry:
				//Add dialog for add entry

				//Call another activity to handle this.
				Intent intent = new Intent(getApplicationContext(), AppendItemActivity.class);
				this.startActivityForResult(intent, BaseActivity.APPEND_ITEM_REQUEST_CODE);

				break;

			case R.id.revert_blank:
				taskQueue.clear();
				addTask(TASK.BACKUP_ENTRIES);
				addTask(TASK.LOAD_BLANK_FILE);
				addTask(TASK.DISPLAY_REVERT_MESSAGE);
				doNextTask();
				break;

			case R.id.about:
				//Add dialog for About
				AlertDialog.Builder builderAbout = new AlertDialog.Builder(this);
				builderAbout.setMessage(R.string.dialog_about);
				builderAbout.setTitle(R.string.about);
				builderAbout.setCancelable(true);
				builderAbout.setPositiveButton("OK", new DialogInterface.OnClickListener()
				{

					@Override
					public void onClick (DialogInterface dialog, int which)
					{
						dialog.cancel();
					}
				});
				AlertDialog alertAbout = builderAbout.create();
				alertAbout.show();
				break;
		}
		return true;

	}

	@Override
	protected void onActivityResult (int requestCode, int resultCode, Intent data)
	{
		if (resultCode == BaseActivity.APPEND_ITEM_REQUEST_CODE)
		{
			Serializable newEntry = data.getSerializableExtra("NewEntry");
			if (newEntry != null)
			{
				addEntryToFile(newEntry.toString(), "newHost");
				new FileCopier(AutoHostsActivity.this, R.string.addingHostsToFile, true).execute(getFilesDir() + "/newHost", "/system/etc/hosts");
			}
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	public void loadHostsFromInputStream (InputStream inputStream)
	{
		if (inputStream == null)
			displayCalbackErrorMessage(R.string.label_updating);
		else
		{
			new FileCopier(AutoHostsActivity.this, R.string.label_updating, false).execute(inputStream, getFilesDir() + "/hosts");
			displayCalbackMessage(R.string.host_pulled);
		}
	}

	/**
	 * getVersion - Method for retrieving SVN info
	 *
	 * @param f - Input file
	 * @throws IOException
	 */
	public void setVersion (File f) throws IOException {

		String versionStr = "";

        BufferedReader br = new BufferedReader(new FileReader(f));
        String line;
        while ((line = br.readLine()) != null) {
            // process the line.
            if (line.startsWith(BaseActivity.TIMESTAMP_PREFIX)) {
                versionStr = line.substring(BaseActivity.TIMESTAMP_PREFIX.length() + 1);
                break;
            }
        }
        br.close();
        version.setText(BaseActivity.PROJECTH + "\n" + getString(R.string.current_ver) + versionStr);
	}

    AsyncTask<String, Void, File> getVersionTask = new AsyncTask<String, Void, File>() {
        @Override
        protected File doInBackground(String... params) {
            File f = null;
            try {
                URL url = new URL(params[0]);
                URLConnection ucon = url.openConnection();
                InputStream is = ucon.getInputStream();
                BufferedInputStream bis = new BufferedInputStream(is);

                ByteArrayBuffer baf = new ByteArrayBuffer(50);
                int current = 0;
                while ((current = bis.read()) != -1) {
                    baf.append((byte) current);
                }

                f = new File(getExternalCacheDir(), "hosts");
                FileOutputStream fos = new FileOutputStream(f);
                fos.write(baf.toByteArray());
                fos.close();
            } catch (MalformedURLException mue) {
                mue.printStackTrace();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
            return f;
        }

        @Override
        public void onPostExecute(File f) {
            if (f != null) {
                try {
                    setVersion(f);
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }
    };

	public void displayCalbackErrorMessage (final int... completionMessage)
	{
		runOnUiThread(new Runnable()
		{
			@Override
			public void run ()
			{
				if (load != null && load.isShowing())
					load.dismiss();
				StringBuilder sb = new StringBuilder();
				for (int messageId : completionMessage)
					sb.append(getString(messageId));
				sb.append(getString(R.string.errorPrefix));
				textView2.setText(getTimeStamp() + sb.toString() + "\n" + textView2.getText());
			}
		});
	}

	public void displayCalbackMessage (final int... id)
	{
		runOnUiThread(new Runnable()
		{
			@Override
			public void run ()
			{
				if (load != null && load.isShowing())
					load.dismiss();
				StringBuilder sb = new StringBuilder();
				for (int messageId : id)
					sb.append(getString(messageId));
				textView2.setText(getTimeStamp() + sb.toString() + "\n" + textView2.getText());
			}
		});
	}
	
	private String getTimeStamp()
	{
		return "[" + df.format(new Date()) + "] ";
	}

	private void addEntryToFile (String entry, String fileName)
	{
		FileOutputStream fOut = null;
		OutputStreamWriter osw = null;
		try
		{
			fOut = openFileOutput(fileName, MODE_PRIVATE);
			osw = new OutputStreamWriter(fOut);
			osw.write(entry);
			osw.flush();
		} catch (Exception e)
		{
			Log.d("AutoHosts", "Creating blank hosts file error.");
		} finally
		{
			closeStream(osw);
			closeStream(fOut);
		}
	}

	private void closeStream (Closeable closeable)
	{
		if (closeable != null)
		{
			try
			{
				closeable.close();
			} catch (IOException ex)
			{
				Log.d(TAG, "Error closing stream.", ex);
			}
		}
	}


	public void addTask (TASK nextTask)
	{
		if (taskQueue == null)
			taskQueue = new PriorityQueue<TASK>();

		taskQueue.add(nextTask);
	}

	public void doNextTask ()
	{
		if (load != null && load.isShowing())
			load.hide();
		if (taskQueue != null && taskQueue.peek() != null)
		{
			switch (taskQueue.remove())
			{
				case BACKUP_ENTRIES:
					displayCalbackMessage(R.string.label_backingup);
					new FileCopier(AutoHostsActivity.this, R.string.label_backingup, false).execute("/system/etc/hosts", getFilesDir() + "/hosts.bak");
					break;
				case LOAD_NEW_ENTRIES:
					displayCalbackMessage(R.string.label_loadingFile);
					new FileCopier(AutoHostsActivity.this, R.string.label_loadingFile, false).execute(getFilesDir() + "/hosts", "/system/etc/hosts");
					break;
				case DOWNLOAD_NEW_ENTRIES:
					load = ProgressDialog.show(AutoHostsActivity.this, getString(R.string.dialog_title_load), getString(R.string.dialog_txt_load), true);
					new WebFileDownloader(AutoHostsActivity.this).execute(BaseActivity.PROJECTH);
					break;
				case REVERT_ENTRIES:
					displayCalbackMessage(R.string.label_reverting);
					File backupFile = new File(getFilesDir() + "/hosts.bak");
					// Check if the file exists.  If it doesn't exist quickly make a new backup with only the localhost entry
					if (backupFile == null || !backupFile.isFile())
						addEntryToFile("127.0.0.1 localhost", "hosts.bak");
					new FileCopier(AutoHostsActivity.this, R.string.label_reverting, false).execute(getFilesDir() + "/hosts.bak", "/system/etc/hosts");
					break;
				case DELETE_DOWNLOADED_ENTRIES:
					displayCalbackMessage(R.string.label_deleteDownloadedFiles);
					new FileDeleter(AutoHostsActivity.this, R.string.label_deleteDownloadedFiles).execute(getFilesDir() + "/hosts");
					break;
				case DELETE_BACKUP:
					new FileDeleter(AutoHostsActivity.this, R.string.label_deletingBackupFiles).execute(getFilesDir() + "/hosts.bak");
					break;
				case LOAD_BLANK_FILE:
					addEntryToFile("127.0.0.1 localhost", "blank");
					new FileCopier(AutoHostsActivity.this, R.string.label_loadingFile, false).execute(getFilesDir() + "/blank", "/system/etc/hosts");
					break;
				case DISPLAY_SUCCESS_MESSAGE:
					displayCalbackMessage(R.string.host_success);
					doNextTask();
					break;
				case DISPLAY_REVERT_MESSAGE:
					displayCalbackMessage(R.string.label_reverting, R.string.append_success);
					doNextTask();
					break;

			}
		}
	}
}