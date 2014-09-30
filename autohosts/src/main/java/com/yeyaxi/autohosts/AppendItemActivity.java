package com.yeyaxi.AutoHosts;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

/**
 * @author Yaxi Ye
 */
public class AppendItemActivity extends BaseActivity
{

	EditText items;
	Button ok;
	Button cancel;

	public void onCreate (Bundle icicle)
	{

		super.onCreate(icicle);
		this.setContentView(R.layout.appenditem);
		items = (EditText) this.findViewById(R.id.txt_addhosts);
		ok = (Button) this.findViewById(R.id.btn_ok);
		cancel = (Button) this.findViewById(R.id.btn_cancel);

	}

	public void onStart ()
	{
		super.onStart();

		ok.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick (View v)
			{
				Intent intent = new Intent();
				intent.putExtra("NewEntry", items.getText().toString());
				AppendItemActivity.this.setResult(BaseActivity.APPEND_ITEM_REQUEST_CODE, intent);
				AppendItemActivity.this.finish();
			}

		});

		cancel.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick (View v)
			{
				finish();
		}

		});

	}
}
