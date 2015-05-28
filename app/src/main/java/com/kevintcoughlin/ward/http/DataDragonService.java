package com.kevintcoughlin.ward.http;

import com.kevintcoughlin.ward.models.DataDragonChampionsData;
import retrofit.Callback;
import retrofit.http.GET;

public interface DataDragonService {
	@GET("/champion.json")
	void getChampions(Callback<DataDragonChampionsData> callback);
}

