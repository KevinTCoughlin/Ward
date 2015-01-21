package com.kevintcoughlin.sightstone;

import android.app.Application;

import com.kevintcoughlin.sightstone.database.CupboardSQLiteOpenHelper;
import com.kevintcoughlin.sightstone.http.RiotGamesClient;
import com.kevintcoughlin.sightstone.models.Champion;
import com.kevintcoughlin.sightstone.models.ChampionData;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import nl.qbusict.cupboard.DatabaseCompartment;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

public class SightstoneApplication extends Application {
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
}
