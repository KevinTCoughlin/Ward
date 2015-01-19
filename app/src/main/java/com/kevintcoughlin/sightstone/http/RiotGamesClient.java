package com.kevintcoughlin.sightstone.http;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;

public class RiotGamesClient {
    private static RiotGamesService sRiotGamesService;
    private static final String baseUrl = "https://na.api.pvp.net";
    private static final String apiKey = "70328b7e-c373-44b9-9336-6832cda44559";

    private static RequestInterceptor requestInterceptor = new RequestInterceptor() {
        @Override
        public void intercept(RequestFacade request) {
            request.addQueryParam("api_key", apiKey);
        }
    };

    public static RiotGamesService getClient() {
        if (sRiotGamesService == null) {
            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint(baseUrl)
                    .setRequestInterceptor(requestInterceptor)
                    .build();

            sRiotGamesService = restAdapter.create(RiotGamesService.class);
        }

        return sRiotGamesService;
    }
}
