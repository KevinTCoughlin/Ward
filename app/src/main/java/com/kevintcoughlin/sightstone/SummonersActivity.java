package com.kevintcoughlin.sightstone;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

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

public final class SummonersActivity extends ActionBarActivity implements Callback<Map<String, Summoner>>, FloatingActionButton.OnClickListener {
    @InjectView(R.id.toolbar_actionbar) Toolbar mToolbar;
    @InjectView(R.id.list) RecyclerView mRecyclerView;
    @InjectView(R.id.fab) FloatingActionButton mFab;

    private final String region = "na"; // @TODO: make configurable
    private final CupboardSQLiteOpenHelper db = new CupboardSQLiteOpenHelper(this);
    private LinearLayoutManager mLayoutManager;
    private SummonersAdapter mAdapter;
    private Context mContext;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
            @Override public void onItemClick(View view, int position) {
                Intent intent = new Intent(mContext, MatchHistoryActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("summoner", Parcels.wrap(cupboard().withDatabase(db.getReadableDatabase()).get(Summoner.class, mAdapter.getItemId(position))));
                intent.putExtras(bundle);
                startActivity(intent);
            }
        }));
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
        promptForSummoner();
    }

    private Cursor getFollowedSummoners() {
        return cupboard().withDatabase(db.getWritableDatabase()).query(Summoner.class).getCursor();
    }

    private void promptForSummoner() {
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Follow Summoner");
        builder.setView(input);
        builder.setPositiveButton("Follow", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = input.getText().toString();
                addSummoner(name);
            }
        });
        builder.setNegativeButton("Nevermind", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void addSummoner(String name) {
        RiotGamesClient.getClient().listSummonersByNames(region, name, this);
    }
}
