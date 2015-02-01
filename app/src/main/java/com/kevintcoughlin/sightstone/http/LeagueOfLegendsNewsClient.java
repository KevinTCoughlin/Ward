package com.kevintcoughlin.sightstone.http;

import retrofit.RestAdapter;
import retrofit.converter.SimpleXMLConverter;

public class LeagueOfLegendsNewsClient {
    public static LeagueOfLegendsNewsService getClient() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("http://na.leagueoflegends.com/en/rss.xml")
                .setConverter(new SimpleXMLConverter())
                .build();
        return restAdapter.create(LeagueOfLegendsNewsService.class);
    }
}
