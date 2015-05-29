package com.kevintcoughlin.ward.models;

public final class DataDragonChampion extends Champion {
	public static final String TAG = DataDragonChampion.class.getSimpleName();
	public Image image;

	public final class Image {
		public String full;
		public String sprite;
		public String group;
		public int x;
		public int y;
		public int w;
		public int h;
	}
}

