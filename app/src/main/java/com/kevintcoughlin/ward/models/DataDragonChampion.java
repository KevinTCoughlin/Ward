package com.kevintcoughlin.ward.models;

import java.util.HashMap;

public class DataDragonChampion extends BaseChampion {
    public String id;
    public HashMap<String, Image> image;

    public class Image {
        public HashMap<String, String> full;
    }
}
