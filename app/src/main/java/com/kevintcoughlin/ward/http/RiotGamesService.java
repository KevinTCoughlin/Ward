package com.kevintcoughlin.ward.http;

import com.kevintcoughlin.ward.models.ChampionData;
import com.kevintcoughlin.ward.models.MatchSummary;
import com.kevintcoughlin.ward.models.Summoner;

import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

public interface RiotGamesService {
    public static final int MATCH_HISTORY_LIMIT = 15;

    @GET("/api/lol/{region}/v1.4/summoner/by-name/{summonerNames}")
    void listSummonersByNames(@Path("region") String region, @Path("summonerNames") String summonerNames, Callback<Map<String, Summoner>> callback);

    @GET("/api/lol/{region}/v1.4/summoner/{summonerIds}")
    void listSummonersByIds(@Path("region") String region, @Path("summonerIds") String summonerIds, Callback<Map<String, Summoner>> callback);

    @GET("/api/lol/{region}/v2.2/matchhistory/{summonerId}")
    void listMatchesById(@Path("region") String region, @Path("summonerId") long summonerId, Callback<Map<String, List<MatchSummary>>> callback);

    @GET("/api/lol/{region}/v2.2/matchhistory/{summonerId}")
    void listMatchesById(@Path("region") String region, @Path("summonerId") long summonerId, @Query("beginIndex") int beginIndex, Callback<Map<String, List<MatchSummary>>> callback);

    @GET("/api/lol/static-data/{region}/v1.2/champion")
    void listChampionsById(@Path("region") String region, @Query("dataById") boolean dataById, Callback<ChampionData> callback);
}