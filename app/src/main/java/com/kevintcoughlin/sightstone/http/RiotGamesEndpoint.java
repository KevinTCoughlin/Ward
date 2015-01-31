package com.kevintcoughlin.sightstone.http;

import retrofit.Endpoint;

public final class RiotGamesEndpoint implements Endpoint {
    private static final String PROTOCOL = "https://";
    private static final String BASE = ".api.pvp.net";
    private String url;
    private String mRegion;

    public void setRegion(String region) {
        mRegion = region;
        url = PROTOCOL + mRegion + BASE;
    }

    @Override public String getName() {
        return mRegion;
    }

    @Override public String getUrl() {
        if (url == null) throw new IllegalStateException("Illegal URL.");
        return url;
    }
}