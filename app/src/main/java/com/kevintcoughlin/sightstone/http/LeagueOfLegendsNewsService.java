package com.kevintcoughlin.sightstone.http;

import com.kevintcoughlin.sightstone.models.news.Rss;

import retrofit.Callback;
import retrofit.http.GET;

public interface LeagueOfLegendsNewsService {
    @GET("/")
    void getFeed(Callback<Rss> callback);
}
