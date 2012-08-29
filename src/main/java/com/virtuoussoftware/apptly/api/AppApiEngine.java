package com.virtuoussoftware.apptly.api;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.preference.PreferenceActivity.Header;
import android.util.Log;

import com.google.gson.Gson;
import com.virtuoussoftware.apptly.auth.Account;
import com.virtuoussoftware.apptly.auth.Credentials;
import com.virtuoussoftware.apptly.entities.*;

public class AppApiEngine {
  private static final String TAG = "AppApiEngine"; 
  
  static final String rawApiGet(URL target, String authToken) throws IOException {
    HttpURLConnection conn = (HttpURLConnection) target.openConnection();
    try {
      conn.setRequestProperty("Authorization", "Bearer " + authToken);
      InputStream in = new BufferedInputStream(conn.getInputStream());
      StringBuffer buffer = new StringBuffer();
      BufferedReader reader = new BufferedReader(new InputStreamReader(in));
      String i = null;
      while((i = reader.readLine()) != null) {
        buffer.append(i);
      }
      
      Log.d(TAG, "Brace yourself, here it comes!");
      Log.d(TAG, buffer.toString());
      return buffer.toString();
    }
    catch (IOException e) {
      Log.e(TAG, "Trying to fetch, we failed.", e);
      // TODO Handle error sensibly with getErrorStream here!
      throw e;
    }
    finally {
      // Hmm, will this fire too early?
      conn.disconnect();
    }
  }
  
  public static final Profile getSelfProfile(String authToken) {
    URL target = null;
    try { target = new URL("https://alpha-api.app.net/stream/0/token"); }
    catch(MalformedURLException e) {
      Log.e(TAG, "Something totally insane happened.", e);
    }
    
    try {
      String r = rawApiGet(target, authToken);
      Gson parser = new Gson();
      
      TokenDescription result = parser.fromJson(r, TokenDescription.class);
      Log.d(TAG, result.toString());
      Log.d(TAG, result.user.name.toString());
      return result.user;
    }
    catch(IOException e) {
      Log.e(TAG, "No idea what to do yet.");
    }
    return null;
  }
}
