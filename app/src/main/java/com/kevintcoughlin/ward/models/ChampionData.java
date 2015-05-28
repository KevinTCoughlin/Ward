package com.kevintcoughlin.ward.models;

import java.util.HashMap;

public final class ChampionData {
	private HashMap<String, Champion> data;

	public HashMap<String, Champion> getData() {
		return data;
	}

	public void setData(HashMap<String, Champion> data) {
		this.data = data;
	}
}
