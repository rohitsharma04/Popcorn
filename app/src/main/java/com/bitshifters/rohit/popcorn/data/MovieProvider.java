package com.bitshifters.rohit.popcorn.data;

import android.net.Uri;

import de.triplet.simpleprovider.AbstractProvider;
import de.triplet.simpleprovider.Column;
import de.triplet.simpleprovider.Table;

/**
 * Created by rohit on 3/4/16.
 */
public class MovieProvider extends AbstractProvider {

    public static final String AUTHORITY = "com.bitshifters.rohit.popcorn.data.MovieProvider";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    private static final int SCHEMA_VERSION = 1;

    @Override
    protected String getAuthority() {
        return AUTHORITY;
    }

    public static Uri buildUri(String ... paths){
        Uri.Builder builder = BASE_CONTENT_URI.buildUpon();
        for (String path : paths){
            builder.appendPath(path);
        }
        return builder.build();
    }

    public static interface Path{
        String MOVIES = "movies";
    }

    @Table
    public class Movie {

        @Column(Column.FieldType.INTEGER)
        public static final String ID = "_id";

        @Column(Column.FieldType.TEXT)
        public static final String POSTER_PATH = "poster_path";

        @Column(Column.FieldType.TEXT)
        public static final String OVERVIEW = "overview";

        @Column(Column.FieldType.TEXT)
        public static final String RELEASE_DATE = "release_date";

        @Column(Column.FieldType.TEXT)
        public static final String TITLE = "title";

        @Column(Column.FieldType.TEXT)
        public static final String BACKDROP = "backdrop";

        @Column(Column.FieldType.REAL)
        public static final String POPULARITY = "popularity";

        @Column(Column.FieldType.INTEGER)
        public static final String VOTE_COUNT = "vote_count";

        @Column(Column.FieldType.REAL)
        public static final String VOTE_AVERAGE = "vote_average";

    }

    @Override
    protected int getSchemaVersion() {
        return SCHEMA_VERSION;
    }

}
