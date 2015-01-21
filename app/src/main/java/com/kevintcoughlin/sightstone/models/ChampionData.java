package com.kevintcoughlin.sightstone.models;

import java.util.HashMap;

public class ChampionData {
    private HashMap<String, Champion> data;

    public HashMap<String, Champion> getData() {
        return data;
    }

    public void setData(HashMap<String, Champion> data) {
        this.data = data;
    }
}
