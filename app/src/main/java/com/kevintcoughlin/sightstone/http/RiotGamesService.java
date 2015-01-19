package com.kevintcoughlin.sightstone.http;

import com.kevintcoughlin.sightstone.models.MatchSummary;
import com.kevintcoughlin.sightstone.models.Summoner;

import java.util.List;
import java.util.Map;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;

public interface RiotGamesService {
    @GET("/api/lol/{region}/v1.4/summoner/by-name/{summonerNames}")
    void listSummonersByNames(@Path("region") String region, @Path("summonerNames") String summonerNames, Callback<Map<String, Summoner>> callback);

    @GET("/api/lol/{region}/v1.4/summoner/{summonerIds}")
    void listSummonersByIds(@Path("region") String region, @Path("summonerIds") String summonerIds, Callback<Map<String, Summoner>> callback);

    @GET("/api/lol/{region}/v2.2/matchhistory/{summonerId}")
    void listMatchesById(@Path("region") String region, @Path("summonerId") long summonerId, Callback<Map<String, List<MatchSummary>>> callback);
}
