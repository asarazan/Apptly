package com.virtuoussoftware.apptly.entities;

import java.util.Collection;

public class Description {
  
  public static class MentionItem {
    public String name;
    public String id;
    public int    pos;
    public int    len;
  }
  
  public static class HashtagItem {
    public String name;
    public int pos;
    public int len;
  }
  
  public static class LinkItem {
    public String text;
    public String url;
    public int pos;
    public int len;
  }
  
  public String text;
  public String html;
  public Collection<MentionItem> mentions;
  public Collection<HashtagItem> hashtags;
  public Collection<LinkItem>    links;
}
