package com.kevintcoughlin.ward;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.facebook.stetho.Stetho;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.kevintcoughlin.ward.http.RiotGamesClient;
import com.kevintcoughlin.ward.models.Champion;
import com.kevintcoughlin.ward.models.ChampionData;
import com.parse.Parse;
import com.parse.ParseUser;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

import java.util.HashMap;
import java.util.Map;

public final class WardApplication extends Application {
	@Override
	public void onCreate() {
		super.onCreate();

		if (BuildConfig.DEBUG) {
			Stetho.initialize(Stetho.newInitializerBuilder(this)
					.enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
					.enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
					.build());
		}

		Parse.enableLocalDatastore(this);
		Parse.initialize(this, Config.PARSE_CLIENT_ID.value(), Config.PARSE_CLIENT_KEY.value());
		ParseUser.enableAutomaticUser();
		ParseUser.getCurrentUser().increment("RunCount");
		ParseUser.getCurrentUser().saveInBackground();

		final String TRACKING_PREF_KEY = getResources().getString(R.string.pref_ga_tracking_key);
		final SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

		mSharedPreferences.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
			@Override
			public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
				if (key.equals(TRACKING_PREF_KEY)) {
					GoogleAnalytics.getInstance(getApplicationContext()).setAppOptOut(sharedPreferences.getBoolean(key, false));
				}
			}
		});

		new ChampionMap();
	}


	public synchronized Tracker getTracker() {
		final GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
		return analytics.newTracker(R.xml.global_tracker);
	}

	public static final class ChampionMap {
		private static final HashMap<String, String> mChampionMap = new HashMap<>();

		public ChampionMap() {
			// @TODO: Don't fetch this data every time
			RiotGamesClient.getClient().listChampionsById("na", false, new Callback<ChampionData>() {
				@Override
				public void success(ChampionData data, Response response) {
					for (final Map.Entry<String, Champion> entry : data.getData().entrySet()) {
						final Champion champion = entry.getValue();
						mChampionMap.put(champion.getId(), champion.getKey());
					}
				}

				@Override
				public void failure(RetrofitError error) {
					Timber.e("GetChampions", error.getMessage());
				}
			});
		}

		public static String getChampionNameById(String id) {
			return mChampionMap.get(id);
		}
	}
}
