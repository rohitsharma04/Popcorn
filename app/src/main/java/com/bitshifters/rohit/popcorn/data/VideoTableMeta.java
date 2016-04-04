package com.bitshifters.rohit.popcorn.data;

import de.triplet.simpleprovider.Column;

/**
 * Created by rohit on 4/4/16.
 */
public interface VideoTableMeta {
    String MOVIE_ID = "movie_id";
    String ID = "_id";
    String ISO6391 =  "iso6391";
    String ISO31661 = "iso31661";
    String KEY = "key";
    String NAME = "name";
    String SITE = "site";
    String SIZE = "size";
    String TYPE = "type";

    String [] COLUMNS = { "_id", "iso6391", "iso31661", "key", "name", "site", "size", "type", "movie_id"};

    int ID_ID = 0, ISO6391_ID = 1, ISO31661_ID = 2, KEY_ID = 3, NAME_ID = 4, SITE_ID = 5, SIZE_ID = 6, TYPE_ID = 7,MOVIE_ID_ID=8;
}
