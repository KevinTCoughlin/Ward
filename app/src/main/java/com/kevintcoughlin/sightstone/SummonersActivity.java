package com.kevintcoughlin.sightstone;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.kevintcoughlin.sightstone.adapters.SummonersAdapter;
import com.kevintcoughlin.sightstone.database.CupboardSQLiteOpenHelper;
import com.kevintcoughlin.sightstone.http.RiotGamesClient;
import com.kevintcoughlin.sightstone.models.Summoner;
import com.melnykov.fab.FloatingActionButton;

import org.parceler.Parcels;

import java.util.Iterator;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import nl.qbusict.cupboard.DatabaseCompartment;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

public final class SummonersActivity extends ActionBarActivity implements RecyclerView.OnItemTouchListener, Callback<Map<String, Summoner>>, FloatingActionButton.OnClickListener {
    @InjectView(R.id.toolbar_actionbar) Toolbar mToolbar;
    @InjectView(R.id.list) RecyclerView mRecyclerView;
    @InjectView(R.id.fab) FloatingActionButton mFab;

    private final String region = "na"; // @TODO: make configurable
    private final CupboardSQLiteOpenHelper db = new CupboardSQLiteOpenHelper(this);
    private LinearLayoutManager mLayoutManager;
    private SummonersAdapter mAdapter;
    private GestureDetectorCompat mDetector;
    private Context mContext;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!BuildConfig.DEBUG) {
            Crashlytics.start(this);
        }

        setContentView(R.layout.activity_summoners);
        ButterKnife.inject(this);
        setSupportActionBar(mToolbar);

        mContext = this;
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new SummonersAdapter(this, getFollowedSummoners());
        mAdapter.setHasStableIds(true);
        mFab.attachToRecyclerView(mRecyclerView);
        mFab.setOnClickListener(this);
        mRecyclerView.setAdapter(mAdapter);
        mDetector = new GestureDetectorCompat(this, new RecyclerViewOnGestureListener());
        mRecyclerView.addOnItemTouchListener(this);
    }

    @Override public void success(Map<String, Summoner> stringSummonerMap, Response response) {
        final DatabaseCompartment dbc = cupboard().withDatabase(db.getWritableDatabase());
        final Iterator it = stringSummonerMap.entrySet().iterator();

        while (it.hasNext()) {
            final Map.Entry pairs = (Map.Entry) it.next();
            final Summoner summoner = (Summoner) pairs.getValue();
            dbc.put(summoner);
            it.remove();
        }

        mAdapter.swapCursor(dbc.query(Summoner.class).getCursor());
    }

    @Override public void failure(RetrofitError error) {
        Toast.makeText(this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override public void onClick(View v) {
        promptAddSummoner();
    }

    private Cursor getFollowedSummoners() {
        return cupboard().withDatabase(db.getWritableDatabase()).query(Summoner.class).getCursor();
    }

    private void promptAddSummoner() {
        final LayoutInflater inflater = getLayoutInflater();
        final View v = inflater.inflate(R.layout.add_summoner_dialog, null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Follow");
        builder.setView(v);
        builder.setPositiveButton("Follow", new DialogInterface.OnClickListener() {
            @Override public void onClick(DialogInterface dialog, int which) {
                final EditText input = (EditText) v.findViewById(R.id.summoner_name);
                final String name = input.getText().toString();
                addSummoner(name);
            }
        });
        builder.setNegativeButton("Nevermind", new DialogInterface.OnClickListener() {
            @Override public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void promptDeleteSummoner(final Summoner summoner) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Unfollow");
        builder.setMessage("Are you sure that you want to unfollow " + summoner.getName());
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override public void onClick(DialogInterface dialog, int which) {
                removeSummoner(summoner.getId());
            }
        });
        builder.setNegativeButton("Nevermind", new DialogInterface.OnClickListener() {
            @Override public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void addSummoner(String name) {
        RiotGamesClient.getClient().listSummonersByNames(region, name, this);
    }

    private void removeSummoner(long id) {
        final DatabaseCompartment dbc = cupboard().withDatabase(db.getWritableDatabase());
        dbc.delete(Summoner.class, id);
        mAdapter.swapCursor(dbc.query(Summoner.class).getCursor());
    }

    @Override public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        mDetector.onTouchEvent(e);
        return false;
    }

    @Override public void onTouchEvent(RecyclerView rv, MotionEvent e) {
    }

    private class RecyclerViewOnGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override public boolean onSingleTapConfirmed(MotionEvent e) {
            final View view = mRecyclerView.findChildViewUnder(e.getX(), e.getY());
            final int position = mRecyclerView.getChildPosition(view);
            final Intent intent = new Intent(mContext, MatchHistoryActivity.class);
            final Bundle bundle = new Bundle();
            bundle.putParcelable("summoner", Parcels.wrap(cupboard().withDatabase(db.getReadableDatabase()).get(Summoner.class, mAdapter.getItemId(position))));
            intent.putExtras(bundle);
            startActivity(intent);
            return super.onSingleTapConfirmed(e);
        }

        public void onLongPress(MotionEvent e) {
            final View view = mRecyclerView.findChildViewUnder(e.getX(), e.getY());
            final int position = mRecyclerView.getChildPosition(view);
            final Summoner summoner = cupboard().withDatabase(db.getReadableDatabase()).get(Summoner.class, mAdapter.getItemId(position));
            promptDeleteSummoner(summoner);
            super.onLongPress(e);
        }
    }
}
