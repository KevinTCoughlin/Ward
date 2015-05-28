package com.kevintcoughlin.ward.http;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;

public final class RiotGamesClient {
	private static final String apiKey = "70328b7e-c373-44b9-9336-6832cda44559";
	private static RiotGamesEndpoint mEndpoint = new RiotGamesEndpoint();

	private static RequestInterceptor requestInterceptor = new RequestInterceptor() {
		@Override
		public void intercept(RequestFacade request) {
			request.addQueryParam("api_key", apiKey);
		}
	};

	public static RiotGamesService getClient() {
		mEndpoint.setRegion("na");
		RestAdapter restAdapter = new RestAdapter.Builder()
				.setEndpoint(mEndpoint)
				.setRequestInterceptor(requestInterceptor)
				.build();

		return restAdapter.create(RiotGamesService.class);
	}

	public static RiotGamesService getClient(String region) {
		mEndpoint.setRegion(region);
		RestAdapter restAdapter = new RestAdapter.Builder()
				.setEndpoint(mEndpoint)
				.setRequestInterceptor(requestInterceptor)
				.build();

		return restAdapter.create(RiotGamesService.class);
	}
}
