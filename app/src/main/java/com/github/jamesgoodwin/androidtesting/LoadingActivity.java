package com.github.jamesgoodwin.androidtesting;

import android.app.Activity;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.support.test.espresso.IdlingResource;

public class LoadingActivity extends Activity implements LoaderManager.LoaderCallbacks<Boolean>, IdlingResource {

    private ProgressDialog progressDialog;   
    private ResourceCallback resourceCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.deckard);

        LoaderManager loaderManager = getLoaderManager();
        Loader loader = loaderManager.getLoader(LongRunningLoader.class.hashCode());

        if (loader == null) {
            loaderManager.initLoader(LongRunningLoader.class.hashCode(), null, this);
        } else {
            loaderManager.restartLoader(LongRunningLoader.class.hashCode(), null, this);
        }
    }

    @Override
    public Loader<Boolean> onCreateLoader(int i, Bundle bundle) {
        progressDialog = ProgressDialog.show(
                this,
                getString(R.string.loading),
                getString(R.string.long_running_task_message),
                true,
                true,
                new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {

                    }
                });
        return new LongRunningLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<Boolean> booleanLoader, Boolean aBoolean) {
        hideProgressDialog();
        
        Intent intent = new Intent(this, ResultActivity.class);
        startActivity(intent);

        resourceCallback.onTransitionToIdle();
    }

    private void hideProgressDialog() {
        if(progressDialog != null) {
            progressDialog.hide();
        }
    }

    @Override
    public void onLoaderReset(Loader<Boolean> booleanLoader) {
    }

    @Override
    public String getName() {
        return "LoadingActivity idling resource";
    }

    @Override
    public boolean isIdleNow() {
        return !progressDialog.isShowing();
    }

    @Override
    public void registerIdleTransitionCallback(ResourceCallback resourceCallback) {
        this.resourceCallback = resourceCallback;
    }
}