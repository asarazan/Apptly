package com.virtuoussoftware.apptly.list;

/**
 * JSONStreamAdapter
 * Created by Aaron Sarazan on 8/26/12
 * <p/>
 * Copyright 2012 Virtuous Software. All rights reserved.
 */

// From http://stackoverflow.com/questions/6277154/populate-listview-from-json

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import com.virtuoussoftware.apptly.R;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

public class JSONStreamAdapter extends BaseAdapter implements ListAdapter {

    private final Activity activity;
    private final JSONArray jsonArray;

    public JSONStreamAdapter(Activity activity, JSONArray jsonArray) {
        this.activity = activity;
        this.jsonArray = jsonArray;
    }

    public JSONStreamAdapter(Activity activity, String jsonString) {
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(jsonString);
        } catch (JSONException e) {
            Log.e("Exception Raised", e.getMessage(), e);
        }

        this.activity = activity;
        this.jsonArray = jsonArray;
    }

    @Override public int getCount() {

        return jsonArray.length();
    }

    @Override public JSONObject getItem(int position) {

        return jsonArray.optJSONObject(position);
    }

    @Override public long getItemId(int position) {
        JSONObject jsonObject = getItem(position);

        return jsonObject.optLong("id");
    }

    @Override public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = activity.getLayoutInflater().inflate(R.layout.stream_item, null);

        JSONObject jsonObject = getItem(position);

        final TextView title = (TextView)convertView.findViewById(R.id.stream_item_title);
        final TextView content = (TextView)convertView.findViewById(R.id.stream_item_content);
        final ImageView portrait = (ImageView)convertView.findViewById(R.id.stream_item_portrait);

        try {
            JSONObject user = jsonObject.getJSONObject("user");

            title.setText(user.getString("name"));
            content.setText(Html.fromHtml(jsonObject.getString("html")));

            new AsyncTask<String, String, Bitmap>() {

                @Override
                protected Bitmap doInBackground(String... strings) {
                    Bitmap retval = null;
                    try {
                        HttpClient httpClient = new DefaultHttpClient();
                        HttpGet get = new HttpGet();
                        get.setURI(new URI(strings[0]));
                        HttpResponse response = httpClient.execute(get);
                        HttpEntity entity = response.getEntity();
                        InputStream stream = entity.getContent();
                        retval = BitmapFactory.decodeStream(stream);
                    } catch (Exception e) {
                        Log.e("Exception Raised", e.getMessage(), e);
                    }
                    return retval;
                }

                @Override
                protected void onPostExecute(Bitmap bitmap) {
                    if (bitmap != null) {
                        portrait.setImageBitmap(bitmap);
                    }
                }
            }.execute(user.getJSONObject("avatar_image").getString("url"));

        } catch (Exception e) {
            Log.e("Exception Raised", e.getMessage(), e);
        }

        return convertView;
    }
}