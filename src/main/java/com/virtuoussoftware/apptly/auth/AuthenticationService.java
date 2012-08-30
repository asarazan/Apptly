package com.virtuoussoftware.apptly.auth;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class AuthenticationService extends Service {

  private Authenticator authenticator;
  
  @Override
  public void onCreate() {
    if(Log.isLoggable("APP_AUTH", Log.DEBUG)) {
      Log.d("APP_AUTH", "Heyo I, the service, am awake!");
    }
    super.onCreate();
    authenticator = new Authenticator(this);
  }
  
  @Override
  public IBinder onBind(Intent intent) {
    if(Log.isLoggable("APP_AUTH", Log.DEBUG)) {
      Log.d("APP_AUTH", "sending a binder!");
    }
    return authenticator.getIBinder();
  }

}
