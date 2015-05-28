package com.kevintcoughlin.ward.models;

import android.database.Cursor;
import com.google.gson.annotations.SerializedName;
import org.parceler.Parcel;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

@Parcel
public final class Summoner {
	public final static String TAG = "summoner";

	@SerializedName("id")
	private long _id;
	private String name;
	private int profileIconId;
	private long revisionDate;
	private long summonerLevel;
	private String region;

	public Summoner() {

	}

	public static Summoner fromCursor(Cursor cursor) {
		return cupboard().withCursor(cursor).get(Summoner.class);
	}

	public long getId() {
		return _id;
	}

	public void setId(long _id) {
		this._id = _id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getProfileIconId() {
		return profileIconId;
	}

	public void setProfileIconId(int profileIconId) {
		this.profileIconId = profileIconId;
	}

	public long getRevisionDate() {
		return revisionDate;
	}

	public void setRevisionDate(long revisionDate) {
		this.revisionDate = revisionDate;
	}

	public long getSummonerLevel() {
		return summonerLevel;
	}

	public void setSummonerLevel(long summonerLevel) {
		this.summonerLevel = summonerLevel;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}
}
