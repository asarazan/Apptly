package com.virtuoussoftware.apptly;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

public class StreamListActivity extends FragmentActivity
        implements StreamListFragment.Callbacks {

    private boolean mTwoPane;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stream_list);

        if (findViewById(R.id.stream_detail_container) != null) {
            mTwoPane = true;
            ((StreamListFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.stream_list))
                    .setActivateOnItemClick(true);
        }
    }

    @Override
    public void onItemSelected(String id) {
        if (mTwoPane) {
            Bundle arguments = new Bundle();
            arguments.putString(StreamDetailFragment.ARG_ITEM_ID, id);
            StreamDetailFragment fragment = new StreamDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.stream_detail_container, fragment)
                    .commit();

        } else {
            Intent detailIntent = new Intent(this, StreamDetailActivity.class);
            detailIntent.putExtra(StreamDetailFragment.ARG_ITEM_ID, id);
            startActivity(detailIntent);
        }
    }
}
