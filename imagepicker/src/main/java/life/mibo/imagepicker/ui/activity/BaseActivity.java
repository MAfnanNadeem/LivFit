/*
 *  Created by Sumeet Kumar on 4/11/20 12:06 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 4/11/20 12:06 PM
 *  Mibo Hexa - imagepicker
 */

package life.mibo.imagepicker.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import life.mibo.imagepicker.BuildConfig;

import life.mibo.imagepicker.Configuration;
import life.mibo.imagepicker.utils.Logger;


public abstract class BaseActivity extends AppCompatActivity {

    public static final String EXTRA_PREFIX = BuildConfig.APPLICATION_ID;
    public static final String EXTRA_CONFIGURATION = EXTRA_PREFIX + ".Configuration";

    private final String CLASS_NAME = getClass().getSimpleName();

    public Configuration mConfiguration;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        printActivityLife("onCreate");
        Intent intent = getIntent();
        //androidx.lifecycle.Lifecycle.
        Bundle bundle = null;
        if (intent != null) {
            bundle = intent.getExtras();
        }


        if (savedInstanceState != null) {
            mConfiguration = savedInstanceState.getParcelable(EXTRA_CONFIGURATION);
        }
        if (mConfiguration == null && bundle != null) {
            mConfiguration = bundle.getParcelable(EXTRA_CONFIGURATION);
        }

        if (mConfiguration == null) {
            finish();
        } else {
            if (bundle == null) {
                bundle = savedInstanceState;
            }
            setContentView(getContentViewId());
            findViews();
            setTheme();
            onCreateView(bundle);
        }
    }

    @LayoutRes
    public abstract int getContentViewId();

    protected abstract void onCreateView(@Nullable Bundle savedInstanceState);

    @Override
    protected void onStart() {
        super.onStart();
        printActivityLife("onStart");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        printActivityLife("onRestart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        printActivityLife("onRestart");
    }

    @Override
    protected void onPause() {
        super.onPause();
        printActivityLife("onPause");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        printActivityLife("onDestroy");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        printActivityLife("onSaveInstanceState");
        outState.putParcelable(EXTRA_CONFIGURATION, mConfiguration);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        printActivityLife("onRestoreInstanceState");
        mConfiguration = savedInstanceState.getParcelable(EXTRA_CONFIGURATION);
    }

    public abstract void findViews();

    protected abstract void setTheme();

    private void printActivityLife(String method) {
        Logger.i(String.format("Activity:%s Method:%s", CLASS_NAME, method));
    }
}
