package com.virtuoussoftware.apptly;

import java.io.IOException;

import com.virtuoussoftware.apptly.auth.Account;
import com.virtuoussoftware.apptly.auth.AuthenticationActivity;
import com.virtuoussoftware.apptly.auth.AuthenticationConstants;
import com.virtuoussoftware.apptly.auth.Credentials;
import com.virtuoussoftware.apptly.jankotron.SlowTabulizerActivity;

import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class StreamListActivity extends FragmentActivity
        implements StreamListFragment.Callbacks {

    private final String TAG = this.getClass().getName();
    private final int AUTH_CODE = 1;
    private boolean mTwoPane;
    private AccountManager accountManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stream_list);
        accountManager = AccountManager.get(this);

        if (findViewById(R.id.stream_detail_container) != null) {
            mTwoPane = true;
            ((StreamListFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.stream_list))
                    .setActivateOnItemClick(true);
        }
    }

    @Override
    public void onItemSelected(String id) {
        if (mTwoPane) {
            Bundle arguments = new Bundle();
            arguments.putString(StreamDetailFragment.ARG_ITEM_ID, id);
            StreamDetailFragment fragment = new StreamDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.stream_detail_container, fragment)
                    .commit();

        } else {
            Intent detailIntent = new Intent(this, StreamDetailActivity.class);
            detailIntent.putExtra(StreamDetailFragment.ARG_ITEM_ID, id);
            startActivity(detailIntent);
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.standard, menu);
    	return true;
    }
    
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
      Intent intent;
    	switch (item.getItemId()) {
    	case R.id.janky_stream_menu_item:
    		intent = new Intent(this, SlowTabulizerActivity.class);
    	    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    	    startActivity(intent);
    		return true;
    	case R.id.auth_test_menu_item:
    	  android.accounts.Account[] currentAccounts = accountManager.getAccountsByType(AuthenticationConstants.ACCOUNT_TYPE);
    	  Log.d(TAG,"I am aware of " + currentAccounts.length + " account(s)");
    	  if(currentAccounts.length == 0) {
    	    Log.d(TAG, "Going to make one from scratch.");
    	     String[] features = {};
    	     final Activity anchor = this;
    	     //Intent keyIntent = new Intent(this, AuthenticationActivity.class);
    	     //anchor.startActivityForResult(keyIntent, AUTH_CODE);
    	     accountManager.addAccount(AuthenticationConstants.ACCOUNT_TYPE, 
    	                               AuthenticationConstants.AUTHTOKEN_TYPE, 
    	                               features, 
    	                               new Bundle(), 
    	                               null, 
    	                               new AccountManagerCallback<Bundle>() {

                                      @Override
                                      public void run(AccountManagerFuture<Bundle> notSoFuture) {
                                        // In this case it should be okay to call the future. Clients of us may 
                                        // not be so lucky!
                                        try {
                                          Bundle result = notSoFuture.getResult();
                                          Intent keyIntent = result.getParcelable(AccountManager.KEY_INTENT);
                                          if(keyIntent == null) { throw new Exception("I AM BEREFT OF A KEY INTNET!"); }
                                          anchor.startActivityForResult(keyIntent, AUTH_CODE);
                                        }
                                        catch( Exception e ) {
                                          Log.e(TAG, "A really impossible exception occured. addActivity just doesn't do much.", e);
                                        }
                                        
                                      }
                                    }, null);
    	     Log.d(TAG, "Taptap");
    	     Log.d(TAG, "We'll get back to you later!");
    	     return true;
    	  }
    	 
        setCurrentAccount(currentAccounts);
    	  return true;
      case R.id.apptly_log_out:
        Account.setCurrentAccount(null, this);
        return true;
    	}
    	
    	return super.onMenuItemSelected(featureId, item);
    }

    private void setCurrentAccount(android.accounts.Account[] currentAccounts) {
      if(currentAccounts == null) {
        currentAccounts = accountManager.getAccountsByType(AuthenticationConstants.ACCOUNT_TYPE);
      }
      
      final android.accounts.Account[] accts = currentAccounts;
      
      Log.d(TAG,"I am now aware of " + currentAccounts.length);
      if(currentAccounts.length > 0) {
        final StreamListActivity anchor = this;
        android.accounts.Account account = currentAccounts[0];
        AsyncTask<android.accounts.Account, Integer, Void> getter = new AsyncTask<android.accounts.Account, Integer, Void>() {
            @Override
            protected Void doInBackground(android.accounts.Account... params) {
              android.accounts.Account account = params[0];
              String authToken = "";
              try {
                authToken = accountManager.blockingGetAuthToken(account, AuthenticationConstants.AUTHTOKEN_TYPE, false);
              }
              catch (OperationCanceledException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
              }
              catch (AuthenticatorException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
              }
              catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
              }
              Account.setCurrentAccount(new Account(authToken, account.name), anchor);
              Log.v(TAG, "Set current account successfully. Hello, " + account.name);
              return null;
            }
        };
        getter.execute(account);
      }
      else {
        Log.e(TAG, "I am at a loss as for how to proceed. No accounts but you swore to me there would be some.");
      }
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent callerIntent) {
      super.onActivityResult(requestCode, resultCode, callerIntent);
      if(requestCode == AUTH_CODE && resultCode == RESULT_OK) {
        Bundle b = callerIntent.getExtras();
        Log.d(TAG, "Result was: AccountManager.KEY_BOOLEAN_RESULT");
        setCurrentAccount(null);
      }
    }
}
