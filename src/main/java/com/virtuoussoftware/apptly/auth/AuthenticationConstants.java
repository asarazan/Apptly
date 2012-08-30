package com.virtuoussoftware.apptly.auth;

public class AuthenticationConstants {
  public static final String BASE_URI = "/alpha.app.net/oauth/authenticate";
  
  // TODO: Find a better value for this. Right now this only works because my website doesn't accept https.
  public static final String REDIRECT_BASIS = "https://dave.fayr.am";
  
  public static final String SCOPES = "stream email write_post follow messages export";
  
  public final static String AUTHTOKEN_TYPE = "com.virtuoussoftware.apptly.oauth2_token";
  public final static String ACCOUNT_TYPE = "com.virtuoussoftware.apptly";
  
  
  
}