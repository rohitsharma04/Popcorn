package com.bitshifters.rohit.popcorn.data;

/**
 * Created by rohit on 3/4/16.
 */
public interface MovieTableMeta {

    public static final String ID = "_id";

    public static final String POSTER_PATH = "poster_path";

    public static final String OVERVIEW = "overview";

    public static final String RELEASE_DATE = "release_date";

    public static final String TITLE = "title";

    public static final String BACKDROP = "backdrop";

    public static final String POPULARITY = "popularity";

    public static final String VOTE_COUNT = "vote_count";

    public static final String VOTE_AVERAGE = "vote_average";

    public static final String [] COLUMNS = {"_id","poster_path","overview",
            "release_date","title","backdrop","popularity","vote_count","vote_average"};

    public static final int ID_ID = 0, POSTER_PATH_ID = 1, OVERVIEW_ID = 2, RELEASE_DATE_ID = 3
            ,TITLE_ID = 4, BACKDROP_ID = 5, POPULARITY_ID = 6, VOTE_COUNT_ID = 7, VOTE_AVERAGE_ID = 8;


}
