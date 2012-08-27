package com.virtuoussoftware.apptly.jankotron;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.util.Log;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.FragmentManager;
import android.app.ListFragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.virtuoussoftware.apptly.net.*;
import com.virtuoussoftware.apptly.R;
import com.virtuoussoftware.apptly.list.JSONStreamAdapter;

public class SlowTabulizerActivity extends Activity {
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
        try {
            setContentView(R.layout.janky_list);
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        fetchStuff();
	}

    private String mockFetchGlobalStreamAsString() {
        return "[" +
                "  { 'id' : '2', 'user' : { 'id' : '2', 'name' : 'Dave Fayram' }, 'html' : '<html><b>@asarazan</b> There\\'s twoots EVERYWHERE</html>' }," +
                "  { 'id' : '1', 'user' : { 'id' : '1', 'name' : 'Aaron Sarazan' }, 'html' : '<html>Hey <b>@KirinDave</b>, how\\'re the twoots?</html>' }" +
                "]";
    }

	private String fetchGlobalStreamAsString() {		
		StringBuilder builder = new StringBuilder();
		try {
			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet();
			request.setURI(new URI(Path.API_ENDPOINT + Path.API_GLOBAL_STREAM));
			HttpResponse response = client.execute(request);

			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			if (statusCode == 200) {
				HttpEntity entity = response.getEntity();
				InputStream content = entity.getContent();
				BufferedReader reader = new BufferedReader(new InputStreamReader(content));
				String line;
				while ((line = reader.readLine()) != null) {
					builder.append(line);
				}
				return builder.toString();
			}				
		} catch (Exception e) {
			// TODO (not really)
		}			
		return null;
	}
	
	private String stringForObject(JSONObject obj) {
		
		try {
			StringBuilder builder = new StringBuilder();
			
			String postId = obj.getString("id");
			JSONObject user = obj.getJSONObject("user");
			String username = user.getString("username");
			//String userid = user.getString("id");
			String content = obj.getString("text");
			//Spanned content = Html.fromHtml(obj.getString("html"));
			
			builder
			.append("[")
			.append(postId)
			.append("] ")
			.append(username)
			.append("\n\t")
			.append(content);
			
			return builder.toString();
						
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

	private void fetchStuff() {
		//String globalStream = fetchGlobalStreamAsString();
        String globalStream = mockFetchGlobalStreamAsString();
        JSONStreamAdapter adapter = new JSONStreamAdapter(this, globalStream);
        ListFragment list = (ListFragment)getFragmentManager().findFragmentById(R.id.janky_list_fragment);
        list.setListAdapter(adapter);
	}
}
