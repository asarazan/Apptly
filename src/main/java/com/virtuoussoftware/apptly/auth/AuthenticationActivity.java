package com.virtuoussoftware.apptly.auth;

import com.virtuoussoftware.apptly.R;
import com.virtuoussoftware.apptly.api.AppApiEngine;
import com.virtuoussoftware.apptly.entities.Profile;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Loader;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.net.Uri;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

@SuppressLint("SetJavaScriptEnabled")
public class AuthenticationActivity extends Activity {
  private final String TAG = this.getClass().getName();
  private final int LOAD_PROFILE = 0;
  
  private WebView wv;
  private ProgressBar progressBar;
  private ProgressDialog progressSpinner;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
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
      progressSpinner = ProgressDialog.show(this, getString(R.string.authentication_waiting_title),
                                                  getString(R.string.authentication_waiting_message));
      /* TODO: Pull down user's profile synchronously and get the username and email to populate some 
       *       essential details.
       */
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
                return AppApiEngine.getSelfProfile(new Credentials());
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
          anchor.onProfileLoaded(arg1);
        }

        @Override public void onLoaderReset(Loader<Profile> arg0) {}
        
      });
      l.forceLoad();
    }
    catch( Exception e ) {
      progressSpinner.cancel();
    }
  }
  
  protected void onProfileLoaded(Profile profile) {
    Log.d(TAG, "Profile loaded!");
    Log.d(TAG, "Hello, " + profile.username);
    
    // TODO Create new account instance.
    
    progressSpinner.dismiss();
    finish(); 
  }

  private final Uri generateOauth2Uri(String client_id, String client_secret) {
    Uri.Builder builder = new Uri.Builder();
    builder.scheme("https");
    builder.appendEncodedPath(AuthenticationRules.BASE_URI);
    builder.appendQueryParameter("client_id", client_id);
    builder.appendQueryParameter("client_secret", client_secret);
    builder.appendQueryParameter("redirect_uri", AuthenticationRules.REDIRECT_BASIS);
    builder.appendQueryParameter("response_type", "token");
    builder.appendQueryParameter("scopes", AuthenticationRules.SCOPES);
    
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
      public void onPageStarted(WebView view, String url, Bitmap favicon) {
        if(url.startsWith(AuthenticationRules.REDIRECT_BASIS)) {
          Log.d(TAG, "We got a mad token yo!");          
          view.stopLoading();
          int offset = url.indexOf("=");
          anchor.handleAuthToken(url.substring(offset+1));
        }
        else {
          Log.d(TAG, "Unexciting! " + url);
        }
        super.onPageFinished(view, url);
      }
    });
  }
}
