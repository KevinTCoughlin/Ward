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
import com.mopub.common.MoPub;

import java.util.Map;

import io.fabric.sdk.android.Fabric;
import nl.qbusict.cupboard.DatabaseCompartment;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

public final class WardApplication extends Application {
    private final CupboardSQLiteOpenHelper db = new CupboardSQLiteOpenHelper(this);

    @Override public void onCreate() {
        super.onCreate();
        Fabric.with(this, new MoPub());

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

        // @TODO: Don't fetch this data every time
        RiotGamesClient.getClient().listChampionsById("na", false, new Callback<ChampionData>() {
            @Override
            public void success(ChampionData data, Response response) {
                final DatabaseCompartment dbc = cupboard().withDatabase(db.getWritableDatabase());
                for (final Map.Entry<String, Champion> entry : data.getData().entrySet()) {
                    dbc.put(entry.getValue());
                }
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    public synchronized Tracker getTracker() {
        final GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
        return analytics.newTracker(R.xml.global_tracker);
    }
}
