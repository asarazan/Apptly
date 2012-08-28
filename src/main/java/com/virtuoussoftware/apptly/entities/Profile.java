package com.virtuoussoftware.apptly.entities;

public class Profile {
  
  static final class Type {
    static final String HUMAN = "human";   
  }
  
  public String id;
  public String username;
  public String name;
  public String timezone;
  public String locale;
  public String type;
  public String created_at;
  
  public ImageSpec cover_image;
  public ImageSpec avatar_image;
  public Counts    counts;
  
  public Description description;
  
}
