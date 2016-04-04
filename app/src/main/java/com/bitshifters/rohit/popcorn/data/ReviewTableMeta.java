package com.bitshifters.rohit.popcorn.data;

import de.triplet.simpleprovider.Column;

/**
 * Created by rohit on 4/4/16.
 */
public interface ReviewTableMeta {

    String MOVIE_ID = "movie_id";
    String ID = "_id";
    String AUTHOR = "_author";
    String CONTENT = "content";
    String URL = "url";

    String[] COLUMNS = {"movie_id","_id","_author","content","url"};

    int MOVIE_ID_ID = 0, ID_ID = 1, AUTHOR_ID = 2, CONTENT_ID = 3, URL_ID = 4;
}
