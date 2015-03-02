package com.kevintcoughlin.ward.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.kevintcoughlin.ward.R;
import com.kevintcoughlin.ward.WardApplication;
import com.kevintcoughlin.ward.adapters.SummonersAdapter;
import com.kevintcoughlin.ward.database.CupboardSQLiteOpenHelper;
import com.kevintcoughlin.ward.http.RiotGamesClient;
import com.kevintcoughlin.ward.models.Summoner;
import com.melnykov.fab.FloatingActionButton;

import java.util.Arrays;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import nl.qbusict.cupboard.DatabaseCompartment;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

public final class FavoriteSummonersFragment extends Fragment implements RecyclerView.OnItemTouchListener, FloatingActionButton.OnClickListener {
    @InjectView(R.id.list) RecyclerView mRecyclerView;
    @InjectView(R.id.fab) FloatingActionButton mFab;
    public static final String TAG = "Summoners";
    private final String ACTION_ADD = "Add";
    private final String ACTION_REMOVE = "Remove";
    private final String ACTION_SEARCH = "Search";
    private final String ACTION_MATCH_HISTORY = "Match History";
    private CupboardSQLiteOpenHelper db;
    private OnSummonerSelectedListener mListener;
    private SummonersAdapter mAdapter;
    private GestureDetectorCompat mDetector;
    private Context mContext;
    private Tracker mTracker;

    public interface OnSummonerSelectedListener {
        public void onSummonerSelectedListener(Summoner summoner);
    }

    @Override public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnSummonerSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnSummonerSelectedListener");
        }
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final View view = inflater.inflate(R.layout.fragment_favorite_summoners, container, false);
        ButterKnife.inject(this, view);

        getActivity().setTitle(TAG);
        mRecyclerView.setHasFixedSize(true);
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new SummonersAdapter(mContext, getFollowedSummoners());
        mAdapter.setHasStableIds(true);
        mFab.setOnClickListener(this);
        mRecyclerView.setAdapter(mAdapter);
        mDetector = new GestureDetectorCompat(mContext, new RecyclerViewOnGestureListener());
        mRecyclerView.addOnItemTouchListener(this);

        return view;
    }

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getActivity();
        db = new CupboardSQLiteOpenHelper(mContext);
        mTracker = ((WardApplication) getActivity().getApplication()).getTracker();
        mTracker.setScreenName(TAG);
        mTracker.send(new HitBuilders.AppViewBuilder().build());
    }

    @Override public void onClick(View v) {
        promptAddSummoner();
    }

    private Cursor getFollowedSummoners() {
        return cupboard().withDatabase(db.getWritableDatabase()).query(Summoner.class).getCursor();
    }

    private void promptAddSummoner() {
        final LayoutInflater inflater = LayoutInflater.from(getActivity());
        final View v = inflater.inflate(R.layout.dialog_add_summoner, null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        final String[] regionValues = getResources().getStringArray(R.array.region_values);
        final int position = Arrays.asList(regionValues).indexOf(prefs.getString("region", "na"));
        final Spinner spinner = (Spinner) v.findViewById(R.id.regions);
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(mContext, R.array.region_keys, android.R.layout.simple_spinner_dropdown_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(position);
        builder.setTitle(getString(R.string.follow));
        builder.setView(v);
        builder.setPositiveButton(getString(R.string.follow), new DialogInterface.OnClickListener() {
            @Override public void onClick(DialogInterface dialog, int which) {
                final EditText input = (EditText) v.findViewById(R.id.summoner_name);
                final String name = input.getText().toString();
                final int spinnerPosition = spinner.getSelectedItemPosition();
                final String region = regionValues[spinnerPosition];
                addSummoner(name, region);

                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory(TAG)
                        .setAction(ACTION_SEARCH)
                        .setLabel(name)
                        .build());
            }
        });
        builder.setNegativeButton(getString(R.string.nevermind), new DialogInterface.OnClickListener() {
            @Override public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void promptDeleteSummoner(final Summoner summoner) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(getString(R.string.unfollow));
        builder.setMessage(String.format(getString(R.string.confirm_unfollow_summoner), summoner.getName()));
        builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                removeSummoner(summoner.getId());

                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory(TAG)
                        .setAction(ACTION_REMOVE)
                        .setLabel(summoner.getName())
                        .build());
            }
        });
        builder.setNegativeButton(getString(R.string.nevermind), new DialogInterface.OnClickListener() {
            @Override public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void addSummoner(String name, final String region) {
        RiotGamesClient.getClient(region).listSummonersByNames(region, name, new Callback<Map<String, Summoner>>() {
            @Override public void success(Map<String, Summoner> stringSummonerMap, Response response) {
                final DatabaseCompartment dbc = cupboard().withDatabase(db.getWritableDatabase());
                for (final Map.Entry<String, Summoner> pair : stringSummonerMap.entrySet()) {
                    final Summoner summoner = pair.getValue();
                    summoner.setRegion(region);
                    dbc.put(summoner);
                    mTracker.send(new HitBuilders.EventBuilder()
                            .setCategory(TAG)
                            .setAction(ACTION_ADD)
                            .setLabel(summoner.getName())
                            .build());
                }

                mAdapter.swapCursor(dbc.query(Summoner.class).getCursor());
            }

            @Override public void failure(RetrofitError error) {
                Toast.makeText(mContext, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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
            if (position > -1) {
                final Summoner summoner = cupboard().withDatabase(db.getReadableDatabase()).get(Summoner.class, mAdapter.getItemId(position));
                mListener.onSummonerSelectedListener(summoner);
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory(TAG)
                        .setAction(ACTION_MATCH_HISTORY)
                        .setLabel(summoner.getName())
                        .build());
            }
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
