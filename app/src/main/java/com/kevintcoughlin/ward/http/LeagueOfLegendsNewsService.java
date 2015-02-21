package com.kevintcoughlin.ward.http;

import com.kevintcoughlin.ward.models.news.Rss;

import retrofit.Callback;
import retrofit.http.GET;

public interface LeagueOfLegendsNewsService {
    @GET("/")
    void getFeed(Callback<Rss> callback);
}
