package com.virtuoussoftware.apptly.auth;

class AuthenticationRules {
  public static final String BASE_URI = "/alpha.app.net/oauth/authenticate";
  
  // TODO: Find a better value for this. Right now this only works because my website doesn't accept https.
  public static final String REDIRECT_BASIS = "https://dave.fayr.am";
  
  public static final String SCOPES = "stream email write_post follow messages export";
}