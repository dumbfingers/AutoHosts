package com.yeyaxi.AutoHosts;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class AutoHostsActivity extends Activity {
    /** Called when the activity is first created. */
	Su su = new Su();
	GetURL getURL = new GetURL();
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
    			getURL.getContent(Constants.hosts);
    		}
    	});
    }
}