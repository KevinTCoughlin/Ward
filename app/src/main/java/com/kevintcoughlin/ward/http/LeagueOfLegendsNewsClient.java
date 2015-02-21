package com.kevintcoughlin.ward.http;

import retrofit.RestAdapter;
import retrofit.converter.SimpleXMLConverter;

public class LeagueOfLegendsNewsClient {
    public static LeagueOfLegendsNewsService getClient(String region, String language) {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("http://" + region + ".leagueoflegends.com/" + language + "/rss.xml")
                .setConverter(new SimpleXMLConverter())
                .build();
        return restAdapter.create(LeagueOfLegendsNewsService.class);
    }
}
