package com.kevintcoughlin.ward.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.crashlytics.android.Crashlytics;
import com.kevintcoughlin.ward.BuildConfig;
import com.kevintcoughlin.ward.R;
import com.kevintcoughlin.ward.fragments.FavoriteSummonersFragment;
import com.kevintcoughlin.ward.fragments.MatchHistoryFragment;
import com.kevintcoughlin.ward.models.Summoner;

import org.parceler.Parcels;

import butterknife.ButterKnife;
import butterknife.InjectView;

public final class SummonersActivity extends ActionBarActivity implements FavoriteSummonersFragment.OnSummonerSelectedListener {
    @InjectView(R.id.toolbar_actionbar) Toolbar mToolbar;
    @InjectView(R.id.drawer_layout) DrawerLayout mDrawerLayout;
    @InjectView(R.id.left_drawer) ListView mDrawerList;
    private String[] mNavigationMenuItems;
    private ActionBarDrawerToggle mDrawerToggle;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summoners);
        ButterKnife.inject(this);

        if (!BuildConfig.DEBUG) {
            Crashlytics.start(this);
        }

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mNavigationMenuItems = getResources().getStringArray(R.array.navigation_menu_items);
        mDrawerList.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mNavigationMenuItems));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                mToolbar,
                R.string.drawer_open,
                R.string.drawer_close
        );
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (findViewById(R.id.fragment_container) != null) {
            if (savedInstanceState != null) {
                return;
            }

            FavoriteSummonersFragment fragment = new FavoriteSummonersFragment();
            fragment.setArguments(getIntent().getExtras());
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }
    }

    @Override protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    private void selectItem(int position) {
        final String action = mNavigationMenuItems[position];
        // @TODO: Convert to string id ints
        switch (action) {
            case "Settings":
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case "Summoners":
                Intent intent = new Intent(this, SummonersActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                break;
            case "News":
                startActivity(new Intent(this, NewsActivity.class));
                break;
            default:
                break;
        }
    }

    @Override public void onSummonerSelectedListener(Summoner summoner) {
        final Bundle bundle = new Bundle();
        Fragment matchHistoryFragment = new MatchHistoryFragment();
        bundle.putParcelable(Summoner.TAG, Parcels.wrap(summoner));
        matchHistoryFragment.setArguments(bundle);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, matchHistoryFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            selectItem(position);
        }
    }
}
