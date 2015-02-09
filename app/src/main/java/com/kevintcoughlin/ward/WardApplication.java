package com.kevintcoughlin.ward;

import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.kevintcoughlin.ward.database.CupboardSQLiteOpenHelper;
import com.kevintcoughlin.ward.http.RiotGamesClient;
import com.kevintcoughlin.ward.models.Champion;
import com.kevintcoughlin.ward.models.ChampionData;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import nl.qbusict.cupboard.DatabaseCompartment;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

public class WardApplication extends Application {
    private final CupboardSQLiteOpenHelper db = new CupboardSQLiteOpenHelper(this);
    private final String region = "na";

    @Override public void onCreate() {
        super.onCreate();

        // @TODO: Don't fetch this data every time
        RiotGamesClient.getClient().listChampionsById(region, true, new Callback<ChampionData>() {
            @Override public void success(ChampionData championData, Response response) {
                final HashMap<String, Champion> champions = championData.getData();
                final DatabaseCompartment dbc = cupboard().withDatabase(db.getWritableDatabase());
                final Iterator it = champions.entrySet().iterator();
                while (it.hasNext()) {
                    final Map.Entry pairs = (Map.Entry) it.next();
                    final Champion champion = (Champion) pairs.getValue();
                    dbc.put(champion);
                    it.remove();
                }
            }

            @Override public void failure(RetrofitError error) {

            }
        });
    }

    public synchronized Tracker getTracker() {
        final GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
        final Tracker tracker = analytics.newTracker(R.xml.global_tracker);
        return tracker;
    }
}