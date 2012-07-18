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


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

/**
 * @author Yaxi Ye
 */
public class AppendItemActivity extends Activity
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
				AppendItemActivity.this.setResult(Constants.APPEND_ITEM_REQUEST_CODE, intent);
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
