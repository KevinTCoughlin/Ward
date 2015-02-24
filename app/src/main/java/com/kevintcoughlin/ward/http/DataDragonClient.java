package com.kevintcoughlin.ward.http;

import retrofit.RestAdapter;

public final class DataDragonClient {
    public static DataDragonService getClient() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("http://ddragon.leagueoflegends.com/cdn/5.2.1/data/en_US")
                .build();
        return restAdapter.create(DataDragonService.class);
    }
}
