package com.bitshifters.rohit.popcorn.data;

/**
 * Created by rohit on 3/4/16.
 */
public interface MovieTableMeta {

    String ID = "_id";

    String POSTER_PATH = "poster_path";

    String OVERVIEW = "overview";

    String RELEASE_DATE = "release_date";

    String TITLE = "title";

    String BACKDROP = "backdrop";

    String POPULARITY = "popularity";

    String VOTE_COUNT = "vote_count";

    String VOTE_AVERAGE = "vote_average";

    String [] COLUMNS = {"_id","poster_path","overview",
            "release_date","title","backdrop","popularity","vote_count","vote_average"};

    int ID_ID = 0, POSTER_PATH_ID = 1, OVERVIEW_ID = 2, RELEASE_DATE_ID = 3
            ,TITLE_ID = 4, BACKDROP_ID = 5, POPULARITY_ID = 6, VOTE_COUNT_ID = 7, VOTE_AVERAGE_ID = 8;


}
