package com.virtuoussoftware.apptly.auth;

import com.virtuoussoftware.apptly.R;
import com.virtuoussoftware.apptly.api.AppApiEngine;
import com.virtuoussoftware.apptly.entities.Profile;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.view.ViewManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.net.Uri;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

@SuppressLint("SetJavaScriptEnabled")
public class AuthenticationActivity extends AccountAuthenticatorActivity {
  private final String TAG = this.getClass().getName();
  private final int LOAD_PROFILE = 0;
  
  private WebView wv;
  private ProgressBar progressBar;
  private ProgressDialog progressSpinner;
  private CookieManager webViewCookieManager; 
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    CookieSyncManager.createInstance(this);
    webViewCookieManager = CookieManager.getInstance();
    webViewCookieManager.removeAllCookie();
    
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_authentication);
    wv = (WebView)findViewById(R.id.authWebView);
    progressBar = (ProgressBar)findViewById(R.id.authProgressBar);
 
    wv.getSettings().setJavaScriptEnabled(true);
    instrumentWebView(wv);
    
    Log.i(TAG, "Going to build URI!");
    
    Uri targetUri = generateOauth2Uri(Credentials.CLIENT_ID, Credentials.CLIENT_SECRET);
    
    Log.i(TAG, "Loading URI: " + targetUri.toString());
    wv.loadUrl(targetUri.toString());
  }
  
  @Override
  protected void onStart() {
    Uri targetUri = generateOauth2Uri(Credentials.CLIENT_ID, Credentials.CLIENT_SECRET);
    
    Log.i(TAG, "(Started) Loading URI: " + targetUri.toString());
    super.onStart();
  }
  
  
  private final void handleAuthToken(String isolatedToken) {
    Log.d(TAG, "Isolated token: " + isolatedToken);
    try {
      Log.d(TAG, "Going to fetch account details!");
      progressBar.setVisibility(View.GONE);
      progressSpinner = ProgressDialog.show(this, getString(R.string.authentication_waiting_title),
                                                  getString(R.string.authentication_waiting_message));
      final String auth = isolatedToken;
      final Context ctx = this;
      final AuthenticationActivity anchor = this;
      Bundle args = new Bundle();
      Loader<Profile> l = getLoaderManager().initLoader(LOAD_PROFILE, args, new LoaderManager.LoaderCallbacks<Profile>() {
        
        @Override
        public Loader<Profile> onCreateLoader(int id, Bundle args) {
          if(id == LOAD_PROFILE) {
            Log.d(TAG, "Creating task loader for profile.");
            return new AsyncTaskLoader<Profile>(ctx) {
              public Profile loadInBackground() {
                Log.d(TAG, "Running api engine call. Who knows what wil happen now?");
                return AppApiEngine.getSelfProfile(auth);
              };
            };
          }
          else {
            Log.d(TAG, "I got a loader id I don't get: " + id);
          }
          return null;
        }

        @Override
        public void onLoadFinished(Loader<Profile> arg0, Profile arg1) {
          Log.d(TAG, "LOAD FINISHED!");
          anchor.onProfileLoaded(arg1, auth);
        }

        @Override public void onLoaderReset(Loader<Profile> arg0) {}
        
      });
      l.forceLoad();
    }
    catch( Exception e ) {
      progressSpinner.cancel();
    }
  }
  
  protected void onProfileLoaded(Profile profile, String authToken) {
    progressSpinner.dismiss();
    if(profile != null && profile.username != null) {
      Log.d(TAG, "Got a profile, that profile has a name!");
      // Well then, let's do this.
      AccountManager am = AccountManager.get(this);
      final android.accounts.Account newAccount = 
          new android.accounts.Account(profile.username, AuthenticationConstants.ACCOUNT_TYPE);
      am.addAccountExplicitly(newAccount, authToken, new Bundle());
      
      final Intent intent = new Intent();
      intent.putExtra(AccountManager.KEY_BOOLEAN_RESULT, true);
      setAccountAuthenticatorResult(intent.getExtras());
      setResult(RESULT_OK, intent);
      Log.d(TAG, "Account was created: " + newAccount.toString());
      Log.d(TAG, "Result was okay");
      
      // TODO: Kick this off to disk persisting?
      //Account account = new Account(authToken, account.name);
      //Account.setCurrentAccount(account, this);
    }
    else {
      final Intent intent = new Intent();
      intent.putExtra(AccountManager.KEY_BOOLEAN_RESULT, false);
      setAccountAuthenticatorResult(intent.getExtras());
      setResult(RESULT_OK, intent);
    }
    webViewCookieManager.removeAllCookie(); // Either way, let's not save unnecessary data.
    finish(); 
  }

  private final Uri generateOauth2Uri(String client_id, String client_secret) {
    Uri.Builder builder = new Uri.Builder();
    builder.scheme("https");
    builder.appendEncodedPath(AuthenticationConstants.BASE_URI);
    builder.appendQueryParameter("client_id", client_id);
    builder.appendQueryParameter("client_secret", client_secret);
    builder.appendQueryParameter("redirect_uri", AuthenticationConstants.REDIRECT_BASIS);
    builder.appendQueryParameter("response_type", "token");
    builder.appendQueryParameter("scopes", AuthenticationConstants.SCOPES);
    
    return builder.build();
  }
  
  private void instrumentWebView(WebView v) {
    final AuthenticationActivity anchor = this;
    v.setWebChromeClient(new WebChromeClient() {
      @Override
      public void onProgressChanged(WebView view, int newProgress) {
        progressBar.setProgress(newProgress);
        super.onProgressChanged(view, newProgress);
      }
    });
    
    v.setWebViewClient(new WebViewClient() {
            
      @Override
      public void onPageFinished(WebView view, String url) {
        Log.d(TAG, "OnPageFinished: " + url);
        Log.d(TAG, "View says: " + view.getUrl());
        super.onPageFinished(view, url);
      }
      @Override
      public boolean shouldOverrideUrlLoading(WebView view, String url) {
        Log.d(TAG, "ShouldOverrideUrlLoading! Url is: " + url);
        if(url.startsWith("https://alpha.app.net") || url.startsWith(AuthenticationConstants.REDIRECT_BASIS)) {
          view.loadUrl(url);
          return true;
        }
        else { Log.d(TAG, "Refusing redirect to " + url); } 
        return false;
      }
      
      @Override
      public void onPageStarted(WebView view, String url, Bitmap favicon) {
        Log.d(TAG, "onpagestarted got " + url);
        Log.d(TAG, "view says: " + view.getUrl());
        if(url.startsWith(AuthenticationConstants.REDIRECT_BASIS)) {
          Log.d(TAG, "We got a mad token yo!");          
          view.stopLoading();
          int offset = url.indexOf("=");
          anchor.handleAuthToken(url.substring(offset+1));
        }
        else {
          Log.d(TAG, "Unexciting & Boring! " + url);
          //super.onPageStarted(view, url, favicon);
        }
        
      }
    });
  }
}
