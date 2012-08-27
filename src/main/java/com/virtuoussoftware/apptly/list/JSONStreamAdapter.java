package com.virtuoussoftware.apptly.list;

/**
 * JSONStreamAdapter
 * Created by Aaron Sarazan on 8/26/12
 * <p/>
 * Copyright 2012 Virtuous Software. All rights reserved.
 */

// From http://stackoverflow.com/questions/6277154/populate-listview-from-json

import android.app.Activity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;
import com.virtuoussoftware.apptly.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
        TextView title = (TextView)convertView.findViewById(R.id.stream_item_title);
        TextView content = (TextView)convertView.findViewById(R.id.stream_item_content);

        try {
            title.setText(jsonObject.getJSONObject("user").getString("name"));
            content.setText(Html.fromHtml(jsonObject.getString("html")));
        } catch (JSONException e) {
            Log.e("Exception Raised", e.getMessage(), e);
        }

        return convertView;
    }
}