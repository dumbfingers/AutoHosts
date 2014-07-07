package com.yeyaxi.android.autohosts;

import android.annotation.TargetApi;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;

/**
 * Created by allen on 07/07/14.
 */
public class BaseActivity extends SherlockActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTranslucentStatus(true);
    }

    public static void setFont(TextView textView) {
        Typeface tf = Typeface.createFromAsset(textView.getContext().getAssets(), "fonts/OCRAStd.otf");
        textView.setTypeface(tf);
    }

    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        final int bits2 = WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;
        if (on) {
            winParams.flags |= bits;
            winParams.flags |= bits2;
        } else {
            winParams.flags &= ~bits;
            winParams.flags &= ~bits2;
        }
        win.setAttributes(winParams);
    }

}
