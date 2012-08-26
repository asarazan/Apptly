package com.virtuoussoftware.apptly.jankotron;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.virtuoussoftware.apptly.net.*;

public class SlowTabulizerActivity extends FragmentActivity {
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);

		// TODO (asarazan) inflate pretty layout
	}

	private String fetchGlobalStreamAsString() {		
		StringBuilder builder = new StringBuilder();
		BufferedReader reader = null;
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
				reader = new BufferedReader(new InputStreamReader(content));
				String line = null;
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

	private void fetchStuff() {
		String globalStream = fetchGlobalStreamAsString();
		try {
			JSONArray json = new JSONArray(globalStream);
		} catch (Exception e) {
			
		}
	}
}
