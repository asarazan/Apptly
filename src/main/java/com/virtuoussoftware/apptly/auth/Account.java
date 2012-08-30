package com.virtuoussoftware.apptly.auth;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import com.virtuoussoftware.apptly.entities.Profile;

/**
 * Account
 * Created by Aaron Sarazan on 8/28/12
 * <p/>
 * Copyright 2012 Virtuous Software. All rights reserved.
 */

public class Account {

  private static final String PREFS_NAME = "apptly_account";
  private static final String PREFS_USERNAME = "username";
  private static final String PREFS_TOKEN= "token";

  private static boolean hasAttemptedLoadFromDisk = false;
  private static Account currentAccount = null;

  public final String username;
  public final String authToken;

  // To shut IntelliJ up.
  private Account() {
    this.username = null;
    this.authToken = null;
  }

  public Account(String authToken, Profile profile) {
    this.username = profile.username;
    this.authToken = authToken;
  }
  
  public Account(String authToken, String userid) {
    this.username = userid;
    this.authToken = authToken;
  }

  public static Account getCurrentAccount(Context context) {
    if (currentAccount == null && !hasAttemptedLoadFromDisk) {
      hasAttemptedLoadFromDisk = true;
      SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);

      String username = prefs.getString(PREFS_USERNAME, null);
      String authToken = prefs.getString(PREFS_TOKEN, null);

      if (authToken != null && username != null) {
        Profile profile = new Profile();
        profile.username = username;
        currentAccount = new Account(authToken, profile);
      }
    }
    return currentAccount;
  }

  public static void setCurrentAccount(Account account, Context context) {
    currentAccount = account;
    if (account != null) {
      account.persist(context);
    } else {
      clearFromDisk(context);
    }
  }

  private void persist(Context context) {
    SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
    SharedPreferences.Editor editor = prefs.edit();
    editor.putString(PREFS_USERNAME, username);
    editor.putString(PREFS_TOKEN, authToken);
    editor.commit();
  }

  private static void clearFromDisk(Context context) {
    SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
    SharedPreferences.Editor editor = prefs.edit();
    editor.clear();
    editor.commit();
  }
}
