package com.kevintcoughlin.ward;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.facebook.stetho.Stetho;
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

    @Override public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Stetho.initialize(Stetho.newInitializerBuilder(this)
                .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                .build());
        }

        final String TRACKING_PREF_KEY = getResources().getString(R.string.pref_ga_tracking_key);
        final SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        mSharedPreferences.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener () {
            @Override public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if (key.equals(TRACKING_PREF_KEY)) {
                    GoogleAnalytics.getInstance(getApplicationContext()).setAppOptOut(sharedPreferences.getBoolean(key, false));
                }
            }
        });

        // @TODO: Don't fetch this data every timeca
        RiotGamesClient.getClient().listChampionsById("na", true, new Callback<ChampionData>() {
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
        return analytics.newTracker(R.xml.global_tracker);
    }
}
