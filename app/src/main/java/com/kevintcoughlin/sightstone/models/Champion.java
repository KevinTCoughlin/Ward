package com.kevintcoughlin.sightstone.models;

import com.google.gson.annotations.SerializedName;

public final class Champion {
    @SerializedName("id")
    private long _id;
    private String title;
    private String name;
    private String key;

    public Champion() {
    }

    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
