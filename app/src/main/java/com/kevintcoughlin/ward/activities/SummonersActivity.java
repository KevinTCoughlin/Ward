package com.kevintcoughlin.ward.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.crashlytics.android.Crashlytics;
import com.kevintcoughlin.ward.R;
import com.kevintcoughlin.ward.adapters.DrawerNavigationAdapter;
import com.kevintcoughlin.ward.fragments.*;
import com.kevintcoughlin.ward.models.DataDragonChampion;
import com.kevintcoughlin.ward.models.Summoner;
import io.fabric.sdk.android.Fabric;
import org.parceler.Parcels;

public final class SummonersActivity extends AppCompatActivity implements FavoriteSummonersFragment
        .OnSummonerSelectedListener, ChampionsFragment.OnChampionSelectedListener {
    @InjectView(R.id.toolbar_actionbar) Toolbar mToolbar;
    @InjectView(R.id.drawer_layout) DrawerLayout mDrawerLayout;
    @InjectView(R.id.left_drawer) ListView mDrawerList;
    private String[] mNavigationMenuItems;
    private ActionBarDrawerToggle mDrawerToggle;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_summoners);
        ButterKnife.inject(this);
        setSupportActionBar(mToolbar);

        mNavigationMenuItems = getResources().getStringArray(R.array.navigation_menu_items);
        mDrawerList.setDividerHeight(0);
        mDrawerList.setAdapter(new DrawerNavigationAdapter(this, mNavigationMenuItems));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.drawer_open, R.string.drawer_close);
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (findViewById(R.id.fragment_container) != null) {
            if (savedInstanceState != null) {
                return;
            }

            final FavoriteSummonersFragment fragment = new FavoriteSummonersFragment();
            fragment.setArguments(getIntent().getExtras());
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment, FavoriteSummonersFragment.TAG)
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
                replaceFragment(new PrefsFragment());
                break;
            case "Summoners":
                replaceFragment(new FavoriteSummonersFragment());
                break;
            case "News":
                replaceFragment(new NewsFragment());
                break;
            case "Champions":
                replaceFragment(new ChampionsFragment());
                break;
            default:
                break;
        }
        mDrawerLayout.closeDrawer(Gravity.START);
    }

    private void replaceFragment(Fragment fragment){
        final String tag = fragment.getClass().getName();
        final FragmentManager manager = getSupportFragmentManager();
        final boolean popped = manager.popBackStackImmediate(tag, 0);

        if (!popped) {
            final FragmentTransaction ft = manager.beginTransaction();
            ft.replace(R.id.fragment_container, fragment);
            ft.addToBackStack(tag);
            ft.commit();
        }
    }

    @Override public void onSummonerSelectedListener(Summoner summoner) {
        final Bundle bundle = new Bundle();
        final Fragment fragment = new MatchHistoryFragment();
        bundle.putParcelable(Summoner.TAG, Parcels.wrap(summoner));
        fragment.setArguments(bundle);
        replaceFragment(fragment);
    }

    @Override public void onChampionSelectedListener(DataDragonChampion champion) {
        final Bundle bundle = new Bundle();
        final Fragment fragment = new ChampionFragment();
        bundle.putParcelable(DataDragonChampion.TAG, Parcels.wrap(champion));
        fragment.setArguments(bundle);
        replaceFragment(fragment);
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            selectItem(position);
        }
    }
}
