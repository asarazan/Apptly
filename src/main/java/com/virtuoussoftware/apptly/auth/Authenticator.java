package com.virtuoussoftware.apptly.auth;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class Authenticator extends AbstractAccountAuthenticator {

  final private String TAG = "ApptlyAuthenticator"; 
  private final Context mContext;

  public Authenticator(Context context) {
    super(context);
    mContext = context;
  }

  @Override
  public Bundle addAccount(AccountAuthenticatorResponse response,
      String accountType, String authTokenType, String[] requiredFeatures,
      Bundle options) throws NetworkErrorException {
    
    Log.d(TAG, "Add Account called!");
    final Intent i = new Intent(mContext, AuthenticationActivity.class);
    i.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
    final Bundle result = new Bundle();
    result.putParcelable(AccountManager.KEY_INTENT, i);
    Log.d(TAG, "shipping back a parcelable intent for " + AccountManager.KEY_INTENT + " aiming at " + AuthenticationActivity.class.getName());
    return result;
  }

  @Override
  public Bundle confirmCredentials(AccountAuthenticatorResponse response,
      Account account, Bundle options) throws NetworkErrorException {
    // Right now, we'll skip this. I think this is used in other applications
    // we don't need using oAuth
    return null;
  }

  @Override
  public Bundle editProperties(AccountAuthenticatorResponse response,
      String accountType) {
    // TODO Support this.
    throw new UnsupportedOperationException();
  }

  @Override
  public Bundle getAuthToken(AccountAuthenticatorResponse response,
      Account account, String authTokenType, Bundle options)
      throws NetworkErrorException {
    
    final Bundle result = new Bundle();
    if(!authTokenType.equals(AuthenticationConstants.AUTHTOKEN_TYPE)) {
      result.putString(AccountManager.KEY_ERROR_MESSAGE, "Invaid authTokenType!");
      return result;
    }
    
    final AccountManager am = AccountManager.get(mContext);
    final String password = am.getPassword(account);
    if(password != null) {
      // We don't store the user's password, we just store the token.
      // This is arguably better than keeping the password so we don't do further 
      // encryption.
      
      final String authToken = password;
      result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
      result.putString(AccountManager.KEY_ACCOUNT_TYPE, AuthenticationConstants.ACCOUNT_TYPE);
      result.putString(AccountManager.KEY_AUTHTOKEN, password);
    }
    else {
      final Intent goToAuth = new Intent(mContext, AuthenticationActivity.class);
      goToAuth.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
      result.putParcelable(AccountManager.KEY_INTENT, goToAuth);
    }
    
    return result;
  }

  @Override
  public String getAuthTokenLabel(String authTokenType) {
    // TODO Auto-generated method stub
    return AuthenticationConstants.SCOPES;
  }

  @Override
  public Bundle hasFeatures(AccountAuthenticatorResponse response,
      Account account, String[] features) throws NetworkErrorException {
    final Bundle result = new Bundle();
    result.putBoolean(AccountManager.KEY_BOOLEAN_RESULT, true); // We only get all the features.
    return result;
  }

  @Override
  public Bundle updateCredentials(AccountAuthenticatorResponse response,
      Account account, String authTokenType, Bundle options)
      throws NetworkErrorException {
    // TODO Auto-generated method stub
    return null;
  }

}
